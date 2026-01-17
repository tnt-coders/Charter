package log.charter.services.mouseAndKeyboard;

import static java.lang.Math.abs;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import log.charter.data.types.PositionWithIdAndType;
import log.charter.util.data.Position2D;

public class MouseButtonPressReleaseHandler {
	public enum CustomMouseButton {
		LEFT_BUTTON, //
		RIGHT_BUTTON;

		public static CustomMouseButton fromEvent(final MouseEvent e) {
			if (e.getButton() == MouseButton.PRIMARY) {
				return LEFT_BUTTON;
			}
			if (e.getButton() == MouseButton.SECONDARY) {
				return RIGHT_BUTTON;
			}

			return null;
		}
	}

	public static class MouseButtonPressData {
		public final CustomMouseButton button;
		public final Position2D position;
		public final PositionWithIdAndType highlight;

		public MouseButtonPressData(final CustomMouseButton button, final Position2D position,
				final PositionWithIdAndType highlight) {
			this.button = button;
			this.position = position;
			this.highlight = highlight;
		}
	}

	public static class MouseButtonPressReleaseData {
		public final CustomMouseButton button;
		public final PositionWithIdAndType pressHighlight;
		public final PositionWithIdAndType releaseHighlight;
		public final Position2D pressPosition;
		public final Position2D releasePosition;

		public MouseButtonPressReleaseData(final MouseButtonPressData pressData,
				final PositionWithIdAndType releaseHighlight, final int releaseX, final int releaseY) {
			button = pressData.button;
			pressHighlight = pressData.highlight;
			this.releaseHighlight = releaseHighlight;
			pressPosition = pressData.position;
			releasePosition = new Position2D(releaseX, releaseY);
		}

		public boolean isXDrag() {
			return abs(releasePosition.x - pressPosition.x) > 5;
		}
	}

	private HighlightManager highlightManager;

	private final Map<CustomMouseButton, MouseButtonPressData> pressedButtons = new HashMap<>();

	public void press(final MouseEvent e) {
		final CustomMouseButton button = CustomMouseButton.fromEvent(e);
		if (button != null) {
			final Position2D position = new Position2D((int) e.getX(), (int) e.getY());
			final PositionWithIdAndType highlight = highlightManager.getHighlight(position.x, position.y);
			final MouseButtonPressData pressData = new MouseButtonPressData(button, position, highlight);
			pressedButtons.put(button, pressData);
		}
	}

	public MouseButtonPressData getPressPosition(final CustomMouseButton button) {
		return pressedButtons.get(button);
	}

	public MouseButtonPressReleaseData release(final MouseEvent e) {
		final CustomMouseButton button = CustomMouseButton.fromEvent(e);
		if (button == null) {
			return null;
		}

		final MouseButtonPressData pressData = pressedButtons.get(button);
		if (pressData == null) {
			return null;
		}

		final int x = (int) e.getX();
		final int y = (int) e.getY();
		final PositionWithIdAndType highlight = highlightManager.getHighlight(x, y);
		return new MouseButtonPressReleaseData(pressData, highlight, x, y);
	}

	public void remove(final MouseEvent e) {
		final CustomMouseButton button = CustomMouseButton.fromEvent(e);
		if (button != null) {
			pressedButtons.remove(button);
		}
	}

	public void clear() {
		pressedButtons.clear();
	}
}
