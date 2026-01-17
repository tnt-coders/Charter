package log.charter.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import log.charter.data.ChartData;
import log.charter.gui.chartPanelDrawers.ArrangementDrawer;
import log.charter.gui.chartPanelDrawers.common.BackgroundDrawer;
import log.charter.gui.chartPanelDrawers.common.MarkerDrawer;
import log.charter.services.CharterContext;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.mouseAndKeyboard.KeyboardHandler;
import log.charter.services.mouseAndKeyboard.MouseHandler;

public class ChartPanel extends Canvas implements Initiable {

	private CharterContext charterContext;
	private ChartData chartData;
	private ChartTimeHandler chartTimeHandler;
	private KeyboardHandler keyboardHandler;
	private MouseHandler mouseHandler;

	private final ArrangementDrawer arrangementDrawer = new ArrangementDrawer();
	private final BackgroundDrawer backgroundDrawer = new BackgroundDrawer();
	private final MarkerDrawer markerDrawer = new MarkerDrawer();

	public ChartPanel() {
		super(1, 200); // Default size
	}

	@Override
	public void init() {
		charterContext.initObject(arrangementDrawer);
		charterContext.initObject(backgroundDrawer);
		charterContext.initObject(markerDrawer);

		setOnMousePressed(mouseHandler::handleMousePressed);
		// TODO: Implement other mouse and keyboard event handlers for JavaFX
	}

	public void repaint() {
		final double time = chartTimeHandler.time();
		final GraphicsContext gc = getGraphicsContext2D();
		backgroundDrawer.draw(gc, time);
		if (!chartData.isEmpty) {
			arrangementDrawer.draw(gc, time);
			markerDrawer.draw(gc, time);
		}
	}
}
