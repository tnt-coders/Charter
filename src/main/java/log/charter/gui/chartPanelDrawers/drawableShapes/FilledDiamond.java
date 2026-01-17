package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.util.data.Position2D;

class FilledDiamond implements DrawableShape {
	private final Position2D position;
	private final int radius;
	private final Color color;

	public FilledDiamond(final Position2D position, final int radius, final Color color) {
		this.position = position.move(1, 0);
		this.radius = radius;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(color);

		gc.fillPolygon(new double[] { (double) position.x - radius, (double) position.x, (double) position.x + radius, (double) position.x },
				new double[] { (double) position.y, (double) position.y - radius, (double) position.y, (double) position.y + radius }, 4);
	}
}