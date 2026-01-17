package log.charter.gui.chartPanelDrawers.common;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.transform.Affine;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Wrapper class that provides a Graphics2D-like interface for JavaFX GraphicsContext.
 * This allows existing drawing code to work with minimal changes.
 */
public class Graphics2DWrapper {
	private final GraphicsContext gc;
	private final int width;
	private final int height;

	// Current drawing state
	private Color currentColor = Color.BLACK;
	private Font currentFont = Font.font(12);
	private double strokeWidth = 1.0;

	public Graphics2DWrapper(GraphicsContext gc, int width, int height) {
		this.gc = gc;
		this.width = width;
		this.height = height;
	}

	public GraphicsContext getGraphicsContext() {
		return gc;
	}

	// Color operations
	public void setColor(java.awt.Color awtColor) {
		currentColor = convertColor(awtColor);
		gc.setFill(currentColor);
		gc.setStroke(currentColor);
	}

	public java.awt.Color getColor() {
		return convertColor(currentColor);
	}

	private Color convertColor(java.awt.Color awtColor) {
		return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(),
			awtColor.getAlpha() / 255.0);
	}

	private java.awt.Color convertColor(Color fxColor) {
		return new java.awt.Color(
			(int) (fxColor.getRed() * 255),
			(int) (fxColor.getGreen() * 255),
			(int) (fxColor.getBlue() * 255),
			(int) (fxColor.getOpacity() * 255)
		);
	}

	// Font operations
	public void setFont(java.awt.Font awtFont) {
		currentFont = convertFont(awtFont);
		gc.setFont(currentFont);
	}

	public java.awt.Font getFont() {
		return convertFont(currentFont);
	}

	private Font convertFont(java.awt.Font awtFont) {
		FontWeight weight = awtFont.isBold() ? FontWeight.BOLD : FontWeight.NORMAL;
		FontPosture posture = awtFont.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
		return Font.font(awtFont.getFamily(), weight, posture, awtFont.getSize());
	}

	private java.awt.Font convertFont(Font fxFont) {
		int style = java.awt.Font.PLAIN;
		String name = fxFont.getName().toLowerCase();
		if (name.contains("bold")) style |= java.awt.Font.BOLD;
		if (name.contains("italic")) style |= java.awt.Font.ITALIC;
		return new java.awt.Font(fxFont.getFamily(), style, (int) fxFont.getSize());
	}

	// Stroke operations
	public void setStroke(BasicStroke stroke) {
		strokeWidth = stroke.getLineWidth();
		gc.setLineWidth(strokeWidth);

		// Convert line cap
		switch (stroke.getEndCap()) {
			case BasicStroke.CAP_BUTT:
				gc.setLineCap(StrokeLineCap.BUTT);
				break;
			case BasicStroke.CAP_ROUND:
				gc.setLineCap(StrokeLineCap.ROUND);
				break;
			case BasicStroke.CAP_SQUARE:
				gc.setLineCap(StrokeLineCap.SQUARE);
				break;
		}

		// Convert line join
		switch (stroke.getLineJoin()) {
			case BasicStroke.JOIN_BEVEL:
				gc.setLineJoin(StrokeLineJoin.BEVEL);
				break;
			case BasicStroke.JOIN_MITER:
				gc.setLineJoin(StrokeLineJoin.MITER);
				break;
			case BasicStroke.JOIN_ROUND:
				gc.setLineJoin(StrokeLineJoin.ROUND);
				break;
		}
	}

	// Drawing operations
	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.strokeLine(x1, y1, x2, y2);
	}

	public void drawRect(int x, int y, int width, int height) {
		gc.strokeRect(x, y, width, height);
	}

	public void fillRect(int x, int y, int width, int height) {
		gc.fillRect(x, y, width, height);
	}

	public void drawOval(int x, int y, int width, int height) {
		gc.strokeOval(x, y, width, height);
	}

	public void fillOval(int x, int y, int width, int height) {
		gc.fillOval(x, y, width, height);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		gc.strokeArc(x, y, width, height, startAngle, arcAngle, ArcType.OPEN);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		gc.fillArc(x, y, width, height, startAngle, arcAngle, ArcType.ROUND);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		double[] x = new double[nPoints];
		double[] y = new double[nPoints];
		for (int i = 0; i < nPoints; i++) {
			x[i] = xPoints[i];
			y[i] = yPoints[i];
		}
		gc.strokePolygon(x, y, nPoints);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		double[] x = new double[nPoints];
		double[] y = new double[nPoints];
		for (int i = 0; i < nPoints; i++) {
			x[i] = xPoints[i];
			y[i] = yPoints[i];
		}
		gc.fillPolygon(x, y, nPoints);
	}

	public void drawString(String str, int x, int y) {
		gc.fillText(str, x, y);
	}

	public void drawString(String str, float x, float y) {
		gc.fillText(str, x, y);
	}

	// Shape operations
	public void draw(Shape shape) {
		drawShape(shape, false);
	}

	public void fill(Shape shape) {
		drawShape(shape, true);
	}

	private void drawShape(Shape shape, boolean fill) {
		if (shape instanceof Line2D) {
			Line2D line = (Line2D) shape;
			gc.strokeLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
		} else if (shape instanceof Rectangle2D) {
			Rectangle2D rect = (Rectangle2D) shape;
			if (fill) {
				gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
			} else {
				gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
			}
		} else if (shape instanceof Ellipse2D) {
			Ellipse2D ellipse = (Ellipse2D) shape;
			if (fill) {
				gc.fillOval(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());
			} else {
				gc.strokeOval(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());
			}
		} else if (shape instanceof Arc2D) {
			Arc2D arc = (Arc2D) shape;
			ArcType type = arc.getArcType() == Arc2D.PIE ? ArcType.ROUND :
				          arc.getArcType() == Arc2D.CHORD ? ArcType.CHORD : ArcType.OPEN;
			if (fill) {
				gc.fillArc(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(),
					      arc.getAngleStart(), arc.getAngleExtent(), type);
			} else {
				gc.strokeArc(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(),
					        arc.getAngleStart(), arc.getAngleExtent(), type);
			}
		} else if (shape instanceof Path2D) {
			drawPath((Path2D) shape, fill);
		}
	}

	private void drawPath(Path2D path, boolean fill) {
		gc.beginPath();
		java.awt.geom.PathIterator it = path.getPathIterator(null);
		double[] coords = new double[6];

		while (!it.isDone()) {
			int type = it.currentSegment(coords);
			switch (type) {
				case java.awt.geom.PathIterator.SEG_MOVETO:
					gc.moveTo(coords[0], coords[1]);
					break;
				case java.awt.geom.PathIterator.SEG_LINETO:
					gc.lineTo(coords[0], coords[1]);
					break;
				case java.awt.geom.PathIterator.SEG_QUADTO:
					gc.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case java.awt.geom.PathIterator.SEG_CUBICTO:
					gc.bezierCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case java.awt.geom.PathIterator.SEG_CLOSE:
					gc.closePath();
					break;
			}
			it.next();
		}

		if (fill) {
			gc.fill();
		} else {
			gc.stroke();
		}
	}

	// Transform operations
	public void translate(double tx, double ty) {
		gc.translate(tx, ty);
	}

	public void rotate(double theta) {
		gc.rotate(Math.toDegrees(theta));
	}

	public void scale(double sx, double sy) {
		gc.scale(sx, sy);
	}

	public void setTransform(AffineTransform at) {
		Affine affine = new Affine(
			at.getScaleX(), at.getShearY(), at.getTranslateX(),
			at.getShearX(), at.getScaleY(), at.getTranslateY()
		);
		gc.setTransform(affine);
	}

	// Clipping operations
	public void setClip(int x, int y, int width, int height) {
		gc.save();
		gc.beginPath();
		gc.rect(x, y, width, height);
		gc.clip();
	}

	public void clipRect(int x, int y, int width, int height) {
		setClip(x, y, width, height);
	}

	// Clear operations
	public void clearRect(int x, int y, int width, int height) {
		gc.clearRect(x, y, width, height);
	}

	// Get dimensions
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Save and restore state
	public void save() {
		gc.save();
	}

	public void restore() {
		gc.restore();
	}
}
