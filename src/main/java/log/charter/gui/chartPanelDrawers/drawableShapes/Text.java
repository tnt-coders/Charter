package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import java.awt.Font;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.util.data.Position2D;

public class Text implements DrawableShape {
	public static ShapeSize getExpectedSize(final GraphicsWrapper g, final Font font, final String text) {
		final int width = g.getStringWidth(text, font) + (font.isItalic() ? 1 : 0);
		final int height = g.getAscent(font) - g.getDescent(font) - (font.isItalic() ? 1 : 0);

		return new ShapeSize(width, height);
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
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		draw(g, getPositionWithSize(g));
	}

	public ShapePositionWithSize getPositionWithSize(final GraphicsWrapper g) {
		final ShapeSize size = getExpectedSize(g, font, text);
		return new ShapePositionWithSize(position.x, position.y, size.width, size.height);
	}

	public void draw(final GraphicsWrapper g, final ShapePositionWithSize positionAndSize) {
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, positionAndSize.x, positionAndSize.y + positionAndSize.height);
	}
}
