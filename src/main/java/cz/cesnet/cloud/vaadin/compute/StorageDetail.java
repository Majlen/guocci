package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
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

		addClickListener(clickEvent -> {
			if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
				getUI().getNavigator().navigateTo("storage/" + storage.getResource().getId() +
						"&compute/" + compute.getResource().getId());
			}
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
