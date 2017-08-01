package cz.cesnet.cloud.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.vaadin.commons.PolledView;
import cz.cesnet.cloud.vaadin.compute.ComputeView;
import cz.cesnet.cloud.vaadin.create.CreateView;
import cz.cesnet.cloud.vaadin.list.ListView;
import cz.cesnet.cloud.vaadin.network.NetworkView;
import cz.cesnet.cloud.vaadin.storage.StorageView;

@Theme("valo")
@Title("GUOCCI")
public class GUOCCI extends UI {
	private Navigator navigator;
	private HorizontalLayout breadcrumbs;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		Label heading = new Label("OCCI Web Interface");
		heading.addStyleName(ValoTheme.LABEL_HUGE);

		breadcrumbs = new HorizontalLayout();
		removeButtons();

		HorizontalLayout titleBar = new HorizontalLayout(breadcrumbs, heading);
		titleBar.setExpandRatio(heading, 1);
		titleBar.setComponentAlignment(heading, Alignment.MIDDLE_RIGHT);
		titleBar.setWidth("100%");
		titleBar.setMargin(false);

		Panel content = new Panel();
		content.addStyleName(ValoTheme.PANEL_BORDERLESS);

		VerticalLayout layout = new VerticalLayout(titleBar, content);
		setContent(layout);

		navigator = new Navigator(this, content);
		navigator.addView("", new ListView());
		navigator.addView("compute", ComputeView.class);
		navigator.addView("storage", StorageView.class);
		navigator.addView("network", NetworkView.class);
		navigator.addView("create", CreateView.class);

		navigator.addViewChangeListener(viewChangeEvent -> {
			removeButtons();
			PolledView newView = (PolledView) viewChangeEvent.getNewView();
			addPollListener(pollEvent -> newView.pollMethod());
			return true;
		});

		//Poll the server for any changes
		setPollInterval(5000);
	}

	public void addButton(Resource r, String link) {
		addNecessaryArrow();
		Button b = new Button(r);
		b.addStyleName(ValoTheme.BUTTON_QUIET);
		b.addClickListener(clickEvent -> navigator.navigateTo(link));
		breadcrumbs.addComponent(b);
	}

	public void addButton(String s, String link) {
		addNecessaryArrow();
		Button b = new Button(s);
		b.addStyleName(ValoTheme.BUTTON_QUIET);
		b.addClickListener(clickEvent -> navigator.navigateTo(link));
		breadcrumbs.addComponent(b);
	}

	private void addNecessaryArrow() {
		if (breadcrumbs.getComponentCount() > 0) {
			Button arrow = new Button(VaadinIcons.ANGLE_RIGHT);
			arrow.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			arrow.setEnabled(false);
			breadcrumbs.addComponent(arrow);
		}
	}

	private void removeButtons() {
		breadcrumbs.removeAllComponents();
		addButton(VaadinIcons.HOME, "");
	}

	@WebServlet(urlPatterns = "/*", name = "GUOCCI", asyncSupported = true)
	@VaadinServletConfiguration(ui = GUOCCI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
