package log.charter.gui.chartPanelDrawers.drawableShapes;

import java.awt.Color;
import log.charter.gui.chartPanelDrawers.common.GraphicsWrapper;
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
	public void draw(final GraphicsWrapper g) {
		g.setAntialiasing(true);
		g.setColor(color);

		final int[] xPoints = new int[] { position.x - radius, position.x, position.x + radius, position.x };
		final int[] yPoints = new int[] { position.y, position.y - radius, position.y, position.y + radius };
		g.fillPolygon(xPoints, yPoints, 4);
	}
}