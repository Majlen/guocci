package cz.cesnet.cloud.vaadin.storage;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.Storage;
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;
import cz.cesnet.cloud.vaadin.commons.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StorageDetail extends FormLayout {
	private static final Logger logger = LoggerFactory.getLogger(StorageDetail.class);
	private StorageDAO storage;

	private TextField size;
	private TextField state;
	private Button apply;

	public StorageDetail() {
		super();
		size = new TextField("Size");
		state = new TextField("State");
		state.setReadOnly(true);

		apply = new Button("Apply", VaadinIcons.CHECK);
		apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
		apply.addClickListener(clickEvent -> updateStorage());

		addComponents(size, state, apply);
	}

	public void refresh(StorageDAO storage) {
		this.storage = storage;
		setValues();
	}

	private void setValues() {
		size.setPlaceholder(Double.toString(storage.getSize()));
		state.setValue(storage.getState().toString());
	}

	private void updateStorage() {
		Map<String, String> attrMap = new HashMap<>();

		if (!size.isEmpty()) {
			attrMap.put(Storage.SIZE_ATTRIBUTE_NAME, size.getValue());
		}

		try {
			storage.setOptions(attrMap);
		} catch (CommunicationException | InvalidAttributeValueException e) {
			Notify.errNotify("Failed to set attributes.", e.getMessage());
			logger.error("Failed to set attributes to storage.", e);
		}
	}
}
