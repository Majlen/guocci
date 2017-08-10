package cz.cesnet.cloud.vaadin.commons;

import com.vaadin.ui.Notification;

public class Notify {
	public static void errNotify(String title, String message) {
		Notification.show(title, message, Notification.Type.ERROR_MESSAGE);
	}

	public static void warnNotify(String title, String message) {
		Notification.show(title, message, Notification.Type.WARNING_MESSAGE);
	}
}
