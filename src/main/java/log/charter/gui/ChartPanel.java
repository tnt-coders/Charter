package log.charter.gui;

import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.editAreaHeight;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import log.charter.data.ChartData;
import log.charter.gui.chartPanelDrawers.ArrangementDrawer;
import log.charter.gui.chartPanelDrawers.common.BackgroundDrawer;
import log.charter.gui.chartPanelDrawers.common.MarkerDrawer;
import log.charter.gui.chartPanelDrawers.common.Graphics2DWrapper;
import log.charter.services.CharterContext;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.mouseAndKeyboard.KeyboardHandler;
import log.charter.services.mouseAndKeyboard.MouseHandler;

public class ChartPanel implements Initiable {
	private Canvas canvas;
	private GraphicsContext gc;

	private CharterContext charterContext;
	private ChartData chartData;
	private ChartTimeHandler chartTimeHandler;
	private KeyboardHandler keyboardHandler;
	private MouseHandler mouseHandler;

	private final ArrangementDrawer arrangementDrawer = new ArrangementDrawer();
	private final BackgroundDrawer backgroundDrawer = new BackgroundDrawer();
	private final MarkerDrawer markerDrawer = new MarkerDrawer();

	public ChartPanel() {
		canvas = new Canvas();
		gc = canvas.getGraphicsContext2D();
		canvas.setHeight(editAreaHeight);
	}

	@Override
	public void init() {
		charterContext.initObject(arrangementDrawer);
		charterContext.initObject(backgroundDrawer);
		charterContext.initObject(markerDrawer);

		// Set up mouse event handlers
		canvas.setOnMousePressed(this::handleMousePressed);
		canvas.setOnMouseReleased(this::handleMouseReleased);
		canvas.setOnMouseMoved(this::handleMouseMoved);
		canvas.setOnMouseDragged(this::handleMouseDragged);
		canvas.setOnScroll(this::handleScroll);

		// Set up keyboard event handlers
		canvas.setOnKeyPressed(this::handleKeyPressed);
		canvas.setOnKeyReleased(this::handleKeyReleased);

		canvas.setFocusTraversable(true);
	}

	private void handleMousePressed(MouseEvent e) {
		mouseHandler.handleMousePressed(e);
	}

	private void handleMouseReleased(MouseEvent e) {
		mouseHandler.handleMouseReleased(e);
	}

	private void handleMouseMoved(MouseEvent e) {
		mouseHandler.handleMouseMoved(e);
	}

	private void handleMouseDragged(MouseEvent e) {
		mouseHandler.handleMouseDragged(e);
	}

	private void handleScroll(ScrollEvent e) {
		mouseHandler.handleScroll(e);
	}

	private void handleKeyPressed(KeyEvent e) {
		keyboardHandler.handleKeyPressed(e);
	}

	private void handleKeyReleased(KeyEvent e) {
		keyboardHandler.handleKeyReleased(e);
	}

	public void repaint() {
		paintComponent();
	}

	private void paintComponent() {
		final double time = chartTimeHandler.time();

		// Create a wrapper to convert JavaFX GraphicsContext to Graphics2D-like API
		final Graphics2DWrapper g2d = new Graphics2DWrapper(gc, (int) canvas.getWidth(), (int) canvas.getHeight());

		backgroundDrawer.draw(g2d, time);

		if (chartData.isEmpty) {
			return;
		}

		arrangementDrawer.draw(g2d, time);
		markerDrawer.draw(g2d, time);
	}

	public void resize() {
		// Resize will be handled by parent layout
	}

	public Node getNode() {
		return canvas;
	}

	public Canvas getCanvas() {
		return canvas;
	}
}
