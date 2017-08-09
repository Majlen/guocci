package cz.cesnet.cloud.vaadin.storage;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.commons.PolledView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageView extends VerticalLayout implements PolledView {
	private static final Logger logger = LoggerFactory.getLogger(StorageView.class);


	private StorageDAO storage;
	private StorageDetail storageDetail;
	private ComputeDAO parentResource;

	private Button online;
	private Button offline;

	public StorageView() {
		online = new Button("Online", VaadinIcons.ARROW_UP);
		online.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		offline = new Button("Offline", VaadinIcons.ARROW_DOWN);
		offline.addStyleName(ValoTheme.BUTTON_DANGER);

		online.addClickListener(clickEvent -> {
			try {
				storage.online();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while bringing storage online.", e.getMessage());
				logger.error("Cannot bring storage online.", e);
			}
		});

		offline.addClickListener(clickEvent -> {
			try {
				storage.offline();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while bringing storage offline.", e.getMessage());
				logger.error("Cannot bring storage offline.", e);
			}
		});

		HorizontalLayout bar = new HorizontalLayout(online, offline);

		storageDetail = new StorageDetail();

		addComponents(bar, storageDetail);
	}
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		ParameterParser parser = new ParameterParser(viewChangeEvent.getParameters());
		storage = null;

		try {
			final OCCI occi = OCCI.getOCCI(getSession());
			parentResource = occi.getCompute(parser.getOtherValues().get("compute"));
			storage = parentResource.getStorage(parser.getID());

			GUOCCI guocci = (GUOCCI) getUI();
			guocci.addButton(parentResource.getResource().getTitle(), "compute/" + parentResource.getResource().getId());
			guocci.addButton(storage.getResource().getTitle(), "storage/" + viewChangeEvent.getParameters());

			fillDetails();

			UI.getCurrent().addPollListener(pollEvent -> {
				try {
					storage = occi.getCompute(parentResource.getResource().getId()).getStorage(parser.getID());
					fillDetails();
				} catch (CommunicationException e) {
					Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
					logger.error("Cannot get storage detail.", e);
				}
			});
		} catch (CommunicationException e) {
			Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
			logger.error("Cannot get storage detail.", e);
		}

	}

	private void fillDetails() {
		storageDetail.refresh(storage);
		setButtons();
	}

	private void setButtons() {
		online.setEnabled(storage.getResource().containsAction(StorageDAO.STORAGE_ONLINE));
		offline.setEnabled(storage.getResource().containsAction(StorageDAO.STORAGE_OFFLINE));
	}

	@Override
	public void pollMethod() {
		try {
			OCCI occi = OCCI.getOCCI(getSession());
			parentResource = occi.getCompute(parentResource.getResource().getLocation());
			storage = parentResource.getStorage(storage.getResource().getId());
			fillDetails();
		} catch (CommunicationException e) {
			Notify.errNotify("Error getting resource from OCCI.", e.getMessage());
			logger.error("Cannot get storage detail.", e);
		}
	}
}
