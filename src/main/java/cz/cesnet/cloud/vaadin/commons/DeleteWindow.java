package cz.cesnet.cloud.vaadin.commons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;

public class DeleteWindow extends Window {
	public DeleteWindow(String navigateTo, Runnable action) {
		super("Destroy");
		center();

		Label text = new Label(" Destroy resource?");

		Button cancel = new Button("Cancel", VaadinIcons.CLOSE);
		Button ok = new Button("OK", VaadinIcons.CHECK);
		HorizontalLayout buttons = new HorizontalLayout(cancel, ok);

		VerticalLayout content = new VerticalLayout(text, buttons);

		cancel.addClickListener(clickEvent -> this.close());
		ok.addClickListener(clickEvent -> {
			action.run();
			getUI().getNavigator().navigateTo(navigateTo);
			close();
		});

		setContent(content);
	}
}
