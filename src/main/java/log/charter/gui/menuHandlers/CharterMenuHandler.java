package log.charter.gui.menuHandlers;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import log.charter.data.config.Localization.Label;
import log.charter.gui.CharterFrame;
import log.charter.io.Logger;
import log.charter.services.Action;
import log.charter.services.ActionHandler;
import log.charter.services.mouseAndKeyboard.Shortcut;
import log.charter.services.mouseAndKeyboard.ShortcutConfig;

abstract class CharterMenuHandler {

	protected static Menu createMenu(final Label label) {
		return new Menu(label.label());
	}

	protected ActionHandler actionHandler;
	protected CharterFrame charterFrame;

	protected MenuItem createItem(final Label label, final Runnable onAction) {
		return createItem(label.label(), onAction);
	}

	protected MenuItem createItem(final String label, final Runnable onAction) {
		final MenuItem item = new MenuItem(label);
		item.setOnAction(e -> {
			try {
				onAction.run();
			} catch (final Throwable t) {
				Logger.error("Couldn't do action " + label, t);
				// TODO: Implement JavaFX popup
				// ComponentUtils.showPopup(charterFrame, Label.ERROR, t.getLocalizedMessage());
			}
		});
		return item;
	}

	protected MenuItem createItem(final Action action, final Label label) {
		final Shortcut shortcut = ShortcutConfig.shortcuts.get(action);
		final String shortcutName = shortcut == null ? "" : " (" + shortcut.name("-") + ")";
		final MenuItem item = new MenuItem(label.label() + shortcutName);
		item.setOnAction(e -> actionHandler.fireAction(action));
		return item;
	}

	protected MenuItem createItem(final Action action) {
		return createItem(action, action.label);
	}

	protected MenuItem createDisabledItem(final Action action) {
		final MenuItem item = createItem(action);
		item.setDisable(true);
		return item;
	}

	abstract boolean isApplicable();

	abstract Menu prepareMenu();

}
