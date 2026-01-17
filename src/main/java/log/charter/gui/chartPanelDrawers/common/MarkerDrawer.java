package log.charter.gui.chartPanelDrawers.common;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.data.config.GraphicalConfig;
import log.charter.gui.chartPanelDrawers.drawableShapes.TextWithBackground;
import log.charter.util.data.Position2D;

public class MarkerDrawer {
	private static final int arrowSize = 8;
	private static final Font font = Font.font("Dialog", 10);

	private void drawLine(final GraphicsContext gc, final int x) {
		gc.setStroke(ColorLabel.MARKER.color());
		gc.setLineWidth(1);
		gc.strokeLine(x, DrawerUtils.beatTextY - 5, x, DrawerUtils.lanesBottom);
	}

	private void drawArrow(final GraphicsContext gc, final int x) {
		gc.setFill(ColorLabel.MARKER.color());
		final double[] xPoints = { x - arrowSize / 2.0, x + arrowSize / 2.0, (double) x };
		final double[] yPoints = { DrawerUtils.beatTextY - 10.0, DrawerUtils.beatTextY - 10.0, DrawerUtils.beatTextY - 10.0 + arrowSize };
		gc.fillPolygon(xPoints, yPoints, 3);
	}

	private void drawTime(final GraphicsContext gc, final int x, final double time) {
		final int hours = (int) (time / 3_600_000);
		final int minutes = ((int) (time / 60_000)) % 60;
		final int seconds = ((int) time / 1000) % 60;
		final int miliseconds = ((int) time) % 1000;

		final Position2D position = new Position2D(x + arrowSize / 2 + 2, 2);
		final String timeString = "%d:%02d:%02d.%03d".formatted(hours, minutes, seconds, miliseconds);

		new TextWithBackground(position, font, timeString, ColorLabel.MARKER_TIME, ColorLabel.MARKER_TIME_BACKGROUND,
				ColorLabel.MARKER_TIME_BACKGROUND).draw(gc);
	}

	public void draw(final GraphicsContext gc, final double time) {
		final int x = GraphicalConfig.markerOffset;

		drawLine(gc, x);
		drawArrow(gc, x);
		drawTime(gc, x, time);
	}
}
