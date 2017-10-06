package cz.cesnet.cloud.vaadin.list;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.Configuration;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.vaadin.commons.PolledView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ListView extends VerticalLayout implements PolledView {
	private static final Logger logger = LoggerFactory.getLogger(ListView.class);

	private Configuration configuration;

	private VerticalLayout endpointList;

	public ListView() {
		configuration = (Configuration) VaadinServlet.getCurrent().getServletContext().getAttribute("configuration");

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);

		endpointList = new VerticalLayout();
		endpointList.setWidth(100, Unit.PERCENTAGE);
		endpointList.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		endpointList.setSpacing(true);

		Button create = new Button("Create", VaadinIcons.PLUS);
		create.addStyleName(ValoTheme.BUTTON_PRIMARY);
		create.addClickListener(clickEvent -> getUI().getPage().setLocation(configuration.getChooserURI()));

		HorizontalLayout bar = new HorizontalLayout(create);

		addComponents(bar, endpointList);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		endpointList.removeAllComponents();

		try {
			Map<String, OCCI> occiMap = OCCI.getOCCI(getSession(), configuration);

			for (OCCI occi: occiMap.values()) {
				GridLayout grid = new GridLayout(3, 1);
				grid.setWidth(100, Unit.PERCENTAGE);
				grid.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				grid.setSpacing(true);
				Label endpointLabel = new Label(occi.getEndpoint().toString());
				endpointLabel.setStyleName(ValoTheme.LABEL_LARGE);
				endpointList.addComponents(endpointLabel, grid);

				try {
					List<ComputeDAO> computes = occi.getComputes();

					for (ComputeDAO c: computes) {
						grid.addComponent(new ComputeDetail(c));
					}
				} catch (CommunicationException e) {
					//TODO: Think of better way to display exceptions. This exception is thrown even when the list is simply empty.
					//Notify.warnNotify("Exception occurred while listing available computes.", e.getMessage());
					logger.error("Error listing user's computes.", e);
				}
			}
		} catch (CommunicationException e) {
			//TODO: Think of better way to display exceptions. This exception is thrown even when the list is simply empty.
			//Notify.warnNotify("Exception occurred while listing available computes.", e.getMessage());
			logger.error("Error listing user's computes.", e);
		}
	}

	@Override
	public void pollMethod() {
		enter(null);
	}
}
