package cz.cesnet.cloud.vaadin.network;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.*;

import java.util.HashMap;
import java.util.Map;

public class NetworkDetail extends FormLayout {
	private IPNetworkDAO network;

	private TextField vlan;
	private TextField label;
	private TextField address;
	private TextField gateway;
	private TextField allocation;
	private TextField state;
	private Button apply;

	public NetworkDetail() {
		this(false);
	}

	public NetworkDetail(boolean create) {
		vlan = new TextField("Size");
		label = new TextField("Label");
		address = new TextField("Address");
		gateway = new TextField("Gateway");
		allocation = new TextField("Allocation");
		state = new TextField("State");
		state.setReadOnly(true);

		if (!create) {
			apply = new Button("Apply", VaadinIcons.CHECK);
			apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
			apply.addClickListener(clickEvent -> updateNetwork());

			addComponents(vlan, state, apply);
		} else {
			addComponents(vlan);
		}

	}

	public void refresh(IPNetworkDAO network) {
		this.network = network;
		setValues();
	}

	private void setValues() {
		vlan.setPlaceholder(Integer.toString(network.getVlan()));
		label.setPlaceholder(network.getLabel());
		address.setPlaceholder(network.getAddress());
		gateway.setPlaceholder(network.getGateway());
		allocation.setPlaceholder(network.getAllocation().toString());
		state.setValue(network.getState().toString());
	}

	private void updateNetwork() {
		Map<String, String> attrMap = new HashMap<>();

		if (!vlan.isEmpty()) {
			attrMap.put(Network.VLAN_ATTRIBUTE_NAME, vlan.getValue());
		}
		if (!label.isEmpty()) {
			attrMap.put(Network.LABEL_ATTRIBUTE_NAME, label.getValue());
		}
		//TODO: also update link
		/*if (!address.isEmpty()) {
			attrMap.put(IPNetworkInterface.ADDRESS_ATTRIBUTE_NAME, address.getValue());
		}*/
		if (!gateway.isEmpty()) {
			attrMap.put(IPNetwork.GATEWAY_ATTRIBUTE_NAME, gateway.getValue());
		}

		try {
			network.setOptions(attrMap);
		} catch (CommunicationException | InvalidAttributeValueException e) {
			new Notification("Failed to set attributes!", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			System.out.println(e.getMessage());
		}
	}
}
