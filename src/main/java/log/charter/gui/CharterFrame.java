package log.charter.gui;

import static log.charter.data.config.SystemType.MAC;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import log.charter.CharterMain;
import log.charter.data.ChartData;
import log.charter.data.config.Config;
import log.charter.data.config.Localization.Label;
import log.charter.data.config.SystemType;
import log.charter.data.config.values.WindowStateConfig;
import log.charter.data.song.Arrangement;
import log.charter.gui.chartPanelDrawers.common.DrawerUtils;
import log.charter.gui.components.containers.CharterScrollPane;
import log.charter.gui.components.containers.CharterTabbedPane;
import log.charter.gui.components.containers.CharterTabbedPane.Tab;
import log.charter.gui.components.preview3D.Preview3DPanel;
import log.charter.gui.components.simple.ChartMap;
import log.charter.gui.components.tabs.HelpTab;
import log.charter.gui.components.tabs.TextTab;
import log.charter.gui.components.tabs.chordEditor.ChordTemplatesEditorTab;
import log.charter.gui.components.tabs.errorsTab.ErrorsTab;
import log.charter.gui.components.tabs.selectionEditor.CurrentSelectionEditor;
import log.charter.gui.components.toolbar.ChartToolbar;
import log.charter.gui.menuHandlers.CharterMenuBar;
import log.charter.io.Logger;
import log.charter.services.CharterContext;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.files.FileDropHandler;
import log.charter.services.editModes.EditMode;
import log.charter.services.editModes.ModeManager;
import log.charter.services.mouseAndKeyboard.KeyboardHandler;

public class CharterFrame implements Initiable {
	private Stage stage;

	private Scene scene;
	private BorderPane root;
	private VBox topContainer;

	private ChartData chartData;
	private CharterContext charterContext;
	private ChordTemplatesEditorTab chordTemplatesEditorTab;
	private CurrentSelectionEditor currentSelectionEditor;
	private ErrorsTab errorsTab;
	private FileDropHandler fileDropHandler;
	private HelpTab helpTab;
	private KeyboardHandler keyboardHandler;
	private ModeManager modeManager;
	private TextTab textTab;

	private final Preview3DPanel preview3DPanel = SystemType.not(MAC) ? new Preview3DPanel() : null;

	private CharterMenuBar charterMenuBar;
	private ChartToolbar chartToolbar;
	private ChartPanel chartPanel;
	private ChartMap chartMap;
	private CharterTabbedPane tabs;

	private boolean paintWaiting = false;

	public CharterFrame() {
		root = new BorderPane();
		topContainer = new VBox();
	}

	@Override
	public void init() {
		if (SystemType.not(MAC)) {
			charterContext.initObject(preview3DPanel);
		}

		if (SystemType.is(MAC)) {
			tabs = new CharterTabbedPane(//
					new Tab(Label.TAB_QUICK_EDIT, new CharterScrollPane(currentSelectionEditor)), //
					new Tab(Label.TAB_CHORD_TEMPLATES_EDITOR, new CharterScrollPane(chordTemplatesEditorTab)), //
					new Tab(Label.TAB_ERRORS, errorsTab), //
					new Tab(Label.TAB_TEXT, textTab), //
					new Tab(Label.TAB_HELP, helpTab));
		} else {
			tabs = new CharterTabbedPane(//
					new Tab(Label.TAB_QUICK_EDIT, new CharterScrollPane(currentSelectionEditor)), //
					new Tab(Label.TAB_CHORD_TEMPLATES_EDITOR, new CharterScrollPane(chordTemplatesEditorTab)), //
					new Tab(Label.TAB_ERRORS, errorsTab), //
					new Tab(Label.TAB_3D_PREVIEW, preview3DPanel), //
					new Tab(Label.TAB_TEXT, textTab), //
					new Tab(Label.TAB_HELP, helpTab));
		}

		// Build layout
		topContainer.getChildren().addAll(chartToolbar.getNode(), chartPanel.getNode(), chartMap.getNode());
		root.setTop(charterMenuBar.getMenuBar());
		root.setCenter(topContainer);
		root.setBottom(tabs.getTabPane());
	}

