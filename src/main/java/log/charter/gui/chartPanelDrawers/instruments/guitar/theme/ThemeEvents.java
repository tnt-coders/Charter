package log.charter.gui.chartPanelDrawers.instruments.guitar.theme;

import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

import log.charter.data.song.EventPoint;
import log.charter.data.song.Phrase;
import log.charter.data.song.SectionType;

public interface ThemeEvents {

	void addCurrentSection(GraphicsWrapper g, SectionType section);

	void addCurrentSection(GraphicsWrapper g, SectionType section, int nextSectionX);

	void addCurrentPhrase(GraphicsWrapper g, Phrase phrase, String phraseName);

	void addCurrentPhrase(GraphicsWrapper g, Phrase phrase, String phraseName, int nextSectionX);

	void addEventPoint(GraphicsWrapper g, final EventPoint eventPoint, final Phrase phrase, final int x,
			final boolean selected, final boolean highlighted);

	void addEventPointHighlight(final int x);

	void addEvents(GraphicsWrapper g, EventPoint eventPoint, int x);
}
