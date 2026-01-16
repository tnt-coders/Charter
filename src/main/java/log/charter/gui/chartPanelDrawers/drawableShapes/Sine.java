package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

import log.charter.util.data.Position2D;

class Sine implements DrawableShape {
	private final Color color;
	private final int thickness;
	private final int[] xPoints;
	private final int[] yPoints;
	private final int nPoints;

	public Sine(final Position2D from, final int length, final int amplitude, final int phase, final int period, final Color color) {
		this(from, length, amplitude, phase, period, color, 1);
	}

	public Sine(final Position2D from, final int length, final int amplitude, final int phase, final int period, final Color color, final int thickness) {
		this.color = color;
		this.thickness = thickness;
		this.nPoints = length;
		this.xPoints = new int[length];
		this.yPoints = new int[length];

		final double A = amplitude;
		final double B = 2 * Math.PI / period;
		for (int x = 0; x < length; x++) {
			final int y = (int) (A * Math.sin(B * (x + phase)));
			xPoints[x] = x + from.x;
			yPoints[x] = y + from.y;
		}
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);
		g.setStroke(thickness);
		g.drawPolyline(xPoints, yPoints, nPoints);
	}
}
