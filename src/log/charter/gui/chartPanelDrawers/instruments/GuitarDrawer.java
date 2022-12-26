package log.charter.gui.chartPanelDrawers.instruments;

import static java.lang.Math.min;
import static java.util.stream.Collectors.minBy;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.anchorTextY;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.anchorY;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.getAsOdd;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.getLaneSize;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.getLaneY;
import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.lanesBottom;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.centeredImage;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.centeredTextWithBackground;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.filledOval;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.filledRectangle;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.filledTriangle;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.line;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.lineHorizontal;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.lineVertical;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.strokedRectangle;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.strokedTriangle;
import static log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShape.text;
import static log.charter.util.ScalingUtils.timeToX;
import static log.charter.util.ScalingUtils.timeToXLength;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import log.charter.data.ChartData;
import log.charter.data.managers.selection.ChordOrNote;
import log.charter.data.managers.selection.SelectionManager;
import log.charter.data.types.PositionType;
import log.charter.gui.ChartPanel;
import log.charter.gui.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.AudioDrawer;
import log.charter.gui.chartPanelDrawers.common.BeatsDrawer;
import log.charter.gui.chartPanelDrawers.drawableShapes.DrawableShapeList;
import log.charter.gui.chartPanelDrawers.drawableShapes.ShapePositionWithSize;
import log.charter.gui.handlers.KeyboardHandler;
import log.charter.song.Anchor;
import log.charter.song.ArrangementChart;
import log.charter.song.BendValue;
import log.charter.song.ChordTemplate;
import log.charter.song.HandShape;
import log.charter.song.Level;
import log.charter.song.enums.HOPO;
import log.charter.song.enums.Harmonic;
import log.charter.song.enums.Mute;
import log.charter.song.notes.Chord;
import log.charter.song.notes.Note;
import log.charter.util.CollectionUtils.ArrayList2;
import log.charter.util.CollectionUtils.HashSet2;
import log.charter.util.Position2D;

public class GuitarDrawer {
	public static final int noteWidth = 23;
	public static final int noteHeight = getLaneSize(6);
	public static final int tailHeight = getAsOdd(noteHeight * 3 / 4);
	public static final BigDecimal bendStepSize = new BigDecimal("10");

	private static BufferedImage loadImage(final String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (final IOException e) {
			e.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}

	private static final BufferedImage palmMuteMarker = loadImage("images/palmMute.png");

	private final static Color selectColor = ColorLabel.SELECT.color();
	private static final Color[] noteColors = new Color[6];
	private static final Color[] noteAccentColors = new Color[6];
	private static final Color[] noteTailColors = new Color[6];
	private static final Color anchorColor = ColorLabel.ANCHOR.color();
	private static final Color handShapeColor = ColorLabel.HAND_SHAPE.color();

	private static final Font anchorFont = new Font(Font.DIALOG, Font.BOLD, 13);
	private static final Font bendValueFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
	private static final Font fretFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);

	static {
		for (int i = 0; i < 6; i++) {
			noteColors[i] = ColorLabel.valueOf("NOTE_" + i).color();
			noteAccentColors[i] = ColorLabel.valueOf("NOTE_ACCENT_" + i).color();
			noteTailColors[i] = ColorLabel.valueOf("NOTE_TAIL_" + i).color();
		}
	}

	private class NoteData {
		public final int x;
		public final int length;

		public final int string;
		public final int fretNumber;
		public final String fret;
		public final boolean accent;
		public final Mute mute;
		public final HOPO hopo;
		public final Harmonic harmonic;
		public final ArrayList2<BendValue> bendValues;
		public final Integer slideTo;
		public final boolean unpitchedSlide;

		public final boolean selected;
		public final boolean linkPrevious;

		public NoteData(final int x, final int length, final Note note, final boolean selected,
				final boolean lastWasLinkNext) {
			this(x, length, note.string, note.fret, note.fret + "", note.accent, note.mute, note.hopo, note.harmonic,
					note.bendValues, note.slideTo, note.unpitchedSlide, selected, lastWasLinkNext);
		}

