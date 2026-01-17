package log.charter.gui.chartPanelDrawers.common;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import log.charter.data.ChartData;
import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.ChartPanel;
import log.charter.gui.chartPanelDrawers.drawableShapes.CenteredText;
import log.charter.gui.chartPanelDrawers.drawableShapes.ShapePositionWithSize;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.editModes.EditMode;
import log.charter.services.editModes.ModeManager;
import log.charter.util.Utils.TimeUnit;
import log.charter.util.data.Position2D;

public class BackgroundDrawer {
	private static final int[] jumpDistances = { 100, 250, 1000, 2000, 5000, 10_000, 30_000, 60_000, 120_000, 300_000,
			600_000, 3_600_000 };

	private static int nonsecondsMarkerBottom = timingY + 12;
	private static int secondsMarkerBottom = timingY + 24;
	private static int textY = timingY + 30;
	private static Font timeFont = Font.font("System", FontWeight.BOLD, 12);

	public static void reloadGraphics() {
		final int y1 = timingY;
		final int y2 = editAreaHeight;

		final int h = y2 - y1;
		nonsecondsMarkerBottom = timingY + (int) (h * 0.2);
		secondsMarkerBottom = timingY + (int) (h * 0.5);
		textY = timingY + (int) (h * 0.65);
		timeFont = Font.font("System", FontWeight.BOLD, (int) (h * 0.4));
	}

	static {
		reloadGraphics();
	}

	private ChartData chartData;
	private ChartPanel chartPanel;
	private ChartTimeHandler chartTimeHandler;
	private ModeManager modeManager;

	private void drawBackground(final GraphicsContext gc) {
		gc.setFill(ColorLabel.BASE_BG_0.color());
		gc.fillRect(0, 0, chartPanel.getWidth(), chartPanel.getHeight());
	}

	private void drawEventPointsBackground(final GraphicsContext gc) {
		gc.setFill(ColorLabel.BASE_BG_2.color());
		final int y = DrawerUtils.sectionNamesY;
		final int h = DrawerUtils.toneChangeY - y;
		gc.fillRect(0, y, chartPanel.getWidth(), h);
	}

	private void drawToneChangesBackground(final GraphicsContext gc) {
		gc.setFill(ColorLabel.BASE_BG_1.color());
		final int y = DrawerUtils.toneChangeY;
		final int h = DrawerUtils.fhpY - y;
		gc.fillRect(0, y, chartPanel.getWidth(), h);
	}

	private void drawFHPsBackground(final GraphicsContext gc) {
		gc.setFill(ColorLabel.BASE_BG_0.color());
		final int y = DrawerUtils.fhpY;
		final int h = DrawerUtils.lanesTop - y;
		gc.fillRect(0, y, chartPanel.getWidth(), h);
	}

	private void drawLanesBackground(final GraphicsContext gc) {
		gc.setFill(ColorLabel.LANE.color());
		gc.fillRect(0, DrawerUtils.lanesTop, chartPanel.getWidth(), DrawerUtils.lanesBottom - DrawerUtils.lanesTop + 1);
	}

	private int calculateJump(final double startTime, final double endTime) {
		final double dt = endTime - startTime;
		final int maxJumpsA = (int) chartPanel.getWidth() / 10;
		final int maxJumpsB = (int) chartPanel.getWidth() / 40;
		for (int i = 0; i < jumpDistances.length; i++) {
			if (dt < jumpDistances[i] * (jumpDistances[i] <= 250 ? maxJumpsA : maxJumpsB)) {
				return jumpDistances[i];
			}
		}

		return jumpDistances[jumpDistances.length - 1];
	}

	private void drawTimestamp(final GraphicsContext gc, final double time, final int timestampTime) {
		final int x = positionToX(timestampTime, time);
		if (timestampTime % 1000 == 0) {
			filledRectangle(new ShapePositionWithSize(x, lanesBottom + 1, 1, secondsMarkerBottom - lanesBottom - 1),
					ColorLabel.BASE_DARK_TEXT).draw(gc);

			final String formattedTime = formatTime(timestampTime / 1000, TimeUnit.SECONDS, TimeUnit.MINUTES,
					TimeUnit.HOURS);
			new CenteredText(new Position2D(x, textY), timeFont, formattedTime, ColorLabel.BASE_DARK_TEXT).draw(gc);
		} else {
			lineVertical(x, lanesBottom + 1, nonsecondsMarkerBottom, ColorLabel.BASE_DARK_TEXT).draw(gc);
		}
	}

	private void drawTimeScale(final GraphicsContext gc, final double time) {
		int timestampTime = (int) xToPosition(-20, time);
		if (timestampTime < 0) {
			timestampTime = 0;
		}

		final double endTime = xToPosition((int) chartPanel.getWidth() + 20, time);
		final int jump = calculateJump(timestampTime, endTime);
		timestampTime -= timestampTime % jump;

		while (timestampTime <= endTime && timestampTime < chartTimeHandler.maxTime()) {
			drawTimestamp(gc, time, timestampTime);
			timestampTime += jump;
		}
	}

	private void drawStartAndEnd(final GraphicsContext gc, final double time) {
		gc.setStroke(ColorLabel.MARKER.color());
		gc.setLineWidth(1);
		final int startX = positionToX(0, time);
		gc.strokeLine(startX, lanesTop + 30, startX, lanesBottom - 30);
		final int endX = positionToX(chartTimeHandler.maxTime(), time);
		gc.strokeLine(endX, lanesTop + 30, endX, lanesBottom - 30);
	}

	public void draw(final GraphicsContext gc, final double time) {
		drawBackground(gc);

		if (chartData.isEmpty) {
			return;
		}

		if (modeManager.getMode() == EditMode.GUITAR) {
			drawEventPointsBackground(gc);
			drawToneChangesBackground(gc);
			drawFHPsBackground(gc);
		}

		drawLanesBackground(gc);
		drawTimeScale(gc, time);
		drawStartAndEnd(gc, time);
	}
}
