package cz.cesnet.cloud.occi.infrastructure;

import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.core.ActionInstance;
import cz.cesnet.cloud.occi.core.Link;
import cz.cesnet.cloud.occi.core.Resource;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.enumeration.StorageLinkState;
import cz.cesnet.cloud.occi.infrastructure.enumeration.StorageState;

import java.net.URI;
import java.util.Map;

public class StorageDAO {
	public static final String STORAGE_ACTION_SCHEMA = "http://schemas.ogf.org/occi/infrastructure/storage/action";
	public static final String STORAGE_ONLINE = STORAGE_ACTION_SCHEMA + "#online";
	public static final String STORAGE_OFFLINE = STORAGE_ACTION_SCHEMA + "#offline";

	private Resource resource;
	private Link link;

	public StorageDAO(Resource resource, Link link) {
		this.resource = resource;
		this.link = link;
	}

	public double getSize() {
		try {
			return Double.parseDouble(resource.getValue(Storage.SIZE_ATTRIBUTE_NAME));
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public StorageState getState() {
		switch (resource.getValue(Storage.STATE_ATTRIBUTE_NAME)) {
			case "online":
				return StorageState.ONLINE;
			case "offline":
				return StorageState.OFFLINE;
			case "backup":
				return StorageState.BACKUP;
			case "snapshot":
				return StorageState.SNAPSHOT;
			case "resize":
				return StorageState.RESIZE;
			case "degraded":
				return StorageState.DEGRADED;
			default:
				// Saner default
				return StorageState.OFFLINE;
		}
	}

	public String getDeviceID() {
		String out = link.getValue(StorageLink.DEVICE_ID_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public String getMountpoint() {
		String out = link.getValue(StorageLink.MOUNTPOINT_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public StorageLinkState getLinkState() {
		switch (resource.getValue(StorageLink.STATE_ATTRIBUTE_NAME)) {
			case "active":
				return StorageLinkState.ACTIVE;
			case "inactive":
				return StorageLinkState.INACTIVE;
			default:
				return StorageLinkState.INACTIVE;
		}
	}

	public Resource getResource() {
		return resource;
	}

	public void online() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(STORAGE_ONLINE));
		OCCI.getOCCI().performAction(URI.create(resource.getLocation()), action);
	}

	public void offline() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(STORAGE_OFFLINE));
		OCCI.getOCCI().performAction(URI.create(resource.getLocation()), action);
	}

	public void setOptions(Map<String, String> attributes) throws InvalidAttributeValueException, CommunicationException {
		//No attribute can be set multiple times in Storage
		attributes.forEach((attribute, value) -> resource.removeAttribute(attribute));

		resource.addAttributes(attributes);
		OCCI.getOCCI().update(resource);
	}
}
