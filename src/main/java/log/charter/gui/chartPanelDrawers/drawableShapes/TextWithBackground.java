package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.util.data.Position2D;

public class TextWithBackground implements DrawableShape {
	public static ShapeSize getExpectedSize(final GraphicsContext gc, final Font font, final String text, final int space) {
		return Text.getExpectedSize(gc, font, text).resizeBy(4 * space, 4 * space + 1);
	}

	private final Text text;
	private final Color backgroundColor;
	private final int space;

	public TextWithBackground(final Position2D position, final Font font, final String text, final ColorLabel textColor,
			final ColorLabel backgroundColor, final ColorLabel borderColor) {
		this(position, font, text, textColor.color(), backgroundColor.color(), 1, borderColor.color());
	}

	public TextWithBackground(final Position2D position, final Font font, final String text, final ColorLabel textColor,
			final ColorLabel backgroundColor, final Color borderColor) {
		this(position, font, text, textColor.color(), backgroundColor.color(), 1, borderColor);
	}

	public TextWithBackground(final Position2D position, final Font font, final String text, final ColorLabel textColor,
			final ColorLabel backgroundColor, final int space, final ColorLabel borderColor) {
		this(position, font, text, textColor.color(), backgroundColor.color(), space, borderColor.color());
	}

	public TextWithBackground(final Position2D position, final Font font, final String text, final ColorLabel textColor,
			final ColorLabel backgroundColor, final int space, final Color borderColor) {
		this(position, font, text, textColor.color(), backgroundColor.color(), space, borderColor);
	}

	public TextWithBackground(final Position2D position, final Font font, final String text, final Color textColor,
			final Color backgroundColor, final Color borderColor) {
		this(position, font, text, textColor, backgroundColor, 1, borderColor);
	}

	public TextWithBackground(final Position2D position, final Font font, final String text, final Color textColor,
			final Color backgroundColor, final int space, final Color borderColor) {
		this.text = new Text(position.move(space, space), font, text, textColor);
		this.backgroundColor = backgroundColor;
		this.space = space;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		draw(gc, getPositionWithSize(gc));
	}

	public ShapePositionWithSize getPositionWithSize(final GraphicsContext gc) {
		return text.getPositionWithSize(gc).resized(-2 * space + 2, -2 * space, 4 * space, 4 * space + 1);
	}

	public void draw(final GraphicsContext gc, final ShapePositionWithSize positionAndSize) {
		gc.setFill(backgroundColor);
		gc.fillRoundRect(positionAndSize.x, positionAndSize.y,
				positionAndSize.width, positionAndSize.height, 5, 5);

		text.draw(gc, positionAndSize.resized(2 * space, 2 * space, -4 * space, -4 * space - 1));
	}
}
