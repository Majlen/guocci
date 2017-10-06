package cz.cesnet.cloud.occi;

import com.vaadin.server.VaadinSession;
import cz.cesnet.cloud.Configuration;
import cz.cesnet.cloud.occi.api.Client;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.http.HTTPClient;
import cz.cesnet.cloud.occi.api.http.auth.HTTPAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.VOMSAuthentication;
import cz.cesnet.cloud.occi.core.*;
import cz.cesnet.cloud.occi.exception.AmbiguousIdentifierException;
import cz.cesnet.cloud.occi.infrastructure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class OCCI {
	private static final Logger logger = LoggerFactory.getLogger(OCCI.class);

	private HTTPAuthentication auth;
	private Model model;
	private URI endpoint;
	private Client client;
	private static String certPath = "/tmp/x509up_u1000";

	public static Map<String, OCCI> getOCCI(VaadinSession session, Configuration configuration) throws CommunicationException {
		Map<String, OCCI> occiMap = null;

		if (session != null) {
			occiMap = (Map<String, OCCI>)session.getAttribute("occi_clients");
		}

		if (occiMap == null) {
			occiMap = new HashMap<>();

			for (URI uri: configuration.getSourceURI()) {
				logger.debug("Getting OCCI client {} for user {}.", uri, certPath);
				//TODO: check for Communication exception (if one endpoint fails, everything fails)
				OCCI occi = new OCCI(certPath, uri, configuration.getAuthCAPath());
				occiMap.put(occi.getEndpoint().toString(), occi);
			}

			if (session != null) {
				session.setAttribute("occi_clients", occiMap);
			}
		}

		return occiMap;
	}

	private OCCI(String certificate, URI endpoint, String CAPath) throws CommunicationException {
		this.endpoint = endpoint;
		auth = new VOMSAuthentication(certificate);
		auth.setCAPath(CAPath);
		client = new HTTPClient(endpoint, auth);
		model = client.getModel();
	}

	public URI getEndpoint() {
		return endpoint;
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

	public Set<ResourceTemplateMixin> getResTpls() {
		Set<Mixin> mixins = client.getModel().getMixins();

		Set<ResourceTemplateMixin> resTpls = new HashSet<>();

		for (Mixin m: mixins) {
			for (Mixin n : m.getRelations()) {
				if (n.getTerm().equals("resource_tpl")) {
					ResourceTemplateMixin resTpl = new ResourceTemplateMixin(m, n);
					resTpls.add(resTpl);
				}
			}
		}

		return resTpls;
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

