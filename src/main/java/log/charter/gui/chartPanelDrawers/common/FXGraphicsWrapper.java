package log.charter.gui.chartPanelDrawers.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FXGraphicsWrapper implements GraphicsWrapper {
	private final GraphicsContext gc;

	public FXGraphicsWrapper(final GraphicsContext gc) {
		this.gc = gc;
	}

	@Override
	public void setAntialiasing(final boolean on) {
		// JavaFX Canvas is usually anti-aliased by default or managed via other means
	}

	private javafx.scene.paint.Color convertColor(final Color color) {
		return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255.0);
	}

	@Override
	public void setColor(final Color color) {
		final javafx.scene.paint.Color fxColor = convertColor(color);
		gc.setFill(fxColor);
		gc.setStroke(fxColor);
	}

	@Override
	public void setFont(final Font font) {
		final javafx.scene.text.Font fxFont = javafx.scene.text.Font.font(font.getFamily(),
				font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, font.getSize());
		gc.setFont(fxFont);
	}

	@Override
	public void setStroke(final int thickness) {
		gc.setLineWidth(thickness);
		gc.setLineCap(StrokeLineCap.BUTT);
	}

	@Override
	public void setClip(final int x, final int y, final int width, final int height) {
		gc.save();
		gc.beginPath();
		gc.rect(x, y, width, height);
		gc.clip();
	}

	@Override
	public Object getClip() {
		return null; // Not easily supported in JavaFX Canvas without tracking
	}

	@Override
	public void setClip(final Object clip) {
		if (clip == null) {
			gc.restore();
		}
	}

	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		gc.fillRect(x, y, width, height);
	}

	@Override
	public void drawRect(final int x, final int y, final int width, final int height) {
		gc.strokeRect(x, y, width, height);
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight) {
		gc.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
		final double[] xd = new double[nPoints];
		final double[] yd = new double[nPoints];
		for (int i = 0; i < nPoints; i++) {
			xd[i] = xPoints[i];
			yd[i] = yPoints[i];
		}
		gc.fillPolygon(xd, yd, nPoints);
	}

	@Override
	public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
		final double[] xd = new double[nPoints];
		final double[] yd = new double[nPoints];
		for (int i = 0; i < nPoints; i++) {
			xd[i] = xPoints[i];
			yd[i] = yPoints[i];
		}
		gc.strokePolyline(xd, yd, nPoints);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		gc.strokeLine(x1, y1, x2, y2);
	}

	@Override
	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		gc.fillArc(x, y, width, height, startAngle, arcAngle, ArcType.ROUND);
	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
		gc.fillOval(x, y, width, height);
	}

	@Override
	public void drawImage(final BufferedImage image, final int x, final int y) {
		gc.drawImage(SwingFXUtils.toFXImage(image, null), x, y);
	}

	@Override
	public void drawImage(final BufferedImage image, final int x, final int y, final int width, final int height) {
		gc.drawImage(SwingFXUtils.toFXImage(image, null), x, y, width, height);
	}

	@Override
	public void drawString(final String text, final int x, final int y) {
		gc.fillText(text, x, y);
	}

	@Override
	public void setTransparency(final float alpha) {
		gc.setGlobalAlpha(alpha);
	}

	@Override
	public int getStringWidth(final String text, final Font font) {
		final Text helper = new Text(text);
		helper.setFont(javafx.scene.text.Font.font(font.getFamily(), font.getSize()));
		return (int) helper.getLayoutBounds().getWidth();
	}

	@Override
	public int getAscent(final Font font) {
		final Text helper = new Text("Hg");
		helper.setFont(javafx.scene.text.Font.font(font.getFamily(), font.getSize()));
		return (int) helper.getBaselineOffset();
	}

	@Override
	public int getDescent(final Font font) {
		final Text helper = new Text("Hg");
		helper.setFont(javafx.scene.text.Font.font(font.getFamily(), font.getSize()));
		return (int) (helper.getLayoutBounds().getHeight() - helper.getBaselineOffset());
	}
}
