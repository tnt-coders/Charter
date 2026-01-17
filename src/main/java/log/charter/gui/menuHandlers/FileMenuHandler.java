package log.charter.gui.menuHandlers;

import javafx.scene.control.Menu;
import log.charter.data.config.Localization.Label;
import log.charter.services.Action;

public class FileMenuHandler extends CharterMenuHandler {

	@Override
	boolean isApplicable() {
		return true;
	}

	private Menu prepareNewProjectMenu() {
		final Menu newProjectSubmenu = createMenu(Label.NEW_PROJECT);
		newProjectSubmenu.getItems().add(createItem(Action.NEW_PROJECT, Label.NEW_PROJECT_EMPTY));
		// TODO: Implement other options

		return newProjectSubmenu;
	}

	private Menu prepareImportsMenu() {
		final Menu importSubmenu = createMenu(Label.FILE_MENU_IMPORT);
		importSubmenu.getItems().add(createItem(Label.FILE_MENU_IMPORT_MIDI_TEMPO, this::importMidiTempo));
		// TODO: Implement other options

		return importSubmenu;
	}

	@Override
	Menu prepareMenu() {
		final Menu menu = createMenu(Label.FILE_MENU);
		menu.getItems().add(prepareNewProjectMenu());
		menu.getItems().add(createItem(Action.OPEN_PROJECT));
		// TODO: Implement other menu items

		return menu;
	}

	private void importMidiTempo() {
	}
}
