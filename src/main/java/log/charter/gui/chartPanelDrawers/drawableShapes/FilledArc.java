package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

class FilledArc implements DrawableShape {
	private final ShapePositionWithSize position;
	private final int startAngle;
	private final int arcAngle;
	private final Color color;

	public FilledArc(final ShapePositionWithSize position, final int startAngle, final int arcAngle,
			final Color color) {
		this.position = position;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setFill(color);
		gc.fillArc(position.x, position.y, position.width, position.height, startAngle, arcAngle, ArcType.ROUND);
	}
}
