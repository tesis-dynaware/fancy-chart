/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package de.tesis.dynaware.javafx.fancychart;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import de.tesis.dynaware.javafx.fancychart.data.DataItem;
import de.tesis.dynaware.javafx.fancychart.events.DataItemSelectionEvent;

/**
 * 
 */
public class TabbedTablesController {

	private static final int TABLE_MIN_WIDTH = 255;
	private static final int TABLE_COL_MIN_WIDTH = 120;

	@FXML
	private VBox tabPaneContainer;
	@FXML
	private TabPane tableTabPane;
	@FXML
	private Tab tab0;
	@FXML
	private Tab tab1;
	@FXML
	private Tab tab2;
	@FXML
	private TableView<DataItem> tableView0;
	@FXML
	private TableView<DataItem> tableView1;
	@FXML
	private TableView<DataItem> tableView2;
	@FXML
	private HBox buttonContainer;
	@FXML
	private Button importButton;
	@FXML
	private Button exportButton;

	private final List<Tab> tabs = new ArrayList<>();
	private final List<TableView<DataItem>> tableViews = new ArrayList<>();
	private final List<ObservableList<DataItem>> dataItems = new ArrayList<>();

	public void initialize() {

		tabs.add(tab0);
		tabs.add(tab1);
		tabs.add(tab2);

		tableViews.add(tableView0);
		tableViews.add(tableView1);
		tableViews.add(tableView2);

		tableTabPane.getSelectionModel().selectedIndexProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(final Observable observable) {
				clearTableSelections();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void initTable(final int index, final ObservableList<DataItem> items) {
		if (index <= dataItems.size()) {
			dataItems.add(items);
		} else {
			dataItems.get(index).setAll(items);
		}

		final TableColumn<DataItem, Double> xCol = new TableColumn<>("X");
		xCol.setCellValueFactory(new PropertyValueFactory<DataItem, Double>("x"));
		xCol.setMinWidth(TABLE_COL_MIN_WIDTH);
		final TableColumn<DataItem, Double> yCol = new TableColumn<>("Y");
		yCol.setCellValueFactory(new PropertyValueFactory<DataItem, Double>("y"));
		yCol.setMinWidth(TABLE_COL_MIN_WIDTH);

		if (index < tableViews.size()) {
			final TableView<DataItem> tableView = tableViews.get(index);
			tableView.setItems(dataItems.get(index));
			tableView.getColumns().setAll(xCol, yCol);

			tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			tableView.setMinWidth(TABLE_MIN_WIDTH);
			tableView.setEditable(true);

			addSelectionListener();
			addCellFactory(xCol, yCol);
		}
	}

	private void addCellFactory(final TableColumn<DataItem, Double> xCol, final TableColumn<DataItem, Double> yCol) {
		final Callback<TableColumn<DataItem, Double>, TableCell<DataItem, Double>> xCellFactory = new Callback<TableColumn<DataItem, Double>, TableCell<DataItem, Double>>() {
			@Override
			public TableCell<DataItem, Double> call(final TableColumn<DataItem, Double> p) {
				return new DoubleEditingCell();
			}
		};
		xCol.setCellFactory(xCellFactory);

		final Callback<TableColumn<DataItem, Double>, TableCell<DataItem, Double>> vCellFactory = new Callback<TableColumn<DataItem, Double>, TableCell<DataItem, Double>>() {
			@Override
			public TableCell<DataItem, Double> call(final TableColumn<DataItem, Double> p) {
				return new DoubleEditingCell();
			}
		};
		yCol.setCellFactory(vCellFactory);
	}

	private void addSelectionListener() {
		for (final TableView<DataItem> tableView : tableViews) {
			tableView.getSelectionModel().getSelectedIndices()
					.addListener(new SelectedTableItemsChangeListener(tableView));
		}
	}

	public void selectDataItem(final int dataSeriesIndex, final int dataItemIndex) {
		clearTableSelections();
		tableTabPane.getSelectionModel().select(dataSeriesIndex);
		tableViews.get(dataSeriesIndex).getSelectionModel().clearAndSelect(dataItemIndex);
		tableViews.get(dataSeriesIndex).getFocusModel().focus(dataItemIndex);
		tableViews.get(dataSeriesIndex).scrollTo(dataItemIndex - 5);
	}

	private void clearTableSelections() {
		for (final TableView<DataItem> tableView : tableViews) {
			tableView.getSelectionModel().clearSelection();
		}
	}

	/**
	 * 
	 * @param <T>
	 */
	private class DoubleEditingCell extends TableCell<DataItem, Double> {

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
		public void updateItem(final Double item, final boolean empty) {
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
					tableView.fireEvent(new DataItemSelectionEvent(tableViews.indexOf(tableView), list));
				} else if (change.wasRemoved()) {
					final ArrayList<Integer> list = new ArrayList<>(change.getRemoved());
					tableView.fireEvent(new DataItemSelectionEvent(tableViews.indexOf(tableView), list));
				}
			}

		}

	}

}
