package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

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
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);
		g.fillArc(position.x, position.y, position.width, position.height, startAngle, arcAngle);
	}
}
