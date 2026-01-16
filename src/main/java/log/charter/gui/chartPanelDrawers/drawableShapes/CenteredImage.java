package log.charter.gui.chartPanelDrawers.drawableShapes;

import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import java.awt.image.BufferedImage;

import log.charter.util.data.Position2D;

public class CenteredImage implements DrawableShape {
	private final Position2D position;
	private final BufferedImage image;

	public CenteredImage(final Position2D position, final BufferedImage image) {
		this.position = position;
		this.image = image;
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);

		final int x = position.x - image.getWidth() / 2;
		final int y = position.y - image.getHeight() / 2;
		g.drawImage(image, x, y);
	}

}