		private NoteData(final int x, final int length, final int string, final int fretNumber, final String fret,
				final boolean accent, final Mute mute, final HOPO hopo, final Harmonic harmonic,
				final ArrayList2<BendValue> bendValues, final Integer slideTo, final boolean unpitchedSlide,
				final boolean selected, final boolean lastWasLinkNext) {
			this.x = x;
			this.length = length;

			this.string = string;
			this.fretNumber = fretNumber;
			this.fret = fret + (mute == Mute.STRING ? "X" : "");
			this.accent = accent;
			this.mute = mute;
			this.hopo = hopo;
			this.harmonic = harmonic;
			this.bendValues = bendValues;
			this.slideTo = slideTo;
			this.unpitchedSlide = unpitchedSlide;

			this.selected = selected;
			linkPrevious = lastWasLinkNext;
		}
	}

	private ArrayList2<NoteData> fromChord(final Chord chord, final ChordTemplate chordTemplate, final int x,
			final int length, final boolean selected, final boolean lastWasLinkNext, final boolean ctrl) {
		final ArrayList2<NoteData> notes = new ArrayList2<>();
		Integer slideDistance;
		try {
			slideDistance = chord.slideTo == null ? null//
					: chord.slideTo - chordTemplate.frets.values().stream().collect(minBy(Integer::compare)).get();
		} catch (final Exception e) {
			slideDistance = null;
		}

		for (final Entry<Integer, Integer> chordFret : chordTemplate.frets.entrySet()) {
			final int string = chordFret.getKey();
			final int fret = chordFret.getValue();
			final String fretDescription = fret + (ctrl ? "(" + chordTemplate.fingers.get(string) + ")" : "");
			final Integer slideTo = slideDistance == null ? null : (fret + slideDistance);

			notes.add(new NoteData(x, length, string, fret, fretDescription, chord.accent, chord.mute, chord.hopo,
					chord.harmonic, chord.bendValues.get(string), slideTo, chord.unpitchedSlide, selected,
					lastWasLinkNext));
		}

		return notes;
	}

	private class DrawingData {
		private final int[] stringPositions;

		private final DrawableShapeList anchors;
		private final DrawableShapeList bendValues;
		private final DrawableShapeList handShapes;
		private final DrawableShapeList noteTails;
		private final DrawableShapeList noteTailSelects;
		private final DrawableShapeList notes;
		private final DrawableShapeList noteFrets;
		private final DrawableShapeList selects;
		private final DrawableShapeList slideFrets;

		public DrawingData(final int strings) {
			stringPositions = new int[strings];
			for (int i = 0; i < strings; i++) {
				stringPositions[i] = getLaneY(i, strings);
			}

			anchors = new DrawableShapeList();
			bendValues = new DrawableShapeList();
			handShapes = new DrawableShapeList();
			noteTails = new DrawableShapeList();
			noteTailSelects = new DrawableShapeList();
			notes = new DrawableShapeList();
			noteFrets = new DrawableShapeList();
			selects = new DrawableShapeList();
			slideFrets = new DrawableShapeList();
		}

		private void addHammerOnShape(final int y, final NoteData note) {
			final Position2D a = new Position2D(note.x, y - noteHeight / 2);
			final Position2D b = new Position2D(note.x - noteWidth / 2, y + noteHeight / 2);
			final Position2D c = new Position2D(note.x + noteWidth / 2, y + noteHeight / 2);
			notes.add(filledTriangle(a, b, c, noteColors[note.string]));

			if (note.selected) {
				selects.add(strokedTriangle(a, b, c, selectColor));
			}
		}

		private void addPullOffShape(final int y, final NoteData note) {
			final Position2D a = new Position2D(note.x, y + noteHeight / 2);
			final Position2D b = new Position2D(note.x - noteWidth / 2, y - noteHeight / 2);
			final Position2D c = new Position2D(note.x + noteWidth / 2, y - noteHeight / 2);
			notes.add(filledTriangle(a, b, c, noteColors[note.string]));

			if (note.selected) {
				selects.add(strokedTriangle(a, b, c, selectColor));
			}
		}

