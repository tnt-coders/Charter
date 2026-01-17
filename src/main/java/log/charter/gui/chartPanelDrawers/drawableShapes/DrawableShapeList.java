package log.charter.gui.chartPanelDrawers.drawableShapes;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class DrawableShapeList {
	private final List<DrawableShape> list = new ArrayList<>();

	public void add(final DrawableShape shape) {
		list.add(shape);
	}

	public void draw(final GraphicsContext gc) {
		list.forEach(shape -> shape.draw(gc));
	}
}
