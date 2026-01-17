package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import log.charter.gui.components.preview3D.glUtils.Point2D;
import log.charter.util.data.Position2D;

public class CenteredTextWithBackground implements DrawableShape {
	public static ShapePositionWithSize getExpectedPositionAndSize(final GraphicsContext gc, final Position2D position,
			final Font font, final String text) {
		final ShapePositionWithSizeDouble expectedSize = CenteredText.getExpectedPositionAndSize(gc, position, font,
				text);

		return expectedSize.asInteger().resized(-1, -1, 2, 4);
	}

	public static ShapeSize getExpectedSize(final GraphicsContext gc, final Font font, final String text) {
		final Point2D textSize = CenteredText.getExpectedSize(gc, font, text);

		return new ShapeSize((int) textSize.x + 2, (int) textSize.y + 4);
	}

	final CenteredText centeredText;
	final Color backgroundColor;

	public CenteredTextWithBackground(final Position2D position, final Font font, final String text,
			final Color textColor, final Color backgroundColor) {
		centeredText = new CenteredText(position, font, text, textColor);
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		draw(gc, getPositionAndSize(gc));
	}

	public ShapePositionWithSize getPositionAndSize(final GraphicsContext gc) {
		return centeredText.getPositionWithSize(gc).asInteger().resized(-1, -1, 2, 4);
	}

	public void draw(final GraphicsContext gc, final ShapePositionWithSize positionAndSize) {
		if (backgroundColor != null) {
			gc.setFill(backgroundColor);
			gc.fillRect(positionAndSize.x, positionAndSize.y, positionAndSize.width, positionAndSize.height);
		}

		centeredText.draw(gc, positionAndSize.resized(1, 1, -2, -4).asDouble());
	}

}
