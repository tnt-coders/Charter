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

	private static String[] applicationArgs;
	private CharterContext context;

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

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			deleteTempUpdateFile();
			initConfigs();

			final String pathToOpen = getPathToOpen(applicationArgs);

			context = new CharterContext();
			context.init(primaryStage);

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
	public void stop() throws Exception {
		if (context != null) {
			context.forceExit();
		}
		super.stop();
	}

	public static void main(final String[] args) throws InterruptedException, IOException {
		applicationArgs = args;
		launch(args);
	}
}