		private void addHarmonicShape(final int y, final NoteData note) {
			final ShapePositionWithSize position = new ShapePositionWithSize(note.x, y, noteWidth, noteHeight)
					.centered();

			notes.add(filledRectangle(position, noteColors[note.string]));

			final ShapePositionWithSize harmonicPosition = new ShapePositionWithSize(note.x, y - 1, noteWidth + 5,
					noteWidth + 5).centered();
			notes.add(filledOval(harmonicPosition, new Color(224, 224, 224)));
			notes.add(filledOval(harmonicPosition.resized(5, 5, -10, -10), noteColors[note.string]));

			if (note.selected) {
				selects.add(strokedRectangle(position.resized(-1, -1, 1, 1), selectColor));
			}
		}

		private void addPinchHarmonicShape(final int y, final NoteData note) {
			final ShapePositionWithSize position = new ShapePositionWithSize(note.x, y, noteWidth, noteHeight)//
					.centered();

			notes.add(filledRectangle(position, noteColors[note.string]));
			notes.add(filledOval(position.resized(-2, -2, 3, 3), ColorLabel.BASE_BG_1.color()));
			notes.add(lineHorizontal(note.x - noteWidth / 2 - 2, note.x + noteWidth / 2 + 2, y,
					ColorLabel.valueOf("LANE_" + note.string).color()));
			notes.add(filledOval(position.resized(2, 2, -5, -5), noteColors[note.string]));
			notes.add(filledOval(position.resized(5, 5, -11, -11), ColorLabel.BASE_BG_1.color()));
			notes.add(lineHorizontal(note.x - noteWidth / 2 + 5, note.x + noteWidth / 2 - 5, y,
					ColorLabel.valueOf("LANE_" + note.string).color()));

			if (note.selected) {
				selects.add(strokedRectangle(position.resized(-1, -1, 1, 1), selectColor));
			}
		}

		private void addNormalNoteShape(final int y, final NoteData note) {
			if (note.linkPrevious) {
				return;
			}

			final ShapePositionWithSize position = new ShapePositionWithSize(note.x, y, noteWidth, noteHeight)//
					.centered();

			notes.add(filledRectangle(position, noteColors[note.string]));
			if (note.accent) {
				final Color accentColor = noteAccentColors[note.string];
				notes.add(strokedRectangle(position.resized(0, 0, -1, -1), accentColor));
				notes.add(strokedRectangle(position.resized(-1, -1, 1, 1), accentColor));
				notes.add(strokedRectangle(position.resized(-2, -2, 3, 3), accentColor));
			}

			if (note.selected) {
				selects.add(strokedRectangle(position.resized(-1, -1, 1, 1), selectColor));
			}
		}

		private void addNoteShape(final NoteData note, final int y) {
			if (note.hopo == HOPO.HAMMER_ON) {
				addHammerOnShape(y, note);
			} else if (note.hopo == HOPO.PULL_OFF) {
				addPullOffShape(y, note);
			} else if (note.harmonic == Harmonic.NORMAL) {
				addHarmonicShape(y, note);
			} else if (note.harmonic == Harmonic.PINCH) {
				addPinchHarmonicShape(y, note);
			} else {
				addNormalNoteShape(y, note);
			}
		}

		private void addPalmMute(final NoteData note, final int y) {
			notes.add(centeredImage(new Position2D(note.x, y), palmMuteMarker));
		}

		private void addSimpleNote(final NoteData note) {
			final int y = stringPositions[note.string];
			addNoteShape(note, y);

			if (note.mute == Mute.PALM) {
				addPalmMute(note, y);
			}

			if (!note.linkPrevious) {
				noteFrets.add(centeredTextWithBackground(new Position2D(note.x, y), note.fret, Color.WHITE,
						note.mute == Mute.STRING ? Color.GRAY : Color.BLACK));
			}

			if (note.length > 0) {
				addNoteTail(note, y);
			}
		}

