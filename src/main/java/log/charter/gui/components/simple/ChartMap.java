package log.charter.gui.components.simple;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static log.charter.data.config.ChartPanelColors.getStringBasedColor;
import static log.charter.data.config.GraphicalConfig.chartMapHeightMultiplier;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.chartMapHeight;
import static log.charter.util.ScalingUtils.xToTimeLength;
import static log.charter.util.Utils.getStringPosition;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.function.Predicate;

import log.charter.data.ChartData;
import log.charter.data.config.GraphicalConfig;
import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.data.config.ChartPanelColors.StringColorLabelType;
import log.charter.data.song.BeatsMap.ImmutableBeatsMap;
import log.charter.data.song.EventPoint;
import log.charter.data.song.notes.ChordOrNote;
import log.charter.data.song.vocals.Vocal;
import log.charter.data.song.vocals.Vocal.VocalFlag;
import log.charter.gui.ChartPanel;
import log.charter.gui.CharterFrame;
import log.charter.io.Logger;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.editModes.EditMode;
import log.charter.services.editModes.ModeManager;
import log.charter.util.ExitActions;

public class ChartMap implements Initiable {
	private Canvas canvas;
	private GraphicsContext gc;

	private ChartData chartData;
	private CharterFrame charterFrame;
	private ChartPanel chartPanel;
	private ChartTimeHandler chartTimeHandler;
	private ModeManager modeManager;

	private WritableImage background = null;

	private Thread imageMakerThread;

	public ChartMap() {
		canvas = new Canvas();
		gc = canvas.getGraphicsContext2D();
		canvas.setHeight(chartMapHeight);
	}

	private WritableImage createBackground() {
		final int width = (int) canvas.getWidth();
		final int height = (int) canvas.getHeight();
		final WritableImage img = new WritableImage(width, height);
		final Canvas tempCanvas = new Canvas(width, height);
		final GraphicsContext tempGc = tempCanvas.getGraphicsContext2D();

		tempGc.setFill(convertColor(ColorLabel.LANE.color()));
		tempGc.fillRect(0, 0, width, height);
		if (chartData.isEmpty) {
			tempCanvas.snapshot(null, img);
			return img;
		}

		switch (modeManager.getMode()) {
			case TEMPO_MAP:
				drawBars(tempGc);
				break;
			case VOCALS:
				drawVocalLines(tempGc);
				break;
			case GUITAR:
				drawPhrases(tempGc);
				drawSections(tempGc);
				drawNotes(tempGc);
				break;
			default:
				break;
		}

		drawBookmarks(tempGc);
		tempCanvas.snapshot(null, img);

		return img;
	}

