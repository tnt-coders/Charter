package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;

class FilledOval implements DrawableShape {
	private final ShapePositionWithSize position;
	private final Color color;

	public FilledOval(final ShapePositionWithSize position, final Color color) {
		this.position = position;
		this.color = color;
	}

	@Override
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);
		g.fillOval(position.x, position.y, position.width, position.height);
	}
}
