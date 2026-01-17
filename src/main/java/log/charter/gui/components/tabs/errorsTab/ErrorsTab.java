package log.charter.gui.components.tabs.errorsTab;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import log.charter.data.config.Localization.Label;
import log.charter.gui.components.containers.RowedPanel;

public class ErrorsTab extends RowedPanel {

	private TableView<ChartError> errorsTable;

	private List<ChartError> errorsBuffer = new ArrayList<>();
	private List<ChartError> internalBuffer = new ArrayList<>();
	private final List<ChartError> errors = new ArrayList<>();

	public ErrorsTab() {
		errorsTable = new TableView<>();
		final TableColumn<ChartError, String> positionColumn = new TableColumn<>(Label.ERRORS_TAB_POSITION.label());
		positionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().position.description));
		
		final TableColumn<ChartError, String> severityColumn = new TableColumn<>(Label.ERRORS_TAB_SEVERITY.label());
		severityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().severity.name()));

		final TableColumn<ChartError, String> descriptionColumn = new TableColumn<>(Label.ERRORS_TAB_DESCRIPTION.label());
		descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().message));

		errorsTable.getColumns().add(positionColumn);
		errorsTable.getColumns().add(severityColumn);
		errorsTable.getColumns().add(descriptionColumn);

		getChildren().add(errorsTable);
	}

	public void addError(final ChartError chartError) {
		errorsBuffer.add(chartError);
	}

	public void swapBuffer() {
		internalBuffer = errorsBuffer;
		errorsBuffer = new ArrayList<>();
		
		errorsTable.getItems().setAll(internalBuffer);
	}
}
