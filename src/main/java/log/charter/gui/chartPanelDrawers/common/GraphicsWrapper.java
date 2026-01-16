package log.charter.gui.chartPanelDrawers.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

public interface GraphicsWrapper {
	void setAntialiasing(boolean on);

	void setColor(Color color);

	void setFont(Font font);

	void setStroke(int thickness);

	void setClip(int x, int y, int width, int height);

	Object getClip();

	void setClip(Object clip);

	void fillRect(int x, int y, int width, int height);

	void drawRect(int x, int y, int width, int height);

	void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

	void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

	void drawLine(int x1, int y1, int x2, int y2);

	void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	void fillOval(int x, int y, int width, int height);

	void drawImage(BufferedImage image, int x, int y);

	void drawImage(BufferedImage image, int x, int y, int width, int height);

	void drawString(String text, int x, int y);

	void setTransparency(float alpha);

	int getStringWidth(String text, Font font);

	int getAscent(Font font);

	int getDescent(Font font);
}
