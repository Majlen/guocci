package cz.cesnet.cloud.vaadin.compute;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.cesnet.cloud.occi.OCCI;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.infrastructure.ComputeDAO;
import cz.cesnet.cloud.occi.infrastructure.ResourceTemplateMixin;
import cz.cesnet.cloud.vaadin.commons.Notify;

import java.util.Optional;
import java.util.Set;

public class ResizeWindow extends Window {
	private ComputeDAO compute;
	private ComboBox<ResourceTemplateMixin> resTplComboBox;

	public ResizeWindow(ComputeDAO compute) {
		super("Resize compute");
		center();
		this.compute = compute;

		try {
			Set<ResourceTemplateMixin> resTpls = OCCI.getOCCI(getSession()).getResTpls();
			resTplComboBox = new ComboBox<>("Resource template", resTpls);
			resTplComboBox.setWidth(100, Unit.PERCENTAGE);

			Button apply = new Button("Apply", VaadinIcons.CHECK);
			apply.addStyleName(ValoTheme.BUTTON_PRIMARY);
			apply.addClickListener(clickEvent -> {
				resizeCompute();
				close();
			});

			Button cancel = new Button("Cancel", VaadinIcons.CLOSE);
			cancel.addStyleName(ValoTheme.BUTTON_DANGER);
			cancel.addClickListener(clickEvent -> {
				close();
			});

			HorizontalLayout buttons = new HorizontalLayout(cancel, apply);
			FormLayout form = new FormLayout(resTplComboBox, buttons);
			form.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
			form.setWidth(600, Unit.PIXELS);
			form.setMargin(true);

			setContent(form);
		} catch (CommunicationException e) {

		}
	}

	private void resizeCompute() {
		Optional<ResourceTemplateMixin> selected = resTplComboBox.getSelectedItem();

		selected.ifPresent(resTpl -> {
			try {
				compute.setOptionMixin(resTpl.getMixin(), resTpl.getParentMixin());
			} catch (CommunicationException e) {
				Notify.errNotify("Cannot resize compute.", e.getMessage());
			}
		});
	}
}
