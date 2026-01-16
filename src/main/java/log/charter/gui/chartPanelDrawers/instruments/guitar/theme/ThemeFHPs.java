package log.charter.gui.chartPanelDrawers.instruments.guitar.theme;

import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

import log.charter.data.song.FHP;

public interface ThemeFHPs {
	void addCurrentFHP(GraphicsWrapper g, FHP fhp);

	void addCurrentFHP(GraphicsWrapper g, FHP fhp, int nextFHPX);

	void addFHP(final FHP fhp, final int x, final boolean selected, final boolean highlighted);

	void addFHPHighlight(final int x);
}
