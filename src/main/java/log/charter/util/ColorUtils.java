package log.charter.util;

import javafx.scene.paint.Color;

public class ColorUtils {
	private static double clamp(final double colorValue) {
		return Math.max(0, Math.min(1.0, colorValue));
	}

	public static Color multiplyColor(final Color c, final double multiplier) {
		return new Color(clamp(c.getRed() * multiplier), //
				clamp(c.getGreen() * multiplier), //
				clamp(c.getBlue() * multiplier), //
				c.getOpacity());
	}

	public static Color mix(final Color c0, final Color c1, final double mix) {
		final double red = c0.getRed() * (1 - mix) + c1.getRed() * mix;
		final double green = c0.getGreen() * (1 - mix) + c1.getGreen() * mix;
		final double blue = c0.getBlue() * (1 - mix) + c1.getBlue() * mix;
		final double opacity = c0.getOpacity() * (1 - mix) + c1.getOpacity() * mix;
		return new Color(clamp(red), clamp(green), clamp(blue), clamp(opacity));
	}

	public static Color setAlpha(final Color c, final int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha / 255.0);
	}

	public static Color transparent(final Color c) {
		return setAlpha(c, 0);
	}
}
