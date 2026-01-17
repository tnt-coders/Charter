package log.charter.gui.menuHandlers;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import log.charter.services.CharterContext;
import log.charter.services.CharterContext.Initiable;
import log.charter.util.collections.ArrayList2;
import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.CharterFrame;

public class CharterMenuBar extends MenuBar implements Initiable {

	public static final ColorLabel backgroundColor = ColorLabel.BASE_BG_2;

	private CharterContext charterContext;
	private CharterFrame charterFrame;

	private final ArrangementMenuHandler arrangementMenuHandler = new ArrangementMenuHandler();
	private final EditMenuHandler editMenuHandler = new EditMenuHandler();
	private final FileMenuHandler fileMenuHandler = new FileMenuHandler();
	private final GuitarMenuHandler guitarMenuHandler = new GuitarMenuHandler();
	private final InfoMenuHandler infoMenuHandler = new InfoMenuHandler();
	private final MusicMenuHandler musicMenuHandler = new MusicMenuHandler();
	private final NotesMenuHandler notesMenuHandler = new NotesMenuHandler();
	private final VocalsMenuHandler vocalsMenuHandler = new VocalsMenuHandler();

	private final ArrayList2<CharterMenuHandler> menus = new ArrayList2<>(//
			fileMenuHandler, //
			editMenuHandler, //
			musicMenuHandler, //
			arrangementMenuHandler, //
			vocalsMenuHandler, //
			notesMenuHandler, //
			guitarMenuHandler, //
			infoMenuHandler);

	@Override
	public void init() {
		charterContext.initObject(arrangementMenuHandler);
		charterContext.initObject(editMenuHandler);
		charterContext.initObject(fileMenuHandler);
		charterContext.initObject(guitarMenuHandler);
		charterContext.initObject(infoMenuHandler);
		charterContext.initObject(musicMenuHandler);
		charterContext.initObject(notesMenuHandler);
		charterContext.initObject(vocalsMenuHandler);

		refreshMenus();
	}

	public void refreshMenus() {
		final List<Menu> menusToAdd = menus.stream()//
				.filter(menu -> menu.isApplicable())//
				.map(menu -> menu.prepareMenu())//
				.collect(Collectors.toList());

		getMenus().clear();
		getMenus().addAll(menusToAdd);
	}
}
