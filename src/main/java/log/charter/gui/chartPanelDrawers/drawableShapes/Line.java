package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import log.charter.data.config.ChartPanelColors.ColorLabel;
import log.charter.util.data.Position2D;

public class Line implements DrawableShape {
	private final Position2D from;
	private final Position2D to;
	private final Color color;
	private final int thickness;

	public Line(final Position2D from, final Position2D to, final ColorLabel color) {
		this(from, to, color.color(), 1);
	}

	public Line(final Position2D from, final Position2D to, final Color color) {
		this(from, to, color, 1);
	}

	public Line(final Position2D from, final Position2D to, final ColorLabel color, final int thickness) {
		this(from, to, color.color(), thickness);
	}

	public Line(final Position2D from, final Position2D to, final Color color, final int thickness) {
		this.from = from;
		this.to = to;
		this.color = color;
		this.thickness = thickness;
	}

	@Override
	public void draw(final GraphicsContext gc) {
		gc.setStroke(color);
		gc.setLineWidth(thickness);
		gc.strokeLine(from.x, from.y, to.x, to.y);
	}
}
