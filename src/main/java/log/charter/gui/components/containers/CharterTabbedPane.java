package log.charter.gui.components.containers;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import log.charter.data.config.Localization.Label;

public class CharterTabbedPane extends TabPane {

	public static class TabData {
		public final String name;
		public final Node component;

		public String tip = null;

		public TabData(final Label label, final Node component) {
			this(label.label(), component);
		}

		public TabData(final String name, final Node component) {
			this.name = name;
			this.component = component;
		}

		public TabData tip(final String value) {
			tip = value;
			return this;
		}
	}

	public CharterTabbedPane(final TabData... tabs) {
		super();

		for (final TabData tabData : tabs) {
			final Tab tab = new Tab(tabData.name, tabData.component);
			tab.setClosable(false);
			if (tabData.tip != null) {
				// TODO: set tooltip
			}
			getTabs().add(tab);
		}
	}
}
