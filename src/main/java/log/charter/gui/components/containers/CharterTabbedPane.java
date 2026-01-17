package log.charter.gui.components.containers;

import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import log.charter.data.config.Localization.Label;

public class CharterTabbedPane {
	private TabPane tabPane;

	public static class Tab {
		public final String name;
		public final Node component;

		public Image icon = null;
		public String tip = null;

		public Tab(final Label label, final Node component) {
			this(label.label(), component);
		}

		public Tab(final String name, final Node component) {
			this.name = name;
			this.component = component;
		}

		public Tab icon(final Image value) {
			icon = value;
			return this;
		}

		public Tab tip(final String value) {
			tip = value;
			return this;
		}
	}

	public CharterTabbedPane(final Tab... tabs) {
		tabPane = new TabPane();

		for (final Tab tab : tabs) {
			javafx.scene.control.Tab fxTab = new javafx.scene.control.Tab(tab.name);
			fxTab.setContent(tab.component);

			if (tab.icon != null) {
				fxTab.setGraphic(new ImageView(tab.icon));
			}

			if (tab.tip != null) {
				fxTab.setTooltip(new javafx.scene.control.Tooltip(tab.tip));
			}

			tabPane.getTabs().add(fxTab);
		}

		// Handle resizing
		tabPane.widthProperty().addListener((obs, oldVal, newVal) -> resize());
		tabPane.heightProperty().addListener((obs, oldVal, newVal) -> resize());
	}

	private void resize() {
		// JavaFX handles tab sizing automatically
	}

	public void resize() {
		// Called from parent, JavaFX handles this automatically
	}

	public TabPane getTabPane() {
		return tabPane;
	}
}
