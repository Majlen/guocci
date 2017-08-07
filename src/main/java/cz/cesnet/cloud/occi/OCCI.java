package cz.cesnet.cloud.occi;

import com.vaadin.server.VaadinSession;
import cz.cesnet.cloud.occi.api.Client;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.http.HTTPClient;
import cz.cesnet.cloud.occi.api.http.auth.HTTPAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.VOMSAuthentication;
import cz.cesnet.cloud.occi.core.*;
import cz.cesnet.cloud.occi.exception.AmbiguousIdentifierException;
import cz.cesnet.cloud.occi.infrastructure.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class OCCI {
	private HTTPAuthentication auth;
	private Model model;
	private URI endpoint;
	private Client client;

	public static OCCI getOCCI(VaadinSession session) throws CommunicationException {
		OCCI occi = null;
		if (session != null) {
			occi = session.getAttribute(OCCI.class);
		}

		if (occi == null) {
			occi = new OCCI();
			if (session != null) {
				session.setAttribute(OCCI.class, occi);
			}
		}

		return occi;
	}

	private OCCI() throws CommunicationException {
		this("/tmp/x509up_u1000", URI.create("https://carach5.ics.muni.cz:11443"));
	}

	private OCCI(String certificate, URI endpoint) throws CommunicationException {
		this.endpoint = endpoint;
		auth = new VOMSAuthentication(certificate);
		auth.setCAPath("/etc/grid-security/certificates");
		client = new HTTPClient(endpoint, auth);
		model = client.getModel();
	}

	public List<ComputeDAO> getComputes() throws CommunicationException {
		List<Entity> entities = client.describe(Compute.TERM_DEFAULT);
		List<ComputeDAO> computes = new ArrayList<>(entities.size());
		for (Entity e: entities) {
			computes.add(new ComputeDAO((Resource)e, this));
		}

		return computes;
	}

	public ComputeDAO getCompute(String id) throws CommunicationException {
		if (!id.contains("/")) {
			id = "/" + Compute.TERM_DEFAULT + "/" + id;
		}
		List<Entity> list = client.describe(URI.create(id));

		return new ComputeDAO((Resource)list.get(0), this);
	}

	public IPNetworkDAO getNetwork(String id, Link link) throws CommunicationException {
		if (!id.contains("/")) {
			id = "/" + Network.TERM_DEFAULT + "/" + id;
		}
		List<Entity> list = client.describe(URI.create(id));

		return new IPNetworkDAO((Resource)list.get(0), link, this);

	}

	public StorageDAO getStorage(String id, Link link) throws CommunicationException {
		if (!id.contains("/")) {
			id = "/" + Storage.TERM_DEFAULT + "/" + id;
		}
		List<Entity> list = client.describe(URI.create(id));

		return new StorageDAO((Resource)list.get(0), link, this);

	}

	public void performAction(URI identifier, ActionInstance action) throws CommunicationException {
		client.trigger(identifier, action);
	}

	public URI create(Entity entity) throws CommunicationException {
		return client.create(entity);
	}

	public void delete(URI identifier) throws CommunicationException {
		client.delete(identifier);
	}

	public void update(Entity entity) throws CommunicationException {
		client.update(entity);
	}

	public Model getModel() {
		return model;
	}

	public Mixin getMixin(String type, String id) throws AmbiguousIdentifierException {
		return model.findMixin(id, type);
	}

	public Mixin getMixin(URI id) throws AmbiguousIdentifierException {
		return model.findMixin(id);
	}
}

