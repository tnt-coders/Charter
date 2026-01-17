package log.charter.gui.components.containers;

import javafx.scene.layout.Pane;

public class RowedPanel extends Pane {

	public static interface ValueSetter<T> {
		void setValue(T val);
	}

	public static interface BooleanValueSetter {
		void setValue(boolean val);
	}

	public static interface IntegerValueSetter {
		void setValue(Integer val);
	}

	public RowedPanel() {
	}
}
