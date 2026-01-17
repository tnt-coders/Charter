package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import java.util.List;

class ClippedShapes implements DrawableShape {
	private final ShapePositionWithSize position;
	private final List<DrawableShape> shapes;

	public ClippedShapes(final ShapePositionWithSize position, final List<DrawableShape> shapes) {
		this.position = position;
		this.shapes = shapes;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.save();
		gc.beginPath();
		gc.rect(position.x, position.y, position.width, position.height);
		gc.clip();
		for (final DrawableShape shape : shapes) {
			shape.draw(gc);
		}
		gc.restore();
	}
}
