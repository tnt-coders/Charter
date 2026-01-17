package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class FilledOval implements DrawableShape {
	private final ShapePositionWithSize position;
	private final Color color;

	public FilledOval(final ShapePositionWithSize position, final Color color) {
		this.position = position;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(color);
		gc.fillOval(position.x, position.y, position.width, position.height);
	}
}
