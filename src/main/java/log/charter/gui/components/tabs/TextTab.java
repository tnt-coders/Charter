package log.charter.gui.components.tabs;

import javafx.scene.control.TextArea;
import log.charter.gui.components.containers.CharterScrollPane;

public class TextTab extends CharterScrollPane {

	private final TextArea textArea;

	public TextTab() {
		this(new TextArea());
	}

	private TextTab(final TextArea textArea) {
		super(textArea);
		this.textArea = textArea;
	}

	public String getText() {
		return textArea.getText();
	}

	public void setText(final String text) {
		textArea.setText(text);
	}
}
