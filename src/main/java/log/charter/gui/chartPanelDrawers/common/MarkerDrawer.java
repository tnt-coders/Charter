package log.charter.gui.chartPanelDrawers.common;

import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.beatTextY;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.lanesBottom;

import java.awt.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.data.config.GraphicalConfig;
import log.charter.gui.chartPanelDrawers.drawableShapes.TextWithBackground;
import log.charter.util.data.Position2D;

public class MarkerDrawer {
	private static final int arrowSize = 8;
	private static final Font font = new Font(Font.DIALOG, Font.PLAIN, 10);

	private void drawLine(final GraphicsWrapper g, final int x) {
		g.setStroke(1);
		g.drawLine(x, beatTextY - 5, x, lanesBottom);
	}

	private void drawArrow(final GraphicsWrapper g, final int x) {
		final int[] xPoints = { x - arrowSize / 2, x + arrowSize / 2, x };
		final int[] yPoints = { beatTextY - 10, beatTextY - 10, beatTextY - 10 + arrowSize };
		g.fillPolygon(xPoints, yPoints, 3);
	}

	private void drawTime(final GraphicsWrapper g, final int x, final double time) {
		final int hours = (int) (time / 3_600_000);
		final int minutes = ((int) (time / 60_000)) % 60;
		final int seconds = ((int) time / 1000) % 60;
		final int miliseconds = ((int) time) % 1000;

		final Position2D position = new Position2D(x + arrowSize / 2 + 2, 2);
		final String timeString = "%d:%02d:%02d.%03d".formatted(hours, minutes, seconds, miliseconds);

		new TextWithBackground(position, font, timeString, ColorLabel.MARKER_TIME, ColorLabel.MARKER_TIME_BACKGROUND,
				ColorLabel.MARKER_TIME_BACKGROUND).draw(g);
	}

	public void draw(final GraphicsWrapper g, final double time) {
		final int x = GraphicalConfig.markerOffset;

		g.setColor(ColorLabel.MARKER.color());

		drawLine(g, x);
		drawArrow(g, x);
		drawTime(g, x, time);
	}
}
