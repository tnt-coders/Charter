package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class StrokedRectangle implements DrawableShape {
	private final ShapePositionWithSize position;
	private final Color color;

	private final int thickness;

	public StrokedRectangle(final int x, final int y, final int width, final int height, final Color color) {
		position = new ShapePositionWithSize(x, y, width, height);
		this.color = color;
		thickness = 1;
	}

	public StrokedRectangle(final ShapePositionWithSize position, final Color color) {
		this.position = position;
		this.color = color;
		thickness = 1;
	}

	public StrokedRectangle(final ShapePositionWithSize position, final Color color, final int thickness) {
		this.position = position;
		this.color = color;
		this.thickness = thickness;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(color);
		gc.setLineWidth(thickness);
		gc.strokeRect(position.x, position.y, position.width, position.height);
	}

	public StrokedRectangle centered() {
		return new StrokedRectangle(position.centered(), color);
	}
}