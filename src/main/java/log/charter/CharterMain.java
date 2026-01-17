package log.charter;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import log.charter.data.config.Config;
import log.charter.data.config.GraphicalConfig;
import log.charter.data.config.values.PathsConfig;
import log.charter.io.Logger;
import log.charter.services.CharterContext;
import log.charter.services.mouseAndKeyboard.ShortcutConfig;
import log.charter.util.RW;

public class CharterMain extends Application {
	public static final String VERSION = "0.21.15";
	public static final String VERSION_DATE = "2025.04.06 02:30";
	public static final String TITLE = "Charter " + VERSION;

	private static void deleteTempUpdateFile() {
		try {
			final File tempUpdateFile = new File(RW.getJarDirectory(), "tmp_update.bat");
			if (tempUpdateFile.exists()) {
				tempUpdateFile.delete();
			}
		} catch (final SecurityException e) {
			Logger.debug("Couldn't delete tmp_update.bat", e);
		}
	}

	private static void initConfigs() {
		Config.init();
		GraphicalConfig.init();
		ShortcutConfig.init();
	}

	private static String getPathToOpen(final String[] args) {
		if (args.length > 0) {
			return args[0];
		}

		return PathsConfig.lastPath;
	}

	private CharterContext context;

	@Override
	public void start(final Stage primaryStage) {
		try {
			deleteTempUpdateFile();
			initConfigs();

			context = new CharterContext();
			context.init(primaryStage);

			final String pathToOpen = getPathToOpen(getParameters().getRaw().toArray(new String[0]));
			if (pathToOpen != null && !pathToOpen.isBlank()) {
				context.openProject(pathToOpen);
			}

			context.checkForUpdates();
			Logger.info("Charter started");
		} catch (final Throwable t) {
			Logger.error("Couldn't start Charter", t);
		}
	}

	@Override
	public void stop() {
		if (context != null) {
			context.exit();
		}
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
