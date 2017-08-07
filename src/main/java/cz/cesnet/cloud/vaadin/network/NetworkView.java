package cz.cesnet.cloud.vaadin.network;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.commons.PolledView;

public class NetworkView extends VerticalLayout implements PolledView {
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
				System.out.println(e.getMessage());
			}
		});

		down.addClickListener(clickEvent -> {
			try {
				network.down();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while bringing interface down.", e.getMessage());
				System.out.println(e.getMessage());
			}
		});

		HorizontalLayout bar = new HorizontalLayout(up, down);

		networkDetail = new NetworkDetail();

		addComponents(bar, networkDetail);
	}
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		ParameterParser parser = new ParameterParser(viewChangeEvent.getParameters());
		network = null;

		try {
			final OCCI occi = OCCI.getOCCI(getSession());
			parentResource = occi.getCompute(parser.getOtherValues().get("compute"));
			network = parentResource.getNetwork(parser.getID());

			GUOCCI guocci = (GUOCCI) getUI();
			guocci.addButton(parentResource.getResource().getTitle(), "compute/" + parentResource.getResource().getId());
			guocci.addButton(network.getResource().getTitle(), "network/" + viewChangeEvent.getParameters());

			fillDetails();

			UI.getCurrent().addPollListener(pollEvent -> {
				try {
					network = occi.getCompute(parentResource.getResource().getId()).getNetwork(parser.getID());
					fillDetails();
				} catch (CommunicationException e) {
					Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
					System.out.println(e.getMessage());
				}
			});
		} catch (CommunicationException e) {
			Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
			System.out.println(e.getMessage());
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
			OCCI occi = OCCI.getOCCI(getSession());
			parentResource = occi.getCompute(parentResource.getResource().getLocation());
			network = parentResource.getNetwork(network.getResource().getId());
			fillDetails();
		} catch (CommunicationException e) {
			Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
}
