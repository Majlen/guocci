package cz.cesnet.cloud.vaadin.network;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
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
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.compute.ComputeView;

public class NetworkView extends VerticalLayout implements View {
	private IPNetworkDAO network;
	private NetworkDetail networkDetail;

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
				System.out.println(e.getMessage());
			}
		});

		down.addClickListener(clickEvent -> {
			try {
				network.down();
			} catch (CommunicationException e) {
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
			ComputeDAO parent = occi.getCompute(parser.getOtherValues().get("compute"));
			network = parent.getNetwork(parser.getID());

			GUOCCI guocci = (GUOCCI) getUI();
			guocci.addButton(parent.getResource().getTitle(), "compute/" + parent.getResource().getId());
			guocci.addButton(network.getResource().getTitle(), "network/" + viewChangeEvent.getParameters());

			fillDetails(network);

			UI.getCurrent().addPollListener(pollEvent -> {
				try {
					network = occi.getCompute(parent.getResource().getId()).getNetwork(parser.getID());
					fillDetails(network);
				} catch (CommunicationException e) {
					System.out.println(e.getMessage());
				}
			});
		} catch (CommunicationException e) {
			System.out.println(e.getMessage());
		}

	}

	private void fillDetails(IPNetworkDAO network) {
		networkDetail.refresh(network);
	}
}
