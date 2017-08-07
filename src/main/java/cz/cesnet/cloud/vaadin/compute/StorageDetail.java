package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;

public class StorageDetail extends Panel {
	private ComputeDAO compute;
	private StorageDAO storage;

	private Label deviceID;
	private Label size;
	private Label mountPoint;

	public StorageDetail(ComputeDAO compute, StorageDAO storage) {
		this.compute = compute;
		this.storage = storage;

		deviceID = new Label();
		size = new Label();
		mountPoint = new Label();

		setValues();
		VerticalLayout layout = new VerticalLayout(deviceID, size, mountPoint);
		setContent(layout);
		setCaption("Storage: " + storage.getResource().getId());

		Button detail = new Button("Detail", VaadinIcons.ELLIPSIS_DOTS_V);
		detail.addClickListener(clickEvent -> {
			getUI().getNavigator().navigateTo("storage/" + storage.getResource().getId() +
					"&compute/" + compute.getResource().getId());
		});
	}

	public void refresh(StorageDAO storage) {
		this.storage = storage;
		setValues();
	}

	private void setValues() {
		deviceID.setValue("Device: " + storage.getDeviceID());
		size.setValue("Size: " + storage.getSize());
		mountPoint.setValue("Mountpoint: " + storage.getMountpoint());
	}
}
