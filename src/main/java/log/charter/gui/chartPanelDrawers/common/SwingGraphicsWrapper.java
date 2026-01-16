package log.charter.gui.chartPanelDrawers.common;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class SwingGraphicsWrapper implements GraphicsWrapper {
	private final Graphics2D g;

	public SwingGraphicsWrapper(final Graphics2D g) {
		this.g = g;
	}

	@Override
	public void setAntialiasing(final boolean on) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				on ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	@Override
	public void setColor(final Color color) {
		g.setColor(color);
	}

	@Override
	public void setFont(final Font font) {
		g.setFont(font);
	}

	@Override
	public void setStroke(final int thickness) {
		g.setStroke(new BasicStroke(thickness));
	}

	@Override
	public void setClip(final int x, final int y, final int width, final int height) {
		g.setClip(x, y, width, height);
	}

	@Override
	public Object getClip() {
		return g.getClip();
	}

	@Override
	public void setClip(final Object clip) {
		g.setClip((Shape) clip);
	}

	@Override
	public void fillRect(final int x, final int y, final int width, final int height) {
		g.fillRect(x, y, width, height);
	}

	@Override
	public void drawRect(final int x, final int y, final int width, final int height) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth,
			final int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	@Override
	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
		g.fillOval(x, y, width, height);
	}

	@Override
	public void drawImage(final BufferedImage image, final int x, final int y) {
		g.drawImage(image, x, y, null);
	}

	@Override
	public void drawImage(final BufferedImage image, final int x, final int y, final int width, final int height) {
		g.drawImage(image, x, y, width, height, null);
	}

	@Override
	public void drawString(final String text, final int x, final int y) {
		g.drawString(text, x, y);
	}

	@Override
	public void setTransparency(final float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	}

	@Override
	public int getStringWidth(final String text, final Font font) {
		final FontMetrics fm = g.getFontMetrics(font);
		return fm.stringWidth(text);
	}

	@Override
	public int getAscent(final Font font) {
		final FontMetrics fm = g.getFontMetrics(font);
		return fm.getAscent();
	}

	@Override
	public int getDescent(final Font font) {
		final FontMetrics fm = g.getFontMetrics(font);
		return fm.getDescent();
	}
}
