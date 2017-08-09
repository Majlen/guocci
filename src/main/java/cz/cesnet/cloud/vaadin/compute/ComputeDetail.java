package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.Compute;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.vaadin.commons.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ComputeDetail extends FormLayout {
	private static final Logger logger = LoggerFactory.getLogger(ComputeDetail.class);

	private ComputeDAO compute;

	private TextField cores;
	private TextField hostname;
	private TextField speed;
	private TextField memory;
	private TextField state;
	private Button apply;

	public ComputeDetail() {
		super();
		cores = new TextField("Cores");
		hostname = new TextField("Hostname");
		speed = new TextField("Speed");
		memory = new TextField("Memory");
		state = new TextField("State");
		state.setReadOnly(true);

		apply = new Button("Apply", VaadinIcons.CHECK);
		apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
		apply.addClickListener(clickEvent -> updateCompute());

		addComponents(cores, hostname, speed, memory, state, apply);
	}

	public void refresh(ComputeDAO compute) {
		this.compute = compute;
		setValues(compute);
	}

	private void setValues(ComputeDAO compute) {
		cores.setPlaceholder(Integer.toString(compute.getCores()));
		hostname.setPlaceholder(compute.getHostname());
		speed.setPlaceholder(Double.toString(compute.getSpeed()));
		memory.setPlaceholder(Double.toString(compute.getMemory()));
		state.setValue(compute.getState().toString());
	}

	private void updateCompute() {
		Map<String, String> attrMap = new HashMap<>();

		if (!cores.isEmpty()) {
			attrMap.put(Compute.CORES_ATTRIBUTE_NAME, cores.getValue());
		}
		if (!hostname.isEmpty()) {
			attrMap.put(Compute.HOSTNAME_ATTRIBUTE_NAME, hostname.getValue());
		}
		if (!speed.isEmpty()) {
			attrMap.put(Compute.SPEED_ATTRIBUTE_NAME, speed.getValue());
		}
		if (!memory.isEmpty()) {
			attrMap.put(Compute.MEMORY_ATTRIBUTE_NAME, memory.getValue());
		}

		try {
			compute.setOptions(attrMap);
		} catch (CommunicationException | InvalidAttributeValueException e) {
			Notify.errNotify("Failed to set attributes.", e.getMessage());
			logger.error("Failed to set attributes to compute.", e);
		}
	}
}
