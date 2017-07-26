package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;

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

		addClickListener(clickEvent -> {
			if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
				getUI().getNavigator().navigateTo("network/" + network.getResource().getId() +
						"&compute/" + compute.getResource().getId());
			}
		});
	}

	public void refresh(IPNetworkDAO network) {
		this.network = network;
		setValues();
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
