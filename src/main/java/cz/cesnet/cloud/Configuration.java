package cz.cesnet.cloud;

import java.net.URI;
import java.util.Arrays;
import java.util.Properties;

public class Configuration {
	private final static String RESOURCE_URI_PROPERTY = "guocci.resource.uri";
	private final static String AUTH_CA_PATH_PROPERTY = "guocci.occi.x509.capath";
	private final static String CHOOSER_URI = "guocci.chooser.uri";

	private Properties properties;

	public Configuration(Properties properties) {
		this.properties = properties;
	}

	public URI[] getSourceURI() {
		String[] strArray = properties.getProperty(RESOURCE_URI_PROPERTY).split(",");

		return Arrays.stream(strArray)
				.map(URI::create)
				.toArray(URI[]::new);
	}

	public String getAuthCAPath() {
		return properties.getProperty(AUTH_CA_PATH_PROPERTY);
	}

	public String getChooserURI() {
		return properties.getProperty(CHOOSER_URI);
	}
}
