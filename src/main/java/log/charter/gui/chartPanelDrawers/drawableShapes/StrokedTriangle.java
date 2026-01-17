package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.data.config.ChartPanelColors.ColorLabel;
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
	public void draw(final GraphicsContext gc) {
		gc.setStroke(color);
		gc.setLineWidth(thickness);
		gc.strokePolygon(new double[] { (double) a.x, (double) b.x, (double) c.x },
				new double[] { (double) a.y, (double) b.y, (double) c.y }, 3);
	}
}
