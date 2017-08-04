package cz.cesnet.cloud.vaadin.list;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;

public class ComputeDetail extends Panel {
	public ComputeDetail(ComputeDAO c) {
		setCaption(c.getResource().getTitle());

		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("Hostname: " + c.getHostname()));
		layout.addComponent(new Label("Cores: " + c.getCores()));
		layout.addComponent(new Label("Memory: " + c.getMemory()));
		layout.addComponent(new Label("Architecture: " + c.getArchitecture()));
		layout.addComponent(new Label("Speed: " + c.getSpeed()));
		layout.addComponent(new Label("State: " + c.getState()));

		Button detail = new Button("Detail", VaadinIcons.ELLIPSIS_DOTS_V);
		detail.addClickListener(clickEvent -> {
			getUI().getNavigator().navigateTo("compute/" + c.getResource().getId());
		});

		layout.addComponent(detail);
		layout.setComponentAlignment(detail, Alignment.MIDDLE_RIGHT);
		setContent(layout);
	}
}
