package cz.cesnet.cloud.vaadin.network;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.*;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.ReadOnlyTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NetworkDetail extends FormLayout {
	private static final Logger logger = LoggerFactory.getLogger(NetworkDetail.class);

	private IPNetworkDAO network;

	private TextField vlan;
	private TextField label;
	private TextField address;
	private TextField gateway;
	private TextField allocation;
	private TextField state;
	private Button apply;

	public NetworkDetail() {
		super();
		vlan = new ReadOnlyTextField("VLAN");
		label = new ReadOnlyTextField("Label");
		address = new ReadOnlyTextField("Address");
		gateway = new ReadOnlyTextField("Gateway");
		allocation = new ReadOnlyTextField("Allocation");
		state = new ReadOnlyTextField("State");

		apply = new Button("Apply", VaadinIcons.CHECK);
		apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
		apply.addClickListener(clickEvent -> updateNetwork());

		addComponents(label, address, gateway, vlan, allocation, state, apply);
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
			Notify.errNotify("Failed to set attributes.", e.getMessage());
			logger.error("Failed to set attributes to network.", e);
		}
	}
}
