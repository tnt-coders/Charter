package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextBoundsType;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.components.preview3D.glUtils.Point2D;
import log.charter.util.data.Position2D;

public class CenteredText implements DrawableShape {
	public static ShapePositionWithSizeDouble getExpectedPositionAndSize(final GraphicsContext gc, final Position2D position,
			final Font font, final String text) {
		final Point2D expectedSize = getExpectedSize(gc, font, text);

		return new ShapePositionWithSizeDouble(position.x - expectedSize.x / 2, position.y - expectedSize.y / 2,
				expectedSize.x, expectedSize.y);
	}

	public static Point2D getExpectedSize(final GraphicsContext gc, final Font font, final String text) {
		final javafx.scene.text.Text textNode = new javafx.scene.text.Text(text);
		textNode.setFont(font);
		textNode.setBoundsType(TextBoundsType.VISUAL);
		final double width = textNode.getLayoutBounds().getWidth();
		final double height = textNode.getLayoutBounds().getHeight();

		return new Point2D(width, height);
	}

	final Position2D position;
	final Font font;
	final String text;
	final Color textColor;

	public CenteredText(final Position2D position, final Font font, final String text, final ColorLabel textColor) {
		this(position, font, text, textColor.color());
	}

	public CenteredText(final Position2D position, final Font font, final String text, final Color textColor) {
		this.position = position;
		this.font = font;
		this.text = text;
		this.textColor = textColor;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		draw(gc, getPositionWithSize(gc));
	}

	public ShapePositionWithSizeDouble getPositionWithSize(final GraphicsContext gc) {
		return getExpectedPositionAndSize(gc, position, font, text);
	}

	public void draw(final GraphicsContext gc, final ShapePositionWithSizeDouble positionAndSize) {
		gc.setFont(font);
		gc.setFill(textColor);
		gc.fillText(text, (float) (positionAndSize.x), (float) (positionAndSize.y + positionAndSize.height));
	}
}