		private void addSlideCommon(final NoteData note, final int y, final Color backgroundColor,
				final Color fretColor) {
			final int topY = y - tailHeight / 2;
			final int bottomY = y + tailHeight / 2 + 1;
			final Position2D a = new Position2D(note.x, topY);
			final Position2D b = new Position2D(note.x, bottomY);
			final int tailEndY = note.slideTo < note.fretNumber ? bottomY : topY;
			final Position2D c = new Position2D(note.x + note.length, tailEndY);

			noteTails.add(filledTriangle(a, b, c, noteTailColors[note.string]));
			slideFrets.add(centeredTextWithBackground(c, note.slideTo + "", backgroundColor, fretColor));
			if (note.selected) {
				noteTailSelects.add(strokedTriangle(a, b, c, selectColor));
			}
		}

		private void addSlideNoteTailShape(final NoteData note, final int y) {
			addSlideCommon(note, y, ColorLabel.SLIDE_NORMAL_FRET_BG.color(), ColorLabel.SLIDE_NORMAL_FRET_TEXT.color());
		}

		private void addUnpitchedSlideNoteTailShape(final NoteData note, final int y) {
			addSlideCommon(note, y, ColorLabel.SLIDE_UNPITCHED_FRET_BG.color(),
					ColorLabel.SLIDE_UNPITCHED_FRET_TEXT.color());
		}

		private void addNormalNoteTailShape(final NoteData note, final int y) {
			final ShapePositionWithSize position = new ShapePositionWithSize(note.x - 1, y, note.length + 2, tailHeight)
					.centeredY();
			noteTails.add(filledRectangle(position, noteTailColors[note.string]));
			if (note.selected) {
				noteTailSelects.add(strokedRectangle(position.resized(0, -1, 0, 1), selectColor));
			}
		}

		private String formatBendValue(final BigDecimal bendValue) {
			if (bendValue == null) {
				return "0";
			}

			try {
				return bendValue.intValueExact() + "";
			} catch (final ArithmeticException e) {
				return bendValue.setScale(1, RoundingMode.HALF_UP).toString();
			}
		}

		private int getBendLineY(final int y, final BigDecimal bendValue) {
			final int bendOffset = min(tailHeight, bendValue == null ? 0
					: bendValue.multiply(new BigDecimal(tailHeight)).divide(new BigDecimal(4)).intValue());
			return y + tailHeight / 2 - bendOffset;
		}

		private void addBendValues(final NoteData note, final int y) {
			Position2D lastBendLinePosition = new Position2D(note.x, getBendLineY(y, BigDecimal.ZERO));

			for (final BendValue bendValue : note.bendValues) {
				final int bendX = timeToX(bendValue.position, data.time);
				final int bendY = bendX > note.x + noteWidth ? y : y - 26;

				final Position2D lineTo = new Position2D(bendX, getBendLineY(y, bendValue.bendValue));

				final Position2D position = new Position2D(bendX, bendY);
				final String text = "ノ" + formatBendValue(bendValue.bendValue);

				noteTails.add(line(lastBendLinePosition, lineTo, Color.WHITE));
				bendValues.add(centeredTextWithBackground(position, text, Color.BLACK, Color.WHITE));

				lastBendLinePosition = lineTo;
			}

			noteTails.add(line(lastBendLinePosition, new Position2D(note.x + note.length, lastBendLinePosition.y),
					Color.WHITE));
		}

		private void addNoteTail(final NoteData note, final int y) {
			if (note.slideTo != null) {
				if (note.unpitchedSlide) {
					addUnpitchedSlideNoteTailShape(note, y);
				} else {
					addSlideNoteTailShape(note, y);
				}
			} else {
				addNormalNoteTailShape(note, y);
			}

			if (note.bendValues != null && !note.bendValues.isEmpty()) {
				addBendValues(note, y);
			}
		}

		public void addNote(final Note note, final int x, final int length, final boolean selected,
				final boolean lastWasLinkNext) {
			addSimpleNote(new NoteData(x, length, note, selected, lastWasLinkNext));
		}

