package cz.cesnet.cloud.vaadin.list;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.vaadin.commons.PolledView;

import java.util.List;

public class ListView extends VerticalLayout implements PolledView {
	private static final String chooseURI = "http://localhost:8080/chooser/";

	private GridLayout list;

	public ListView() {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);

		list = new GridLayout(3,1);
		list.setWidth(100, Unit.PERCENTAGE);
		list.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		list.setSpacing(true);

		Button create = new Button("Create", VaadinIcons.PLUS);
		create.addStyleName(ValoTheme.BUTTON_PRIMARY);
		create.addClickListener(clickEvent -> getUI().getPage().setLocation(chooseURI));

		HorizontalLayout bar = new HorizontalLayout(create);

		addComponents(bar, list);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		list.removeAllComponents();

		try {
			OCCI occi = OCCI.getOCCI(getSession());
			List <ComputeDAO> computes = occi.getComputes();

			for (ComputeDAO c: computes) {
				VerticalLayout layout = new VerticalLayout();
				Panel panel = new Panel(c.getResource().getTitle(), layout);
				panel.setId(c.getResource().getId());

				layout.addComponent(new Label("Hostname: " + c.getHostname()));
				layout.addComponent(new Label("Cores: " + c.getCores()));
				layout.addComponent(new Label("Memory: " + c.getMemory()));
				layout.addComponent(new Label("Architecture: " + c.getArchitecture()));
				layout.addComponent(new Label("Speed: " + c.getSpeed()));
				layout.addComponent(new Label("State: " + c.getState()));

				panel.addClickListener(clickEvent -> {
					if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
						Panel p = (Panel)clickEvent.getComponent();
						getUI().getNavigator().navigateTo("compute/" + p.getId());
					}
				});
				list.addComponent(panel);
			}
		} catch (CommunicationException e) {
			new Notification("Exception occurred while listing available computes.", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void pollMethod() {
		//TODO
	}
}