	public void finishInitAndShow(Stage primaryStage) {
		this.stage = primaryStage;

		try {
			final InputStream stream = this.getClass().getResourceAsStream("/icon.ico");
			if (stream != null) {
				stage.getIcons().add(new Image(stream));
			}
		} catch (final IOException e) {
			Logger.error("Couldn't load icon", e);
		}

		stage.setTitle(CharterMain.TITLE + " : " + Label.NO_PROJECT.label());
		stage.setWidth(WindowStateConfig.width);
		stage.setHeight(WindowStateConfig.height);
		stage.setX(WindowStateConfig.x);
		stage.setY(WindowStateConfig.y);

		// Handle window state (maximized, etc.)
		if (WindowStateConfig.extendedState == java.awt.Frame.MAXIMIZED_BOTH) {
			stage.setMaximized(true);
		}

		scene = new Scene(root, WindowStateConfig.width, WindowStateConfig.height);

		// Apply CSS styling
		applyStyling();

		stage.setScene(scene);

		// Set up event handlers
		setupEventHandlers();

		// Set up drag and drop
		setupDragAndDrop();

		stage.show();
		stage.requestFocus();

		resizeComponents();
	}

	private void applyStyling() {
		// Load CSS stylesheet if it exists
		try {
			final String css = this.getClass().getResource("/charter-style.css").toExternalForm();
			scene.getStylesheets().add(css);
		} catch (Exception e) {
			// CSS file doesn't exist yet, will be created later
		}
	}

	private void setupEventHandlers() {
		// Window close handler
		stage.setOnCloseRequest(event -> {
			event.consume();
			charterContext.exit();
		});

		// Window resize handler
		stage.widthProperty().addListener((obs, oldVal, newVal) -> resize());
		stage.heightProperty().addListener((obs, oldVal, newVal) -> resize());

		// Window focus handler
		stage.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				keyboardHandler.onFocusGained();
			} else {
				keyboardHandler.onFocusLost();
			}
		});

		// Keyboard handler
		scene.setOnKeyPressed(keyboardHandler::handleKeyPressed);
		scene.setOnKeyReleased(keyboardHandler::handleKeyReleased);
	}

	private void setupDragAndDrop() {
		scene.setOnDragOver((DragEvent event) -> {
			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});

		scene.setOnDragDropped((DragEvent event) -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				fileDropHandler.handleFiles(db.getFiles());
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});
	}

	public void resize() {
		WindowStateConfig.height = (int) stage.getHeight();
		WindowStateConfig.width = (int) stage.getWidth();
		WindowStateConfig.x = (int) stage.getX();
		WindowStateConfig.y = (int) stage.getY();
		WindowStateConfig.extendedState = stage.isMaximized() ? java.awt.Frame.MAXIMIZED_BOTH : java.awt.Frame.NORMAL;
		Config.markChanged();

		resizeComponents();
	}

	private void resizeComponents() {
		// JavaFX handles most layout automatically through the BorderPane
		// Custom sizing logic will be handled by individual components
		chartPanel.resize();
		chartMap.resize();
		tabs.resize();
	}

	public void updateSizes() {
		final EditMode editMode = modeManager.getMode();
		final Arrangement arrangement = chartData.currentArrangement();
		final boolean bass = arrangement.isBass();
		final int strings = arrangement.tuning.strings();

		DrawerUtils.updateEditAreaSizes(editMode, bass, strings);
		resize();
	}

	public void reloadTextures() {
		if (SystemType.is(MAC)) {
			return;
		}

		preview3DPanel.reloadTextures();
	}

	public void repaint() {
		if (paintWaiting) {
			return;
		}

		paintWaiting = true;

		Platform.runLater(() -> {
			try {
				chartPanel.repaint();
				chartMap.repaint();
				helpTab.addFrameTime();

				if (SystemType.not(MAC) && preview3DPanel.isShowing()) {
					preview3DPanel.repaint();
				}
			} catch (final Exception e) {
				Logger.error("Error in CharterFrame.repaint", e);
			} finally {
				paintWaiting = false;
			}
		});
	}

	public void close() {
		if (stage != null) {
			stage.close();
		}
	}

	public Stage getStage() {
		return stage;
	}

	public void setTitle(String title) {
		if (stage != null) {
			stage.setTitle(title);
		}
	}
}
