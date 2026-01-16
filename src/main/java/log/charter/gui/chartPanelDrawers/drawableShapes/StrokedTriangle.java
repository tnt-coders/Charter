package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.util.data.Position2D;

public class StrokedTriangle implements DrawableShape {
	private final Position2D a;
	private final Position2D b;
	private final Position2D c;
	private final Color color;
	private final int thickness;

	public StrokedTriangle(final Position2D a, final Position2D b, final Position2D c, final ColorLabel color) {
		this(a, b, c, color.color(), 1);
	}

	public StrokedTriangle(final Position2D a, final Position2D b, final Position2D c, final Color color) {
		this(a, b, c, color, 1);
	}

	public StrokedTriangle(final Position2D a, final Position2D b, final Position2D c, final Color color,
			final int thickness) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.color = color;
		this.thickness = thickness;
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);
		g.setStroke(thickness);
		g.drawPolyline(new int[] { a.x, b.x, c.x, a.x }, new int[] { a.y, b.y, c.y, a.y }, 4);
	}
}