		public void addChord(final Chord chord, final ChordTemplate chordTemplate, final int x, final int length,
				final boolean selected, final boolean lastWasLinkNext, final boolean ctrl) {
			for (final NoteData noteData : fromChord(chord, chordTemplate, x, length, selected, lastWasLinkNext,
					ctrl)) {
				addSimpleNote(noteData);
			}
		}

		public void addAnchor(final Anchor anchor, final int x, final boolean selected) {
			anchors.add(lineVertical(x, anchorY, lanesBottom, anchorColor));
			anchors.add(text(new Position2D(x + 4, anchorTextY), "" + anchor.fret, anchorColor));

			if (selected) {
				final int top = anchorY - 1;
				final int bottom = lanesBottom + 1;
				final ShapePositionWithSize anchorPosition = new ShapePositionWithSize(x - 1, top, 2, bottom - top);
				selects.add(strokedRectangle(anchorPosition, selectColor));
			}
		}

		public void addHandShape(final int x, final int length, final boolean selected) {
			final ShapePositionWithSize position = new ShapePositionWithSize(x, lanesBottom, length, 10);
			handShapes.add(filledRectangle(position, handShapeColor));

			if (selected) {
				selects.add(strokedRectangle(position, selectColor));
			}
		}

		public void draw(final Graphics g) {
			g.setFont(anchorFont);
			anchors.draw(g);
			noteTails.draw(g);

			g.setFont(bendValueFont);
			bendValues.draw(g);
			slideFrets.draw(g);
			noteTailSelects.draw(g);
			notes.draw(g);

			g.setFont(fretFont);
			noteFrets.draw(g);
			handShapes.draw(g);
			selects.draw(g);
		}
	}

	private static boolean isPastRightEdge(final int x, final int width) {
		return x > (width + noteWidth / 2);
	}

	private static boolean isOnScreen(final int x, final int length) {
		return x + length >= 0;
	}

	private AudioDrawer audioDrawer;
	private BeatsDrawer beatsDrawer;
	protected ChartData data;
	private ChartPanel chartPanel;
	private KeyboardHandler keyboardHandler;
	private SelectionManager selectionManager;

	public void init(final AudioDrawer audioDrawer, final BeatsDrawer beatsDrawer, final ChartData data,
			final ChartPanel chartPanel, final KeyboardHandler keyboardHandler,
			final SelectionManager selectionManager) {
		this.audioDrawer = audioDrawer;
		this.beatsDrawer = beatsDrawer;
		this.data = data;
		this.chartPanel = chartPanel;
		this.keyboardHandler = keyboardHandler;
		this.selectionManager = selectionManager;
	}

	private void drawGuitarLanes(final Graphics g) {
		final int lanes = data.getCurrentArrangement().tuning.strings;
		final int width = chartPanel.getWidth();

		final int x = timeToX(0, data.time);

		for (int i = 0; i < lanes; i++) {
			g.setColor(ColorLabel.valueOf("LANE_" + i).color());
			final int y = getLaneY(i, lanes);
			g.drawLine(x, y, width, y);
		}
	}

	private void addAnchors(final DrawingData drawingData, final Level level, final int panelWidth) {
		final HashSet2<Integer> selectedAnchorIds = selectionManager.getSelectedAccessor(PositionType.ANCHOR)//
				.getSelectedSet().map(selection -> selection.id);

		for (int i = 0; i < level.anchors.size(); i++) {
			final Anchor anchor = level.anchors.get(i);
			final int x = timeToX(anchor.position, data.time);
			if (isPastRightEdge(x, panelWidth)) {
				break;
			}

			if (!isOnScreen(x, 20)) {
				continue;
			}

			final boolean selected = selectedAnchorIds.contains(i);
			drawingData.addAnchor(anchor, x, selected);
		}
	}

