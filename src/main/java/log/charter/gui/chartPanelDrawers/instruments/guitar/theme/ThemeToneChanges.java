package log.charter.gui.chartPanelDrawers.instruments.guitar.theme;

import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

import log.charter.data.song.ToneChange;

public interface ThemeToneChanges {
	void addCurrentTone(GraphicsWrapper g, String tone, int nextToneChangeX);

	void addCurrentTone(GraphicsWrapper g, String tone);

	void addToneChange(final ToneChange toneChange, final int x, final boolean selected, final boolean highlighted);

	void addToneChangeHighlight(final int x);
}
