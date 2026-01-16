package log.charter.gui;

import static log.charter.gui.chartPanelDrawers.common.DrawerUtils.editAreaHeight;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import log.charter.data.ChartData;
import log.charter.gui.chartPanelDrawers.ArrangementDrawer;
import log.charter.gui.chartPanelDrawers.common.BackgroundDrawer;
import log.charter.gui.chartPanelDrawers.common.FXGraphicsWrapper;
import log.charter.gui.chartPanelDrawers.common.MarkerDrawer;
import log.charter.services.CharterContext;
import log.charter.services.CharterContext.Initiable;
import log.charter.services.data.ChartTimeHandler;
import log.charter.services.mouseAndKeyboard.KeyboardHandler;
import log.charter.services.mouseAndKeyboard.MouseHandler;

public class ChartPanelFX extends JFXPanel implements Initiable {
	private static final long serialVersionUID = 1L;

	private CharterContext charterContext;
	private ChartData chartData;
	private ChartTimeHandler chartTimeHandler;
	private KeyboardHandler keyboardHandler;
	private MouseHandler mouseHandler;

	private final ArrangementDrawer arrangementDrawer = new ArrangementDrawer();
	private final BackgroundDrawer backgroundDrawer = new BackgroundDrawer();
	private final MarkerDrawer markerDrawer = new MarkerDrawer();

	private Canvas canvas;

	public ChartPanelFX() {
		super();

		setPreferredSize(new Dimension(getWidth(), editAreaHeight));

		Platform.runLater(this::initFX);
	}

	private void initFX() {
		canvas = new Canvas(getWidth(), editAreaHeight);
		final Group root = new Group();
		root.getChildren().add(canvas);
		final Scene scene = new Scene(root);
		setScene(scene);

		canvas.widthProperty().bind(scene.widthProperty());
		canvas.heightProperty().bind(scene.heightProperty());

		canvas.widthProperty().addListener(o -> render());
		canvas.heightProperty().addListener(o -> render());
	}

	@Override
	public void init() {
		charterContext.initObject(arrangementDrawer);
		charterContext.initObject(backgroundDrawer);
		charterContext.initObject(markerDrawer);

		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
		addMouseWheelListener(mouseHandler);
		addKeyListener(keyboardHandler);

		setFocusable(true);
	}

	public void render() {
		if (canvas == null) {
			return;
		}

		Platform.runLater(() -> {
			final GraphicsContext gc = canvas.getGraphicsContext2D();
			final FXGraphicsWrapper wrapper = new FXGraphicsWrapper(gc);

			final double time = chartTimeHandler.time();

			backgroundDrawer.draw(wrapper, time);

			if (chartData.isEmpty) {
				return;
			}

			arrangementDrawer.draw(wrapper, time);
			markerDrawer.draw(wrapper, time);
		});
	}

	@Override
	public void repaint() {
		render();
		super.repaint();
	}
}
