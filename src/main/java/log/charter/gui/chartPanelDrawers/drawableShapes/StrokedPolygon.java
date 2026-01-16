package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
import log.charter.util.data.Position2D;

class StrokedPolygon implements DrawableShape {
	private final Position2D[] points;
	private final Color color;

	public StrokedPolygon(final Position2D[] points, final Color color) {
		this.points = points;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);
		final int[] xs = new int[points.length + 1];
		final int[] ys = new int[points.length + 1];
		for (int i = 0; i < points.length; i++) {
			xs[i] = points[i].x;
			ys[i] = points[i].y;
		}
		xs[points.length] = points[0].x;
		ys[points.length] = points[0].y;

		g.drawPolyline(xs, ys, points.length + 1);
	}
}
