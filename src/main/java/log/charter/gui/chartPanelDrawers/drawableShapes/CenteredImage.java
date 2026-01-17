package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import log.charter.util.data.Position2D;

public class CenteredImage implements DrawableShape {
	private final Position2D position;
	private final Image image;

	public CenteredImage(final Position2D position, final Image image) {
		this.position = position;
		this.image = image;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		final double x = position.x - image.getWidth() / 2;
		final double y = position.y - image.getHeight() / 2;
		gc.drawImage(image, x, y);
	}
}
