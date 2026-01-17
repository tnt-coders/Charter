package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.util.data.Position2D;

public class Text implements DrawableShape {
	public static ShapeSize getExpectedSize(final GraphicsContext gc, final Font font, final String text) {
		final javafx.scene.text.Text textNode = new javafx.scene.text.Text(text);
		textNode.setFont(font);
		textNode.setBoundsType(TextBoundsType.VISUAL);
		final double width = textNode.getLayoutBounds().getWidth();
		final double height = textNode.getLayoutBounds().getHeight();

		return new ShapeSize((int) width, (int) height);
	}

	private final Position2D position;
	private final Font font;
	private final String text;
	private final Color color;

	public Text(final Position2D position, final Font font, final String text, final ColorLabel color) {
		this(position, font, text, color.color());
	}

	public Text(final Position2D position, final Font font, final String text, final Color color) {
		this.position = position;
		this.font = font;
		this.text = text;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		draw(gc, getPositionWithSize(gc));
	}

	public ShapePositionWithSize getPositionWithSize(final GraphicsContext gc) {
		final ShapeSize size = getExpectedSize(gc, font, text);
		return new ShapePositionWithSize(position.x, position.y, size.width, size.height);
	}

	public void draw(final GraphicsContext gc, final ShapePositionWithSize positionAndSize) {
		gc.setFont(font);
		gc.setFill(color);
		gc.fillText(text, positionAndSize.x, positionAndSize.y + positionAndSize.height);
	}
}
