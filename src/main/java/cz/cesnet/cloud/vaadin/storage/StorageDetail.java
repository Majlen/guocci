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

import java.util.HashMap;
import java.util.Map;

public class StorageDetail extends FormLayout {
	private StorageDAO storage;

	private TextField size;
	private TextField state;
	private Button apply;

	public StorageDetail() {
		this(false);
	}

	public StorageDetail(boolean create) {
		size = new TextField("Size");
		state = new TextField("State");
		state.setReadOnly(true);

		if (!create) {
			apply = new Button("Apply", VaadinIcons.CHECK);
			apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
			apply.addClickListener(clickEvent -> updateStorage());

			addComponents(size, state, apply);
		} else {
			addComponents(size);
		}
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
			new Notification("Failed to set attributes!", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			System.out.println(e.getMessage());
		}
	}
}
