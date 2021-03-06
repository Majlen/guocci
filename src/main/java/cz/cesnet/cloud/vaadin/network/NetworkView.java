package cz.cesnet.cloud.vaadin.network;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.Configuration;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.commons.PolledView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

public class NetworkView extends VerticalLayout implements PolledView {
	private static final Logger logger = LoggerFactory.getLogger(NetworkView.class);

	private OCCI occi;
	private IPNetworkDAO network;
	private NetworkDetail networkDetail;
	private ComputeDAO parentResource;

	private Button up;
	private Button down;

	public NetworkView() {
		up = new Button("Up", VaadinIcons.ARROW_UP);
		up.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		down = new Button("Down", VaadinIcons.ARROW_DOWN);
		down.addStyleName(ValoTheme.BUTTON_DANGER);

		up.addClickListener(clickEvent -> {
			try {
				network.up();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while bringing interface up.", e.getMessage());
				logger.error("Cannot bring interface up.", e);
			}
		});

		down.addClickListener(clickEvent -> {
			try {
				network.down();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while bringing interface down.", e.getMessage());
				logger.error("Cannot bring interface down.", e);
			}
		});

		HorizontalLayout bar = new HorizontalLayout(up, down);

		networkDetail = new NetworkDetail();

		addComponents(bar, networkDetail);
	}
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		Configuration configuration = (Configuration) VaadinServlet.getCurrent().getServletContext().getAttribute("configuration");
		ParameterParser parser = new ParameterParser(viewChangeEvent.getParameters());
		network = null;

		try {
			Map<String, OCCI> occiMap = OCCI.getOCCI(getSession(), configuration);
			occi = occiMap.get(URLDecoder.decode(parser.getOtherValues().get("endpoint"), "UTF-8"));

			parentResource = occi.getCompute(parser.getOtherValues().get("compute"));
			network = parentResource.getNetwork(parser.getID());

			GUOCCI guocci = (GUOCCI) getUI();
			guocci.addButton(parentResource.getResource().getTitle(), "compute/" + parentResource.getResource().getId() +
					"&endpoint/" + URLEncoder.encode(network.getEndpoint().toString(), "UTF-8"));
			guocci.addButton(network.getResource().getTitle(), "network/" + viewChangeEvent.getParameters());

			fillDetails();

			UI.getCurrent().addPollListener(pollEvent -> {
				try {
					network = occi.getCompute(parentResource.getResource().getId()).getNetwork(parser.getID());
					fillDetails();
				} catch (CommunicationException e) {
					Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
					logger.error("Cannot get network detail.", e);
				}
			});
		} catch (CommunicationException e) {
			Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
			logger.error("Cannot get network detail.", e);
		} catch (UnsupportedEncodingException e) {
			//Should not happen
		}

	}

	private void fillDetails() {
		networkDetail.refresh(network);
		setButtons();
	}

	private void setButtons() {
		up.setEnabled(network.getResource().containsAction(IPNetworkDAO.NETWORK_UP));
		down.setEnabled(network.getResource().containsAction(IPNetworkDAO.NETWORK_DOWN));
	}

	@Override
	public void pollMethod() {
		try {
			parentResource = occi.getCompute(parentResource.getResource().getLocation());
			network = parentResource.getNetwork(network.getResource().getId());
			fillDetails();
		} catch (CommunicationException e) {
			Notify.warnNotify("Error getting resource from OCCI.", e.getMessage());
			logger.error("Cannot get network detail.", e);
		}
	}
}
