package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.util.data.Position2D;

class Sine implements DrawableShape {
	private final Color color;
	private final int thickness;
	private final double[] xs;
	private final double[] ys;
	private final int points;

	public Sine(final Position2D from, final int length, final int amplitude, final int phase, final int period, final Color color) {
		this(from, length, amplitude, phase, period, color, 1);
	}

	public Sine(final Position2D from, final int length, final int amplitude, final int phase, final int period, final Color color, final int thickness) {
		this.color = color;
		this.thickness = thickness;
		this.points = length;
		this.xs = new double[length];
		this.ys = new double[length];

		final double A = amplitude;
		final double B = 2 * Math.PI / period;
		for (int x = 0; x < length; x++) {
			final double y = A * Math.sin(B * (x + phase));
			xs[x] = (double) x + from.x;
			ys[x] = y + from.y;
		}
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(color);
		gc.setLineWidth(thickness);
		gc.strokePolyline(xs, ys, points);
	}
}
