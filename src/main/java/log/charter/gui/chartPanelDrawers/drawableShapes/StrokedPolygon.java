package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.util.data.Position2D;

class StrokedPolygon implements DrawableShape {
	private final Position2D[] points;
	private final Color color;

	public StrokedPolygon(final Position2D[] points, final Color color) {
		this.points = points;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(color);
		final double[] xs = new double[points.length];
		final double[] ys = new double[points.length];
		for (int i = 0; i < points.length; i++) {
			xs[i] = (double) points[i].x;
			ys[i] = (double) points[i].y;
		}

		gc.strokePolygon(xs, ys, points.length);
	}
}