	private boolean addChord(final DrawingData drawingData, final ArrangementChart arrangement, final int panelWidth,
			final Chord chord, final boolean selected, final boolean lastWasLinkNext) {
		final int x = timeToX(chord.position, data.time);
		if (isPastRightEdge(x, panelWidth)) {
			return false;
		}

		final int length = timeToXLength(chord.length);
		if (!isOnScreen(x, length)) {
			return true;
		}

		final ChordTemplate chordTemplate = arrangement.chordTemplates.get(chord.chordId);
		drawingData.addChord(chord, chordTemplate, x, length, selected, lastWasLinkNext, keyboardHandler.ctrl());
		return true;
	}

	private boolean addNote(final DrawingData drawingData, final int panelWidth, final Note note,
			final boolean selected, final boolean lastWasLinkNext) {
		final int x = timeToX(note.position, data.time);
		final int length = timeToXLength(note.length);
		if (isPastRightEdge(x, panelWidth)) {
			return false;
		}

		if (!isOnScreen(x, length)) {
			return true;
		}

		drawingData.addNote(note, x, length, selected, lastWasLinkNext);

		return true;
	}

	private boolean addChordOrNote(final DrawingData drawingData, final ArrangementChart arrangement,
			final int panelWidth, final ChordOrNote chordOrNote, final boolean selected,
			final boolean lastWasLinkNext) {
		if (chordOrNote.chord != null) {
			return addChord(drawingData, arrangement, panelWidth, chordOrNote.chord, selected, lastWasLinkNext);
		}
		if (chordOrNote.note != null) {
			return addNote(drawingData, panelWidth, chordOrNote.note, selected, lastWasLinkNext);
		}

		return true;
	}

	private void addGuitarNotes(final DrawingData drawingData, final ArrangementChart arrangement,
			final int panelWidth) {
		final HashSet2<Integer> selectedNoteIds = selectionManager.getSelectedAccessor(PositionType.GUITAR_NOTE)
				.getSelectedSet()//
				.map(selection -> selection.id);

		final ArrayList2<ChordOrNote> chordsAndNotes = PositionType.GUITAR_NOTE.getPositions(data);

		boolean lastWasLinkNext = false;
		for (int i = 0; i < chordsAndNotes.size(); i++) {
			final ChordOrNote chordOrNote = chordsAndNotes.get(i);
			final boolean selected = selectedNoteIds.contains(i);
			addChordOrNote(drawingData, arrangement, panelWidth, chordOrNote, selected, lastWasLinkNext);

			lastWasLinkNext = chordOrNote.chord != null ? chordOrNote.chord.linkNext : chordOrNote.note.linkNext;
		}
	}

	private void addHandShapes(final DrawingData drawingData, final Level level, final int panelWidth) {
		final HashSet2<Integer> selectedHandShapeIds = selectionManager.getSelectedAccessor(PositionType.HAND_SHAPE)//
				.getSelectedSet().map(selection -> selection.id);

		for (int i = 0; i < level.handShapes.size(); i++) {
			final HandShape handShape = level.handShapes.get(i);
			final int x = timeToX(handShape.position, data.time);
			if (isPastRightEdge(x, panelWidth)) {
				break;
			}

			final int length = timeToXLength(handShape.length);
			if (!isOnScreen(x, length)) {
				continue;
			}

			final boolean selected = selectedHandShapeIds.contains(i);
			drawingData.addHandShape(x, length, selected);
		}
	}

	private void drawGuitarNotes(final Graphics g) {
		final Level level = data.getCurrentArrangementLevel();
		final ArrangementChart arrangement = data.getCurrentArrangement();
		final int strings = data.getCurrentArrangement().tuning.strings;
		final DrawingData drawingData = new DrawingData(strings);

		final int panelWidth = chartPanel.getWidth();

		addGuitarNotes(drawingData, arrangement, panelWidth);
		addHandShapes(drawingData, level, panelWidth);
		addAnchors(drawingData, level, panelWidth);

		drawingData.draw(g);
	}

	public void draw(final Graphics g) {
		beatsDrawer.draw(g);
		drawGuitarLanes(g);
		audioDrawer.draw(g);
		drawGuitarNotes(g);
	}
}
