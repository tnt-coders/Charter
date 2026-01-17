package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.util.data.Position2D;

class FilledTriangle implements DrawableShape {
	private final Position2D a;
	private final Position2D b;
	private final Position2D c;
	private final Color color;

	public FilledTriangle(final Position2D a, final Position2D b, final Position2D c, final Color color) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(color);
		gc.fillPolygon(new double[] { (double) a.x, (double) b.x, (double) c.x },
				new double[] { (double) a.y, (double) b.y, (double) c.y }, 3);
	}
}
