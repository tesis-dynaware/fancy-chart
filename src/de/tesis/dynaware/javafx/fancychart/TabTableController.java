package de.tesis.dynaware.javafx.fancychart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import de.tesis.dynaware.javafx.fancychart.data.DataItem;
import de.tesis.dynaware.javafx.fancychart.data.DataItemDao;
import de.tesis.dynaware.javafx.fancychart.data.DataItemDao.FileFormat;
import de.tesis.dynaware.javafx.fancychart.events.DataItemImportEvent;
import de.tesis.dynaware.javafx.fancychart.events.DataItemSelectionEvent;

public class TabTableController {

	private static final int TABLE_MIN_WIDTH = 255;
	private static final int TABLE_COL_MIN_WIDTH = 120;

	@FXML
	StackPane rootPane;
	@FXML
	TableView<DataItem> tableView;

	@FXML
	private ImportExportPanelController importExportPanelController;

	private int dataSetIndex = 0;

	public void initialize() {
		assignImportExportButtonActions();

	}

	public void setDataSetIndex(int index) {
		this.dataSetIndex = index;
	}

	@SuppressWarnings("unchecked")
	public void initTable(final ObservableList<DataItem> items) {

		final TableColumn<DataItem, Number> xCol = new TableColumn<>("X");
		xCol.setCellValueFactory(new PropertyValueFactory<DataItem, Number>("x"));
		xCol.setMinWidth(TABLE_COL_MIN_WIDTH);
		final TableColumn<DataItem, Number> yCol = new TableColumn<>("Y");
		yCol.setCellValueFactory(new PropertyValueFactory<DataItem, Number>("y"));
		yCol.setMinWidth(TABLE_COL_MIN_WIDTH);

		tableView.setItems(items);
		tableView.getColumns().setAll(xCol, yCol);

		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.setMinWidth(TABLE_MIN_WIDTH);
		tableView.setEditable(true);

		addSelectionListener();
		addCellFactory(xCol, yCol);
	}

	public void selectDataItem(final int dataItemIndex) {
		tableView.getSelectionModel().clearAndSelect(dataItemIndex);
		tableView.getFocusModel().focus(dataItemIndex);
		tableView.scrollTo(dataItemIndex - 5);
	}

	public void clearTableSelection() {
		tableView.getSelectionModel().clearSelection();
	}

	private void addSelectionListener() {
		tableView.getSelectionModel().getSelectedIndices().addListener(new SelectedTableItemsChangeListener(tableView));
	}

	private void assignImportExportButtonActions() {
		importExportPanelController.getImportButton().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				importFromFile(dataSetIndex, importExportPanelController.getSelectedFileFormat());
			}
		});

		importExportPanelController.getExportButton().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				exportToFile(tableView.getItems(), importExportPanelController.getSelectedFileFormat());
			}
		});
	}

	private void importFromFile(int index, FileFormat fileFormat) {
		FileChooser fileChooser = createFileChooser("Import a " + fileFormat.name() + " file", fileFormat);
		File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());
		if (file != null) {
			List<DataItem> dataItems = DataItemDao.importFromFile(file.getAbsolutePath(), fileFormat);
			tableView.fireEvent(new DataItemImportEvent(dataItems, index));
		}
	}

	private void exportToFile(List<DataItem> dataItems, FileFormat fileFormat) {
		FileChooser fileChooser = createFileChooser("Export a " + fileFormat + " file", fileFormat);
		fileChooser.setInitialFileName("export." + fileFormat.getFileExtension());
		File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
		if (file != null) {
			DataItemDao.exportToFile(dataItems, file.getAbsolutePath(), fileFormat);
		}
	}

	private static FileChooser createFileChooser(String title, FileFormat fileFormat) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(File.listRoots()[0]);
		fileChooser.setTitle(title);
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter(fileFormat.name() + " files", "*."
				+ fileFormat.getFileExtension()));
		return fileChooser;
	}

	private void addCellFactory(final TableColumn<DataItem, Number> xCol, final TableColumn<DataItem, Number> yCol) {
		final Callback<TableColumn<DataItem, Number>, TableCell<DataItem, Number>> xCellFactory = new Callback<TableColumn<DataItem, Number>, TableCell<DataItem, Number>>() {
			@Override
			public TableCell<DataItem, Number> call(final TableColumn<DataItem, Number> p) {
				return new DoubleEditingCell();
			}
		};
		xCol.setCellFactory(xCellFactory);

		final Callback<TableColumn<DataItem, Number>, TableCell<DataItem, Number>> vCellFactory = new Callback<TableColumn<DataItem, Number>, TableCell<DataItem, Number>>() {
			@Override
			public TableCell<DataItem, Number> call(final TableColumn<DataItem, Number> p) {
				return new DoubleEditingCell();
			}
		};
		yCol.setCellFactory(vCellFactory);
	}

	/**
    *
    */
	private final class SelectedTableItemsChangeListener implements ListChangeListener<Integer> {

		private final TableView<DataItem> tableView;

		public SelectedTableItemsChangeListener(final TableView<DataItem> tableView) {
			this.tableView = tableView;
		}

		@Override
		public void onChanged(final ListChangeListener.Change<? extends Integer> change) {
			final boolean next = change.next();
			if (next) {
				if (change.wasAdded()) {
					final ArrayList<Integer> list = new ArrayList<>(change.getAddedSubList());
					tableView.fireEvent(new DataItemSelectionEvent(dataSetIndex, list));
				} else if (change.wasRemoved()) {
					final ArrayList<Integer> list = new ArrayList<>(change.getRemoved());
					tableView.fireEvent(new DataItemSelectionEvent(dataSetIndex, list));
				}
			}
		}
	}

	/**
	 * 
	 * @param <T>
	 */
	private class DoubleEditingCell extends TableCell<DataItem, Number> {

		protected TextField textField;

		@Override
		public void startEdit() {
			if (!isEmpty()) {
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			setText(getItem().toString());
			setGraphic(null);
		}

		@Override
		public void updateItem(final Number item, final boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
			final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(final ObservableValue<? extends Boolean> value, final Boolean oldValue,
						final Boolean newValue) {
					if (!newValue) {
						setValue();
					}
				}

			};
			textField.focusedProperty().addListener(changeListener);
			textField.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
				@Override
				public void handle(final KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						setValue();
					}
				}
			});
		}

		private void setValue() {
			try {
				final double input = Double.valueOf(textField.getText());
				commitEdit(input);
			} catch (final NumberFormatException exception) {
				System.err.println(exception.getMessage());
				cancelEdit();
			}
		}

		private String getString() {
			if (getItem() == null) {
				return "";
			}
			return getItem().toString();
		}
	}

}
