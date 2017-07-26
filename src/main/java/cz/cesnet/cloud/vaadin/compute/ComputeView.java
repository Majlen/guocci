package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;

import java.util.List;

public class ComputeView extends VerticalLayout implements View {
	private ComputeDAO compute;
	private ComputeDetail computeDetail;
	private VerticalLayout linksDetail;

	private Button start;
	private Button stop;
	private Button restart;
	private Button suspend;
	private Button remove;

	public ComputeView() {
		start = new Button("Start", VaadinIcons.PLAY);
		start.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		stop = new Button("Stop", VaadinIcons.STOP);
		stop.addStyleName(ValoTheme.BUTTON_DANGER);
		restart = new Button("Restart", VaadinIcons.REFRESH);
		restart.addStyleName(ValoTheme.BUTTON_DANGER);
		suspend = new Button("Suspend", VaadinIcons.PAUSE);
		remove = new Button("Remove", VaadinIcons.TRASH);
		remove.addStyleName(ValoTheme.BUTTON_DANGER);

		start.addClickListener(clickEvent -> {
			try {
				compute.start();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		stop.addClickListener(clickEvent -> {
			try {
				compute.stop();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		restart.addClickListener(clickEvent -> {
			try {
				compute.restart();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		suspend.addClickListener(clickEvent -> {
			try {
				compute.suspend();
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		remove.addClickListener(clickEvent -> {
			try {
				compute.remove();
				//TODO: Warning window?
				getUI().getNavigator().navigateTo("");
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});

		HorizontalLayout bar = new HorizontalLayout(start, stop, restart, suspend, remove);

		computeDetail = new ComputeDetail();
		linksDetail = new VerticalLayout();

		HorizontalLayout content = new HorizontalLayout(computeDetail, linksDetail);

		addComponents(bar, content);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		OCCI occi = OCCI.getOCCI();
		compute = null;
		try {
			compute = OCCI.getOCCI().getCompute(viewChangeEvent.getParameters());
		} catch (CommunicationException e) {
			System.out.println(e.getMessage());
		}

		GUOCCI guocci = (GUOCCI) getUI();
		guocci.addButton(compute.getResource().getTitle(), "compute/" + viewChangeEvent.getParameters());

		try {
			fillEverything(compute);
		} catch (CommunicationException e) {
			System.out.println(e.getMessage());
		}

		UI.getCurrent().addPollListener(pollEvent -> {
			try {
				compute = occi.getCompute(compute.getResource().getLocation());
				fillCompute(compute);
			} catch (CommunicationException e) {
				System.out.println(e.getMessage());
			}
		});
	}

	private void fillEverything(ComputeDAO compute) throws CommunicationException {
		linksDetail.removeAllComponents();

		fillCompute(compute);
		fillStorages(compute.getStorages());
		fillNetworks(compute.getNetworks());
	}

	private void fillCompute(ComputeDAO compute) {
		computeDetail.refresh(compute);
		setButtons(compute);
	}

	private void fillStorages(List<StorageDAO> storages) {
		for (StorageDAO storage: storages) {
			linksDetail.addComponent(new StorageDetail(compute, storage));
		}
	}

	private void fillNetworks(List<IPNetworkDAO> networks) {
		for (IPNetworkDAO network: networks) {
			linksDetail.addComponent(new NetworkDetail(compute, network));
		}
	}

	private void setButtons(ComputeDAO compute) {
		start.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_START));
		stop.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_STOP));
		restart.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_RESTART));
		suspend.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_SUSPEND));
	}

	public ComputeDAO getCompute() {
		return compute;
	}
}
