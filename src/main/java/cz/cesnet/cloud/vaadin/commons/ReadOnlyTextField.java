package cz.cesnet.cloud.vaadin.commons;

import com.vaadin.ui.TextField;

public class ReadOnlyTextField extends TextField {
	public ReadOnlyTextField() {
		super();
		setReadOnly(true);
	}

	public ReadOnlyTextField(String caption) {
		super(caption);
		setReadOnly(true);
	}

	public ReadOnlyTextField(String caption, String value) {
		super(caption, value);
		setReadOnly(true);
	}

	public ReadOnlyTextField(ValueChangeListener<String> valueChangeListener) {
		super(valueChangeListener);
		setReadOnly(true);
	}

	public ReadOnlyTextField(String caption, ValueChangeListener<String> valueChangeListener) {
		super(caption, valueChangeListener);
		setReadOnly(true);
	}

	public ReadOnlyTextField(String caption, String value, ValueChangeListener<String> valueChangeListener) {
		super(caption, value, valueChangeListener);
		setReadOnly(true);
	}
}
