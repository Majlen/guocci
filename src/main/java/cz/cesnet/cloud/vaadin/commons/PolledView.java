package cz.cesnet.cloud.vaadin.commons;

import com.vaadin.navigator.View;

public interface PolledView extends View {
	void pollMethod();
}
