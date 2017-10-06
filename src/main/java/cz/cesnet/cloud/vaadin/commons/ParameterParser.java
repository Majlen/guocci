package cz.cesnet.cloud.vaadin.commons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParameterParser {
	private String params;
	private boolean id;

	public ParameterParser(String params) {
		this(params, true);
	}

	public ParameterParser(String params, boolean id) {
		this.params = params;
		this.id = id;
	}

	public String getID() {
		if (!id) {
			return null;
		}

		int index = params.indexOf('&');
		if (index != -1) {
			return params.substring(0, index);
		} else {
			return params;
		}
	}

	public Map<String, String> getOtherValues() {
		int index = 0;
		if (id) {
			index = params.indexOf('&');
		}

		if (index != -1) {
			Map<String, String> out = new HashMap<>();
			String[] pairs = params.substring(index == 0 ? index : index + 1).split("&");
			for (String pair : pairs) {
				boolean lastSlash = false;
				if (pair.charAt(pair.length() - 1) == '/') {
					lastSlash = true;
				}

				String[] keyValue = pair.split("/");
				if (lastSlash) {
					keyValue[keyValue.length - 1] +=  "/";
				}

				if (keyValue.length > 2) {
					String[] subArray = Arrays.copyOfRange(keyValue, 1, keyValue.length);
					out.put(keyValue[0], String.join("/", subArray));
				} else {
					out.put(keyValue[0], keyValue[1]);
				}
			}

			return out;
		} else {
			return null;
		}
	}
}
