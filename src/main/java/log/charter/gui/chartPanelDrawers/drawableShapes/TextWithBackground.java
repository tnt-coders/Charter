package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import java.awt.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.util.data.Position2D;

public class TextWithBackground implements DrawableShape {
	public static ShapeSize getExpectedSize(final GraphicsWrapper g, final Font font, final String text, final int space) {
		return Text.getExpectedSize(g, font, text).resizeBy(4 * space, 4 * space + 1);
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
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		draw(g, getPositionWithSize(g));
	}

	public ShapePositionWithSize getPositionWithSize(final GraphicsWrapper g) {
		return text.getPositionWithSize(g).resized(-2 * space + 2, -2 * space, 4 * space, 4 * space + 1);
	}

	public void draw(final GraphicsWrapper g, final ShapePositionWithSize positionAndSize) {
		g.setColor(backgroundColor);
		g.fillRoundRect(positionAndSize.x, positionAndSize.y, positionAndSize.width, positionAndSize.height, 5, 5);

		text.draw(g, positionAndSize.resized(2 * space, 2 * space, -4 * space, -4 * space - 1));
	}
}
