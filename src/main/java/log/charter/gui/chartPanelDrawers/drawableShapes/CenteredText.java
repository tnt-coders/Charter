package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import java.awt.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.gui.components.preview3D.glUtils.Point2D;
import log.charter.util.data.Position2D;

public class CenteredText implements DrawableShape {
	public static ShapePositionWithSizeDouble getExpectedPositionAndSize(final GraphicsWrapper g, final Position2D position,
			final Font font, final String text) {
		final Point2D expectedSize = getExpectedSize(g, font, text);

		return new ShapePositionWithSizeDouble(position.x - expectedSize.x / 2, position.y - expectedSize.y / 2,
				expectedSize.x, expectedSize.y);
	}

	public static Point2D getExpectedSize(final GraphicsWrapper g, final Font font, final String text) {
		final int width = g.getStringWidth(text, font);
		final int height = g.getAscent(font) - g.getDescent(font);

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
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);

		draw(g, getPositionWithSize(g));
	}

	public ShapePositionWithSizeDouble getPositionWithSize(final GraphicsWrapper g) {
		return getExpectedPositionAndSize(g, position, font, text);
	}

	public void draw(final GraphicsWrapper g, final ShapePositionWithSizeDouble positionAndSize) {
		g.setFont(font);
		g.setColor(textColor);
		g.drawString(text, (int) (positionAndSize.x), (int) (positionAndSize.y + positionAndSize.height));
	}
}
