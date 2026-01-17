package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.util.data.Position2D;

public class CenteredTextWithBackgroundAndBorder implements DrawableShape {
	public static ShapePositionWithSize getExpectedPositionAndSize(final GraphicsContext gc, final Position2D position,
			final Font font, final String text) {
		return CenteredTextWithBackground.getExpectedPositionAndSize(gc, position, font, text).resized(-1, -1, 2, 2);
	}

	public static ShapeSize getExpectedSize(final GraphicsContext gc, final Font font, final String text) {
		final ShapeSize innerSize = CenteredTextWithBackground.getExpectedSize(gc, font, text);
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
	public void draw(final GraphicsContext gc) {
		draw(gc, getPositionAndSize(gc));
	}

	public ShapePositionWithSize getPositionAndSize(final GraphicsContext gc) {
		return centeredTextWithBackground.getPositionAndSize(gc).resized(-1, -1, 2, 2);
	}

	public void draw(final GraphicsContext gc, final ShapePositionWithSize positionAndSize) {
		if (borderColor != null) {
			gc.setStroke(borderColor);
			gc.setLineWidth(1);
			gc.strokeRect(positionAndSize.x, positionAndSize.y, positionAndSize.width - 1, positionAndSize.height - 1);
		}

		centeredTextWithBackground.draw(gc, positionAndSize.resized(1, 1, -2, -2));
	}

}