	private Color convertColor(java.awt.Color awtColor) {
		return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(),
			awtColor.getAlpha() / 255.0);
	}

	@Override
	public void init() {
		canvas.setWidth(charterFrame.getStage().getWidth());
		canvas.setFocusTraversable(false);

		// Mouse event handlers
		canvas.setOnMousePressed(this::handleMousePressed);
		canvas.setOnMouseReleased(this::handleMouseReleased);
		canvas.setOnMouseEntered(this::handleMouseEntered);
		canvas.setOnMouseExited(this::handleMouseExited);
		canvas.setOnMouseDragged(this::handleMouseDragged);

		imageMakerThread = new Thread(() -> {
			while (!imageMakerThread.isInterrupted()) {
				try {
					background = createBackground();
				} catch (final Exception e) {
					Logger.error("Couldn't create background for chart map", e);
				}

				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					return;
				}
			}
		});
		imageMakerThread.setName("Chart map painter");
		imageMakerThread.start();

		ExitActions.addOnExit(() -> imageMakerThread.interrupt());
	}

	private int positionToTime(int p) {
		p = max(0, (int) min(canvas.getWidth() - 1, p));
		return (int) ((double) p * chartTimeHandler.maxTime() / (canvas.getWidth() - 1));
	}

	private int timeToPosition(final double t) {
		return (int) (t * (canvas.getWidth() - 1) / chartTimeHandler.maxTime());
	}

	private void drawBars(final GraphicsContext g) {
		g.setStroke(convertColor(ColorLabel.MAIN_BEAT.color()));

		chartData.beats().stream()//
				.filter(beat -> beat.firstInMeasure)//
				.forEach(beat -> {
					final int x = timeToPosition(beat.position());
					final Color color = convertColor((beat.anchor ? ColorLabel.MAIN_BEAT : ColorLabel.SECONDARY_BEAT).color());
					g.setStroke(color);
					g.strokeLine(x, 0, x, canvas.getHeight());
				});
	}

	private void drawVocalLines(final GraphicsContext g) {
		g.setFill(convertColor(ColorLabel.VOCAL_NOTE.color()));
		g.setStroke(convertColor(ColorLabel.VOCAL_NOTE.color()));

		final ImmutableBeatsMap beats = chartData.beats();
		final int y0 = chartMapHeightMultiplier;
		final int y2 = (int) canvas.getHeight() - chartMapHeightMultiplier - 1;
		final int y1 = y0 + chartMapHeightMultiplier;
		boolean started = false;
		int x = 0;

		for (final Vocal vocal : chartData.currentVocals().vocals) {
			if (!started) {
				started = true;
				x = timeToPosition(vocal.position(beats));
			}

			if (vocal.flag() == VocalFlag.PHRASE_END) {
				final int x1 = timeToPosition(vocal.endPosition(beats));

				g.fillRect(x, y1, x1 - x, chartMapHeightMultiplier);
				g.strokeLine(x, y0, x, y2);
				g.strokeLine(x1, y0, x1, y2);
				started = false;
			}
		}
	}

	private void drawEventPoints(final GraphicsContext g, final int y, final ColorLabel color,
			final Predicate<EventPoint> filter) {
		final ImmutableBeatsMap beats = chartData.beats();
		final List<EventPoint> points = chartData.currentArrangement().getFilteredEventPoints(filter);

		for (int i = 0; i < points.size(); i++) {
			final double pointTime = points.get(i).position(beats);
			final double nextPointTime = i + 1 < points.size() ? points.get(i + 1).position(beats)
					: chartTimeHandler.maxTime();

			final int x0 = timeToPosition(pointTime);
			final int x1 = timeToPosition(nextPointTime);
			final int width = max(1, x1 - x0 - 2);

			g.setFill(convertColor(color.color()));
			g.fillRect(x0, y, width, chartMapHeightMultiplier);
			g.setFill(convertColor(color.color().darker()));
			g.fillRect(x1 - 2, y, 2, chartMapHeightMultiplier);
		}
	}

	private void drawPhrases(final GraphicsContext g) {
		drawEventPoints(g, chartMapHeightMultiplier, ColorLabel.PHRASE_NAME_BG, ep -> ep.phrase != null);
	}

	private void drawSections(final GraphicsContext g) {
		drawEventPoints(g, 0, ColorLabel.SECTION_NAME_BG, ep -> ep.section != null);
	}

	private void drawNote(final GraphicsContext g, final int string, final double position, final double length) {
		g.setStroke(convertColor(getStringBasedColor(StringColorLabelType.NOTE, string, chartData.currentStrings())));

		final int x0 = timeToPosition(position);
		final int x1 = timeToPosition(position + length);
		final int y0 = 2 * chartMapHeightMultiplier + 1
				+ getStringPosition(string, chartData.currentStrings()) * chartMapHeightMultiplier;
		final int y1 = y0 + chartMapHeightMultiplier - 1;
		g.strokeLine(x0, y0, x0, y1);
		if (x1 > x0) {
			g.strokeLine(x0, y1, x1, y1);
		}
	}

	private void drawNotes(final GraphicsContext g) {
		final ImmutableBeatsMap beats = chartData.beats();

		chartData.currentArrangementLevel().sounds.stream()//
				.flatMap(ChordOrNote::notes)//
				.forEach(note -> drawNote(g, note.string(), note.position(beats), note.length(beats)));
	}

	private void drawBookmarks(final GraphicsContext g) {
		g.setStroke(convertColor(ColorLabel.BOOKMARK.color()));
		g.setFill(convertColor(ColorLabel.BOOKMARK.color()));

		chartData.songChart.bookmarks.forEach((number, position) -> {
			final int x = timeToPosition(position);
			g.strokeLine(x, 0, x, canvas.getHeight());
			g.fillText(number + "", x + 2, 10);
		});
	}

	private void drawMarkerAndViewArea(final GraphicsContext g) {
		final int markerPosition = timeToPosition(chartTimeHandler.time());

		final int x0 = markerPosition - timeToPosition(xToTimeLength(GraphicalConfig.markerOffset));
		final int x1 = markerPosition
				+ timeToPosition(xToTimeLength((int) chartPanel.getCanvas().getWidth() - GraphicalConfig.markerOffset));
		g.setStroke(convertColor(ColorLabel.MARKER_VIEW_AREA.color()));
		g.strokeRect(x0, 0, x1 - x0, canvas.getHeight() - 1);

		g.setStroke(convertColor(ColorLabel.MARKER.color()));
		g.strokeLine(markerPosition, 0, markerPosition, canvas.getHeight() - 1);
		g.setStroke(convertColor(ColorLabel.MARKER.color().darker()));
		g.strokeLine(markerPosition + 1, 0, markerPosition + 1, canvas.getHeight() - 1);
	}

	public void repaint() {
		if (modeManager.getMode() == EditMode.EMPTY) {
			gc.setFill(convertColor(ColorLabel.BASE_BG_0.color()));
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			return;
		}

		if (background != null) {
			gc.drawImage(background, 0, 0);
		} else {
			gc.setFill(convertColor(ColorLabel.BASE_BG_0.color()));
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}

		drawMarkerAndViewArea(gc);
	}

	private void handleMousePressed(final MouseEvent e) {
		chartTimeHandler.nextTime(positionToTime((int) e.getX()));
	}

	private void handleMouseReleased(final MouseEvent e) {
	}

	private void handleMouseEntered(final MouseEvent e) {
		canvas.requestFocus();
	}

	private void handleMouseExited(final MouseEvent e) {
		charterFrame.getStage().getScene().getRoot().requestFocus();
	}

	private void handleMouseDragged(final MouseEvent e) {
		chartTimeHandler.nextTime(positionToTime((int) e.getX()));
	}

	public void triggerRedraw() {
		new Thread(() -> {
			try {
				background = createBackground();
			} catch (final Exception e) {
				Logger.error("Couldn't create background for chart map", e);
			}
		}).start();
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
