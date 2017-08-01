package cz.cesnet.cloud.vaadin.storage;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
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
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.compute.ComputeView;

public class StorageView extends VerticalLayout implements View {
	private StorageDAO storage;
	private StorageDetail storageDetail;

	private Button online;
	private Button offline;

	public StorageView() {
		online = new Button("Start", VaadinIcons.PLAY);
		online.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		offline = new Button("Stop", VaadinIcons.STOP);
		offline.addStyleName(ValoTheme.BUTTON_DANGER);

		online.addClickListener(clickEvent -> {
			try {
				storage.online();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		offline.addClickListener(clickEvent -> {
			try {
				storage.offline();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
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
			ComputeDAO parent = occi.getCompute(parser.getOtherValues().get("compute"));
			storage = parent.getStorage(parser.getID());

			GUOCCI guocci = (GUOCCI) getUI();
			guocci.addButton(parent.getResource().getTitle(), "compute/" + parent.getResource().getId());
			guocci.addButton(storage.getResource().getTitle(), "storage/" + viewChangeEvent.getParameters());

			fillDetails(storage);

			UI.getCurrent().addPollListener(pollEvent -> {
				try {
					storage = occi.getCompute(parent.getResource().getId()).getStorage(parser.getID());
					fillDetails(storage);
				} catch (CommunicationException e) {
					System.out.println(e.getMessage());
				}
			});
		} catch (CommunicationException e) {
			System.out.println(e.getMessage());
		}

	}

	private void fillDetails(StorageDAO storage) {
		storageDetail.refresh(storage);
	}
}
