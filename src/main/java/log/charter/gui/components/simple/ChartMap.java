package log.charter.gui.components.simple;

import static java.lang.Math.max;
import static java.lang.Math.min;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import log.charter.data.ChartData;
import log.charter.gui.ChartPanel;
import log.charter.gui.CharterFrame;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.editModes.ModeManager;

public class ChartMap extends Canvas implements Initiable {

	private ChartData chartData;
	private CharterFrame charterFrame;
	private ChartPanel chartPanel;
	private ChartTimeHandler chartTimeHandler;
	private ModeManager modeManager;

	public ChartMap() {
		super(1, 20);
	}

	@Override
	public void init() {
		setOnMousePressed(this::onMousePressed);
		setOnMouseDragged(this::onMouseDragged);
	}

	private int positionToTime(int p) {
		p = (int) Math.max(0, Math.min(getWidth() - 1, p));
		return (int) ((double) p * chartTimeHandler.maxTime() / (getWidth() - 1));
	}

	private void onMousePressed(final MouseEvent e) {
		if (chartData.isEmpty) {
			return;
		}

		chartTimeHandler.nextTime(positionToTime((int) e.getX()));
	}

	private void onMouseDragged(final MouseEvent e) {
		if (chartData.isEmpty) {
			return;
		}

		chartTimeHandler.nextTime(positionToTime((int) e.getX()));
	}

	public void triggerRedraw() {
	}
}
