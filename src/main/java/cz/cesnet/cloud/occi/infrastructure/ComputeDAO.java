package cz.cesnet.cloud.occi.infrastructure;

import cz.cesnet.cloud.occi.Model;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.core.*;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.enumeration.Architecture;
import cz.cesnet.cloud.occi.infrastructure.enumeration.ComputeState;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComputeDAO {
	public static final String COMPUTE_ACTION_SCHEMA = "http://schemas.ogf.org/occi/infrastructure/compute/action";
	public static final String COMPUTE_START = COMPUTE_ACTION_SCHEMA + "#start";
	public static final String COMPUTE_STOP = COMPUTE_ACTION_SCHEMA + "#stop";
	public static final String COMPUTE_RESTART = COMPUTE_ACTION_SCHEMA + "#restart";
	public static final String COMPUTE_SUSPEND = COMPUTE_ACTION_SCHEMA + "#suspend";

	private Resource resource;
	private OCCI occi;

	public ComputeDAO(Resource resource, OCCI occi) {
		this.resource = resource;
		this.occi = occi;
	}

	public Architecture getArchitecture() {
		switch (resource.getValue(Compute.ARCHITECTURE_ATTRIBUTE_NAME)) {
			case "x86":
				return Architecture.X_86;
			case "x64":
				return Architecture.X_64;
			default:
				//Make the default saner
				return Architecture.X_64;
		}
	}

	public int getCores() {
		try {
			return Integer.parseInt(resource.getValue(Compute.CORES_ATTRIBUTE_NAME));
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public String getHostname() {
		String out = resource.getValue(Compute.HOSTNAME_ATTRIBUTE_NAME);
		if (out == null) {
			return "";
		} else {
			return out;
		}
	}

	public double getMemory() {
		try {
			return Double.parseDouble(resource.getValue(Compute.MEMORY_ATTRIBUTE_NAME));
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public double getSpeed() {
		try {
			return Double.parseDouble(resource.getValue(Compute.SPEED_ATTRIBUTE_NAME));
		} catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	public ComputeState getState() {
		switch (resource.getValue(Compute.STATE_ATTRIBUTE_NAME)) {
			case "active":
				return ComputeState.ACTIVE;
			case "inactive":
				return ComputeState.INACTIVE;
			case "suspended":
				return ComputeState.SUSPENDED;
			default:
				//Saner default
				return ComputeState.INACTIVE;
		}
	}

	public Resource getResource() {
		return resource;
	}

	public List<StorageDAO> getStorages() throws CommunicationException {
		//Need to mix with Link
		List<StorageDAO> storages = new LinkedList<>();

		Set<Link> links = resource.getLinks(StorageLink.TERM_DEFAULT);
		for (Link link: links) {
			storages.add(occi.getStorage(link.getTarget(), link));
		}

		return storages;
	}

	public StorageDAO getStorage(String id) throws CommunicationException {
		List<StorageDAO> storages = getStorages();

		for (StorageDAO s: storages) {
			if (s.getResource().getId().equals(id)) {
				return s;
			}
		}

		return null;
	}

	public List<IPNetworkDAO> getNetworks() throws CommunicationException {
		//Need to mix with Link
		List<IPNetworkDAO> networks = new LinkedList<>();

		Set<Link> links = resource.getLinks(NetworkInterface.TERM_DEFAULT);
		for (Link link: links) {
			networks.add(occi.getNetwork(link.getTarget(), link));
		}

		return networks;
	}

	public IPNetworkDAO getNetwork(String id) throws CommunicationException {
		List<IPNetworkDAO> networks = getNetworks();

		for (IPNetworkDAO n: networks) {
			if (n.getResource().getId().equals(id)) {
				return n;
			}
		}

		return null;
	}

	public void start() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(COMPUTE_START));
		occi.performAction(URI.create(resource.getLocation()), action);
	}

	public void stop() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(COMPUTE_STOP));
		occi.performAction(URI.create(resource.getLocation()), action);
	}

	public void suspend() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(COMPUTE_SUSPEND));
		occi.performAction(URI.create(resource.getLocation()), action);
	}

	public void restart() throws CommunicationException {
		ActionInstance action = new ActionInstance(resource.getAction(COMPUTE_RESTART));
		occi.performAction(URI.create(resource.getLocation()), action);
	}

	public void remove() throws CommunicationException {
		occi.delete(URI.create(resource.getLocation()));
	}

	public void setOptionMixin(Mixin mixin, Mixin parentMixin) throws CommunicationException {
		Mixin toRemove = null;
		Set<Mixin> mixins = resource.getMixins();
		Model model = occi.getModel();

		for (Mixin m: mixins) {
			Mixin fromModel = model.getMixin(m.getIdentifier());
			for (Mixin n: fromModel.getRelations()) {
				if (n.getTerm().equals(parentMixin.getTerm())) {
					toRemove = n;
					break;
				}
			}
			if (toRemove != null) {
				break;
			}
		}


		resource.removeMixin(toRemove);
		resource.addMixin(mixin);
		occi.update(resource);
	}

	public void setOptions(Map<String, String> attributes) throws InvalidAttributeValueException, CommunicationException {
		//No attribute can be set multiple times in Compute
		attributes.forEach((attribute, value) -> resource.removeAttribute(attribute));

		resource.addAttributes(attributes);
		occi.update(resource);
	}
}
