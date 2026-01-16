package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import java.awt.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.util.data.Position2D;

public class CenteredTextWithBackgroundAndBorder implements DrawableShape {
	public static ShapePositionWithSize getExpectedPositionAndSize(final GraphicsWrapper g, final Position2D position,
			final Font font, final String text) {
		return CenteredTextWithBackground.getExpectedPositionAndSize(g, position, font, text).resized(-1, -1, 2, 2);
	}

	public static ShapeSize getExpectedSize(final GraphicsWrapper g, final Font font, final String text) {
		final ShapeSize innerSize = CenteredTextWithBackground.getExpectedSize(g, font, text);
		return new ShapeSize(innerSize.width + 2, innerSize.height + 2);
	}

	private final CenteredTextWithBackground centeredTextWithBackground;
	private final Color borderColor;

	public CenteredTextWithBackgroundAndBorder(final Position2D position, final Font font, final String text,
			final ColorLabel textColor, final ColorLabel backgroundColor, final ColorLabel borderColor) {
		this(position, font, text, textColor.color(), backgroundColor.color(), borderColor.color());
	}

	public CenteredTextWithBackgroundAndBorder(final Position2D position, final Font font, final String text,
			final Color textColor, final Color backgroundColor, final Color borderColor) {
		centeredTextWithBackground = new CenteredTextWithBackground(position, font, text, textColor, backgroundColor);
		this.borderColor = borderColor;
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		draw(g, getPositionAndSize(g));
	}

	public ShapePositionWithSize getPositionAndSize(final GraphicsWrapper g) {
		return centeredTextWithBackground.getPositionAndSize(g).resized(-1, -1, 2, 2);
	}

	public void draw(final GraphicsWrapper g, final ShapePositionWithSize positionAndSize) {
		if (borderColor != null) {
			g.setColor(borderColor);
			g.setStroke(1);
			g.drawRect(positionAndSize.x, positionAndSize.y, positionAndSize.width - 1, positionAndSize.height - 1);
		}

		centeredTextWithBackground.draw(g, positionAndSize.resized(1, 1, -2, -2));
	}

}
