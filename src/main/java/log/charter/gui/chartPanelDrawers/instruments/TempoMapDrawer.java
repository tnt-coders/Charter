package log.charter.gui.chartPanelDrawers.instruments;

import static log.charter.data.config.GraphicalConfig.tempoMapGhostNotesTransparency;

import java.awt.image.BufferedImage;

import log.charter.gui.chartPanelDrawers.common.BeatsDrawer;
import log.charter.gui.chartPanelDrawers.common.LyricLinesDrawer;
import log.charter.gui.chartPanelDrawers.common.SwingGraphicsWrapper;
import log.charter.gui.chartPanelDrawers.common.waveform.WaveFormDrawer;
import log.charter.gui.chartPanelDrawers.data.FrameData;
import log.charter.gui.chartPanelDrawers.instruments.guitar.GuitarDrawer;

public class TempoMapDrawer {
	private BeatsDrawer beatsDrawer;
	private GuitarDrawer guitarDrawer;
	private LyricLinesDrawer lyricLinesDrawer;
	private WaveFormDrawer waveFormDrawer;

	public void guitarDrawer(final GuitarDrawer guitarDrawer) {
		this.guitarDrawer = guitarDrawer;
	}

	public void lyricLinesDrawer(final LyricLinesDrawer lyricLinesDrawer) {
		this.lyricLinesDrawer = lyricLinesDrawer;
	}

	private void drawGhostNotes(final FrameData frameData) {
		if (tempoMapGhostNotesTransparency <= 0) {
			return;
		}

		final BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
		final FrameData subFrameData = frameData.spawnSubData(new SwingGraphicsWrapper(image.createGraphics()));

		lyricLinesDrawer.draw(subFrameData);
		guitarDrawer.drawGuitar(subFrameData);
		guitarDrawer.drawStringNames(subFrameData);

		frameData.g.setTransparency(tempoMapGhostNotesTransparency);
		frameData.g.drawImage(image, 0, 0);
		frameData.g.setTransparency(1.0f);
	}

	public void draw(final FrameData frameData) {
		waveFormDrawer.draw(frameData);
		beatsDrawer.draw(frameData);
		drawGhostNotes(frameData);
	}
}
