package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkDAO;
import cz.cesnet.cloud.occi.infrastructure.StorageDAO;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.DeleteWindow;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.PolledView;

import java.util.List;

public class ComputeView extends VerticalLayout implements PolledView {
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
				Notify.errNotify("Exception occured while starting compute.", e.getMessage());
				System.out.println(e.getMessage());
			}
		});

		stop.addClickListener(clickEvent -> {
			try {
				compute.stop();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while stopping compute.", e.getMessage());
				System.out.println(e.getMessage());
			}
		});

		restart.addClickListener(clickEvent -> {
			try {
				compute.restart();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while restarting compute.", e.getMessage());
				System.out.println(e.getMessage());
			}
		});

		suspend.addClickListener(clickEvent -> {
			try {
				compute.suspend();
			} catch (CommunicationException e) {
				Notify.errNotify("Exception occured while suspending compute.", e.getMessage());
				System.out.println(e.getMessage());
			}
		});

		remove.addClickListener(clickEvent -> {
			getUI().addWindow(new DeleteWindow("", () -> {
				try {
					compute.remove();
				} catch (CommunicationException e) {
					Notify.errNotify("Exception occured while removing compute.", e.getMessage());
					System.out.println(e.getMessage());
				}
			}
			));
		});

		HorizontalLayout bar = new HorizontalLayout(start, stop, restart, suspend, remove);

		computeDetail = new ComputeDetail();
		linksDetail = new VerticalLayout();

		HorizontalLayout content = new HorizontalLayout(computeDetail, linksDetail);

		addComponents(bar, content);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		compute = null;
		try {
			OCCI occi = OCCI.getOCCI(getSession());
			compute = occi.getCompute(viewChangeEvent.getParameters());
		} catch (CommunicationException e) {
			System.out.println(e.getMessage());
		}

		GUOCCI guocci = (GUOCCI) getUI();
		guocci.addButton(compute.getResource().getTitle(), "compute/" + viewChangeEvent.getParameters());

		try {
			fillEverything();
		} catch (CommunicationException e) {
			Notify.errNotify("Exception occured while getting compute detail.", e.getMessage());
			System.out.println(e.getMessage());
		}

	}

	private void fillEverything() throws CommunicationException {
		linksDetail.removeAllComponents();

		fillCompute();
		fillStorages(compute.getStorages());
		fillNetworks(compute.getNetworks());
	}

	private void fillCompute() {
		computeDetail.refresh(compute);
		setButtons();
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

	private void setButtons() {
		start.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_START));
		stop.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_STOP));
		restart.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_RESTART));
		suspend.setEnabled(compute.getResource().containsAction(ComputeDAO.COMPUTE_SUSPEND));
	}

	public ComputeDAO getCompute() {
		return compute;
	}

	@Override
	public void pollMethod() {
		try {
			compute = OCCI.getOCCI(getSession()).getCompute(compute.getResource().getLocation());
			fillCompute();
		} catch (CommunicationException e) {
			Notify.errNotify("Exception occured while getting compute detail.", e.getMessage());
			System.out.println(e.getMessage());
		}
	}
}
