package cz.cesnet.cloud.occi.infrastructure;

import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.core.ActionInstance;
import cz.cesnet.cloud.occi.core.Link;
import cz.cesnet.cloud.occi.core.Resource;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.enumeration.Allocation;
import cz.cesnet.cloud.occi.infrastructure.enumeration.NetworkState;

import java.net.URI;
import java.util.Map;

public class IPNetworkDAO {
	public static final String NETWORK_ACTION_SCHEMA = "http://schemas.ogf.org/occi/infrastructure/network/action";
	public static final String NETWORK_UP = NETWORK_ACTION_SCHEMA + "#up";
	public static final String NETWORK_DOWN = NETWORK_ACTION_SCHEMA + "#down";

	private Resource resource;
	private Link link;

	public IPNetworkDAO(Resource resource, Link link) {
		this.resource = resource;
		this.link = link;
	}

	public String getLabel() {
		String out = resource.getValue(Network.LABEL_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public int getVlan() {
		try {
			return Integer.parseInt(resource.getValue(Network.VLAN_ATTRIBUTE_NAME));
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public NetworkState getState() {
		switch (resource.getValue(Network.STATE_ATTRIBUTE_NAME)) {
			case "active":
				return NetworkState.ACTIVE;
			case "inactive":
				return NetworkState.INACTIVE;
			default:
				//Saner default
				return NetworkState.INACTIVE;
		}
	}

	public String getNetworkAddress() {
		String out = resource.getValue(IPNetwork.ADDRESS_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public String getGateway() {
		String out = link.getValue(IPNetworkInterface.GATEWAY_ATTRIBUTE_NAME);
		if (out == null) {
			out = resource.getValue(IPNetwork.GATEWAY_ATTRIBUTE_NAME);
			if (out == null) {
				return "";
			} else {
				return out;
			}
		} else {
			return out;
		}
	}

	public Allocation getAllocation() {
		switch (resource.getValue(IPNetwork.ALLOCATION_ATTRIBUTE_NAME)) {
			case "dynamic":
				return Allocation.DYNAMIC;
			case "static":
				return Allocation.STATIC;
			default:
				switch (link.getValue(IPNetworkInterface.ALLOCATION_ATTRIBUTE_NAME)) {
					case "dynamic":
						return Allocation.DYNAMIC;
					case "static":
						return Allocation.STATIC;
					default:
						// Saner default
						return Allocation.DYNAMIC;
				}
		}
	}

	public String getInterface() {
		String out = link.getValue(NetworkInterface.INTERFACE_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public String getMAC() {
		String out = link.getValue(NetworkInterface.MAC_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public NetworkState getLinkState() {
		switch (link.getValue(NetworkInterface.STATE_ATTRIBUTE_NAME)) {
			case "active":
				return NetworkState.ACTIVE;
			case "inactive":
				return NetworkState.INACTIVE;
			default:
				//Saner default
				return NetworkState.INACTIVE;
		}
	}

	public String getAddress() {
		String out = link.getValue(IPNetworkInterface.ADDRESS_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public Resource getResource() {
		return resource;
	}

	public void up() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(NETWORK_UP));
		OCCI.getOCCI().performAction(URI.create(resource.getLocation()), action);
	}

	public void down() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(NETWORK_DOWN));
		OCCI.getOCCI().performAction(URI.create(resource.getLocation()), action);
	}

	public void setOptions(Map<String, String> attributes) throws InvalidAttributeValueException, CommunicationException {
		//No attribute can be set multiple times in Storage
		attributes.forEach((attribute, value) -> resource.removeAttribute(attribute));

		resource.addAttributes(attributes);
		OCCI.getOCCI().update(resource);
	}
}
