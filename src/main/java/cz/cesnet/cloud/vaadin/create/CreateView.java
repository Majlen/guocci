package cz.cesnet.cloud.vaadin.create;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.Configuration;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.EntityBuilder;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.exception.EntityBuildingException;
import cz.cesnet.cloud.occi.core.Mixin;
import cz.cesnet.cloud.occi.core.Resource;
import cz.cesnet.cloud.occi.exception.AmbiguousIdentifierException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.vaadin.GUOCCI;
import cz.cesnet.cloud.vaadin.commons.Notify;
import cz.cesnet.cloud.vaadin.commons.ParameterParser;
import cz.cesnet.cloud.vaadin.commons.PolledView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class CreateView extends VerticalLayout implements PolledView {
	private static final Logger logger = LoggerFactory.getLogger(CreateView.class);
	Configuration configuration;

	private EntityBuilder entityBuilder;
	private TextField title = new TextField("Title");
	private OCCI occi;
	private Mixin os_tpl;
	private VerticalLayout os_tplLayout = new VerticalLayout();
	private Mixin res_tpl;
	private VerticalLayout res_tplLayout = new VerticalLayout();

	public CreateView() {
		configuration = (Configuration) VaadinServlet.getCurrent().getServletContext().getAttribute("configuration");

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);

		Button back = new Button("Back to selection", VaadinIcons.ANGLE_LEFT);
		back.addStyleName(ValoTheme.BUTTON_DANGER);
		back.addClickListener(clickEvent -> Page.getCurrent().open(configuration.getChooserURI(), null));

		Button create = new Button("Create", VaadinIcons.PLUS);
		create.addStyleName(ValoTheme.BUTTON_PRIMARY);
		create.addClickListener(clickEvent -> createCompute());

		HorizontalLayout buttons = new HorizontalLayout(back, create);

		Panel os_tplPanel = new Panel("Image", os_tplLayout);
		os_tplLayout.setWidthUndefined();
		os_tplPanel.setWidthUndefined();
		Panel res_tplPanel = new Panel("Size", res_tplLayout);
		res_tplLayout.setWidthUndefined();
		res_tplPanel.setWidthUndefined();
		VerticalLayout computeLayout = new VerticalLayout(title, os_tplPanel, res_tplPanel, buttons);

		addComponent(computeLayout);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
		GUOCCI guocci = (GUOCCI) getUI();
		guocci.addButton("Create compute", "create/" + viewChangeEvent.getParameters());

		title.clear();
		os_tplLayout.removeAllComponents();
		res_tplLayout.removeAllComponents();

		ParameterParser parser = new ParameterParser(viewChangeEvent.getParameters(), false);
		Map<String, String> parameters = parser.getOtherValues();

		try {
			Map<String, OCCI> occiMap = OCCI.getOCCI(getSession(), configuration);
			logger.debug("Looking for OCCI endpoint {}.", parameters.get("service"));
			occi = occiMap.get(parameters.get("service"));

			entityBuilder = new EntityBuilder(occi.getModel());

			logger.debug("Adding mixin os_tpl with parameter {}.", parameters.get("image"));
			os_tpl = occi.getMixin(URI.create(parameters.get("image")));
			logger.trace(os_tpl.toString());
			os_tplLayout.addComponent(new Label(os_tpl.getTitle()));

			logger.debug("Adding mixin res_tpl with parameter {}.", parameters.get("flavour"));
			res_tpl = occi.getMixin(URI.create(parameters.get("flavour")));
			logger.trace(res_tpl.toString());
			res_tplLayout.addComponent(new Label(res_tpl.getTitle()));
		} catch (AmbiguousIdentifierException | CommunicationException e) {
			Notify.errNotify("Exception occured while loading details about compute.", e.getMessage());
			logger.error("Cannot load details about compute.", e);
		}

	}

	private void createCompute() {
		try {
			Resource computeResource = entityBuilder.getResource("compute");
			computeResource.addMixin(os_tpl);
			computeResource.addMixin(res_tpl);
			if (!title.isEmpty()) {
				computeResource.setTitle(title.getValue());
			}

			String path = occi.create(computeResource).getPath();
			getUI().getNavigator().navigateTo("compute/" + path.substring(path.lastIndexOf('/') + 1));
		} catch (EntityBuildingException | CommunicationException | InvalidAttributeValueException e) {
			Notify.errNotify("Exception occured while creating compute.", e.getMessage());
			logger.error("Error creating compute.", e);
		}
	}

	@Override
	public void pollMethod() {
		//Do nothing
		//This is here just to simplify the PollListener in GUOCCI
	}
}
