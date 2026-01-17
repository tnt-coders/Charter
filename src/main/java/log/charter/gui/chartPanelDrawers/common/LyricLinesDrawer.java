package log.charter.gui.chartPanelDrawers.common;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import log.charter.data.ChartData;
import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.data.song.BeatsMap.ImmutableBeatsMap;
import log.charter.data.song.vocals.Vocal;
import log.charter.data.song.vocals.Vocal.VocalFlag;
import log.charter.data.song.vocals.VocalPath;
import log.charter.gui.chartPanelDrawers.data.FrameData;
import log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShapeList;
import log.charter.gui.chartPanelDrawers.drawableShapes.ShapePositionWithSize;
import log.charter.gui.chartPanelDrawers.drawableShapes.Text;
import log.charter.services.editModes.EditMode;
import log.charter.services.editModes.ModeManager;
import log.charter.util.data.Position2D;

public class LyricLinesDrawer {
	private static int height = lyricLinesY - beatSizeTextY;
	private static Font lyricLineFont = Font.font("Dialog", FontPosture.ITALIC, height - 3);

	public static void reloadGraphics() {
		height = lyricLinesY - beatSizeTextY;
		lyricLineFont = Font.font("Dialog", FontPosture.ITALIC, height - 3);
	}

	private static class VocalLinesDrawingData {
		private final DrawableShapeList backgrounds = new DrawableShapeList();
		private final DrawableShapeList texts = new DrawableShapeList();

		public void addLyricLine(final String text, final int x, final int lengthPx, final Color color) {
			final ShapePositionWithSize backgroundPosition = new ShapePositionWithSize(x, lyricLinesY + 3, lengthPx,
					height);
			backgrounds.add(filledRectangle(backgroundPosition, ColorLabel.VOCAL_LINE_BACKGROUND.color(), true));

			final Position2D textPosition = new Position2D(x + 3, lyricLinesY + 6);
			texts.add(new Text(textPosition, lyricLineFont, text, color));
		}

		public void draw(final GraphicsContext gc) {
			reloadGraphics();
			backgrounds.draw(gc);
			texts.draw(gc);
		}
	}

	private ChartData chartData;
	private ModeManager modeManager;

	public void draw(final FrameData frameData) {
		if (modeManager.getMode() == EditMode.EMPTY) {
			return;
		}

		final VocalLinesDrawingData drawingData = new VocalLinesDrawingData();
		String currentLine = "";
		boolean started = false;
		int x = 0;
		final ImmutableBeatsMap beats = chartData.beats();
		final VocalPath vocalPath = chartData.currentVocals();

		for (final Vocal vocal : vocalPath.vocals) {
			if (!started) {
				started = true;
				x = positionToX(vocal.position(beats), frameData.time);
			}

			currentLine += vocal.text();
			if (vocal.flag() != VocalFlag.WORD_PART) {
				currentLine += " ";
			}

			if (vocal.flag() == VocalFlag.PHRASE_END) {
				drawingData.addLyricLine(currentLine, x, positionToX(vocal.endPosition(beats), frameData.time) - x,
						vocalPath.color);
				currentLine = "";
				started = false;
			}
		}

		drawingData.draw(frameData.gc);
	}
}
