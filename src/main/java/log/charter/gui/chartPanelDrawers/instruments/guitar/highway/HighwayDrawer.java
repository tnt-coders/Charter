package log.charter.gui.chartPanelDrawers.instruments.guitar.highway;

import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.gui.chartPanelDrawers.instruments.guitar.theme.modern.ModernHighwayDrawer;
import java.util.Optional;

import log.charter.data.config.GraphicalConfig;
import log.charter.data.song.ChordTemplate;
import log.charter.data.song.EventPoint;
import log.charter.data.song.FHP;
import log.charter.data.song.HandShape;
import log.charter.data.song.Phrase;
import log.charter.data.song.SectionType;
import log.charter.data.song.ToneChange;
import log.charter.data.song.notes.ChordOrNote;
import log.charter.gui.chartPanelDrawers.data.EditorNoteDrawingData;
import log.charter.gui.chartPanelDrawers.data.HighlightData.HighlightLine;

public interface HighwayDrawer {
	public static void reloadGraphics() {
		ModernHighwayDrawer.reloadGraphics();
	}

	public static HighwayDrawer getHighwayDrawer(final GraphicsWrapper g, final int strings, final double time) {
		return switch (GraphicalConfig.theme) {
			case BASIC -> new DefaultHighwayDrawer(g, strings, time);
			case SQUARE -> new SquareHighwayDrawer(g, strings, time);
			case MODERN -> new ModernHighwayDrawer(g, strings, time);
		};
	}

	void addCurrentSection(GraphicsWrapper g, SectionType section);

	void addCurrentSection(GraphicsWrapper g, SectionType section, int nextSectionX);

	void addCurrentPhrase(GraphicsWrapper g, Phrase phrase, String phraseName, int nextEventPointX);

	void addCurrentPhrase(GraphicsWrapper g, Phrase phrase, String phraseName);

	void addEvents(GraphicsWrapper g, EventPoint eventPoint, int x);

	void addEventPoint(GraphicsWrapper g, EventPoint eventPoint, Phrase phrase, int x, boolean selected,
			boolean highlighted);

	void addEventPointHighlight(int x);

	void addCurrentTone(GraphicsWrapper g, String tone);

	void addCurrentTone(GraphicsWrapper g, String tone, int nextToneChangeX);

	void addToneChange(ToneChange toneChange, int x, boolean selected, boolean highlighted);

	void addToneChangeHighlight(int x);

	void addCurrentFHP(GraphicsWrapper g, FHP fhp);

	void addCurrentFHP(GraphicsWrapper g, FHP fhp, int nextFHPX);

	void addFHP(FHP fhp, int x, boolean selected, boolean highlighted);

	void addFHPHighlight(int x);

	void addChordName(int x, String chordName);

	void addNote(final EditorNoteDrawingData note);

	void addSoundHighlight(int x, int length, Optional<ChordOrNote> originalSound, Optional<ChordTemplate> template,
			int string, boolean drawOriginalStrings);

	void addNoteAdditionLine(HighlightLine line);

	void addHandShape(int x, int length, boolean selected, boolean highlighted, HandShape handShape,
			ChordTemplate chordTemplate);

	void addHandShapeHighlight(int x, int length);

	void draw(GraphicsWrapper g);

}
