package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkDetail extends Panel {
	private ComputeDAO compute;
	private IPNetworkDAO network;

	private Label netInterface;
	private Label macAddress;
	private Label label;
	private Label vlan;
	private Label ipAddress;
	private Label netAddress;
	private Label gateway;
	private Label allocation;

	public NetworkDetail(ComputeDAO compute, IPNetworkDAO network) {
		this.compute = compute;
		this.network = network;

		netInterface = new Label();
		macAddress = new Label();
		label = new Label();
		vlan = new Label();
		ipAddress = new Label();
		netAddress = new Label();
		gateway = new Label();
		allocation = new Label();

		setValues();
		VerticalLayout layout = new VerticalLayout(netInterface, macAddress, label, vlan, ipAddress, netAddress, gateway, allocation);
		setContent(layout);
		setCaption("Network: " + network.getResource().getId());

		Button detail = new Button("Detail", VaadinIcons.ELLIPSIS_DOTS_V);
		detail.addClickListener(clickEvent -> {
			try {
				getUI().getNavigator().navigateTo("network/" + network.getResource().getId() +
						"&compute/" + compute.getResource().getId() + "&endpoint/" +
						URLEncoder.encode(network.getEndpoint().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				//TODO
			}
		});

		layout.addComponent(detail);
		layout.setComponentAlignment(detail, Alignment.MIDDLE_RIGHT);
	}

	private void setValues() {
		netInterface.setValue("Interface: " + network.getInterface());
		macAddress.setValue("MAC Address: " + network.getMAC());
		label.setValue("Label: " + network.getLabel());
		vlan.setValue("VLAN: " + Integer.toString(network.getVlan()));
		ipAddress.setValue("IP address: " + network.getAddress());
		netAddress.setValue("Net address: " + network.getNetworkAddress());
		gateway.setValue("Gateway: " + network.getGateway());
		allocation.setValue("Allocation: " + network.getAllocation().toString());
	}
}
