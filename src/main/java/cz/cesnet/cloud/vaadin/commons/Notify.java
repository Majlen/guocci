package cz.cesnet.cloud.vaadin.commons;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class Notify {
	public static void errNotify(String title, String message) {
		new Notification(title, message, Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
	}
}
