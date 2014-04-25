/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import de.tesis.dynaware.javafx.fancychart.data.DataItem;
import de.tesis.dynaware.javafx.fancychart.data.DefaultDataSet1;
import de.tesis.dynaware.javafx.fancychart.data.DefaultDataSet2;
import de.tesis.dynaware.javafx.fancychart.data.DefaultDataSet3;
import de.tesis.dynaware.javafx.fancychart.events.DataItemImportEvent;
import de.tesis.dynaware.javafx.fancychart.events.DataItemSelectionEvent;
import de.tesis.dynaware.javafx.fancychart.zoom.Zoom;

/**
 * 
 */
public class FancyChartController {

	private static final int CHART_AXIS_TICK_UNIT = 5;

	private static final int NUMBER_OF_DATA_SETS = 3;

	/**
	 * use these default colours for the line chart when you're running JavaFX 2 (Java 7)
	 */
	private static final String CHART_SERIES_DEFAULT_COLOR_0_FX2 = "#f9d900";
	private static final String CHART_SERIES_DEFAULT_COLOR_1_FX2 = "#a9e200";
	private static final String CHART_SERIES_DEFAULT_COLOR_2_FX2 = "#22bad9";

	/**
	 * use these default colours for the line chart when you're running JavaFX 8 (Java 8)
	 */
	private static final String CHART_SERIES_DEFAULT_COLOR_0_FX8 = "#f3622d";
	private static final String CHART_SERIES_DEFAULT_COLOR_1_FX8 = "#fba71b";
	private static final String CHART_SERIES_DEFAULT_COLOR_2_FX8 = "#57b757";

	private static final int DATA_POINT_POPUP_WIDTH = 30;
	private static final int DATA_POINT_POPUP_HEIGHT = 15;
	private static final int RGB_MAX = 255;

	private static final double REGULAR_SCALE = 0.5;
	private static final double SELECTED_SCALE = 1.2;

	private static String toRGBCode(final Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * RGB_MAX), (int) (color.getGreen() * RGB_MAX),
				(int) (color.getBlue() * RGB_MAX));
	}

	private final List<ObservableList<DataItem>> ALL_DATA_SETS = new ArrayList<>();

	@FXML
	private StackPane rootPane;
	@FXML
	private HBox chartPage;
	@FXML
	private StackPane tabPaneContainer;
	@FXML
	private VBox chartBox;
	@FXML
	private HBox colorPickerBox;
	@FXML
	private ColorPicker colorPicker0;
	@FXML
	private ColorPicker colorPicker1;
	@FXML
	private ColorPicker colorPicker2;
	@FXML
	private TabbedTablesController tabPaneContainerController;

	private final ObservableList<Color> seriesColors = FXCollections.observableArrayList();
	private final List<ColorPicker> colorPickers = new ArrayList<>();

	private LineChart<Number, Number> chart;
	private StackPane chartPane;
	private NumberAxis xAxis;
	private NumberAxis yAxis;

	public void initialize() {
		initAllDataSet();
		initTables();
		setupColors();
		setupColorPickers();
		loadDefaultDataSets();
		createChart();
		setDataPointPopup();
		initTabPane();
		addZoom();
	}

	/**
	 * Adds zoom functionality for the line series in the chart.
	 */
	private void addZoom() {
		Zoom zoom = new Zoom(chart, chartPane);
	}

	/**
	 * Initialises the tables in the tab pane.
	 */
	private void initTables() {
		for (int i = 0; i < ALL_DATA_SETS.size(); i++) {
			tabPaneContainerController.initTable(i, ALL_DATA_SETS.get(i));
		}
	}

	private void setupColorPickers() {
		colorPickers.add(colorPicker0);
		colorPickers.add(colorPicker1);
		colorPickers.add(colorPicker2);

		colorPicker0.setValue(seriesColors.get(0));
		colorPicker1.setValue(seriesColors.get(1));
		colorPicker2.setValue(seriesColors.get(2));

	}

	/**
	 * Initialises the data sets with empty lists.
	 */
	private void initAllDataSet() {
		for (int i = 0; i < NUMBER_OF_DATA_SETS; i++) {
			final ObservableList<DataItem> dataItems = FXCollections.observableArrayList();
			ALL_DATA_SETS.add(dataItems);
		}
	}

	/**
	 * Adds a listener that updates the chart items when the a data set changes
	 */
	private void addDataItemChangeListener() {

		for (int i = 0; i < ALL_DATA_SETS.size(); i++) {
			ObservableList<DataItem> dataItems = ALL_DATA_SETS.get(i);
			final int index = i;
			dataItems.addListener(new ListChangeListener<DataItem>() {
				@Override
				public void onChanged(Change<? extends DataItem> change) {
					if (change.next()) {
						setDataItems(dataItems, index);
					}
				}
			});
			// set initial values
			setDataItems(dataItems, index);
		}

	}

	private void setupColors() {
		seriesColors.add(Color.web(CHART_SERIES_DEFAULT_COLOR_0_FX8));
		seriesColors.add(Color.web(CHART_SERIES_DEFAULT_COLOR_1_FX8));
		seriesColors.add(Color.web(CHART_SERIES_DEFAULT_COLOR_2_FX8));

		seriesColors.addListener(new ListChangeListener<Color>() {

			@Override
			public void onChanged(final Change<? extends Color> change) {
				change.next();
				if (change.wasAdded()) {

					int index = change.getFrom();
					final List<? extends Color> addedSubList = change.getAddedSubList();

					for (final Color color : addedSubList) {

						final Series<Number, Number> series = chart.getData().get(index);
						final String newWebColor = toRGBCode(color);
						final String strokeStyle = "-fx-stroke: " + newWebColor + ";";
						final String backgroundColorStyle = "-fx-background-color: " + newWebColor + ", white;";

						// set line color
						series.getNode().setStyle(strokeStyle);

						// set data point color
						for (final Data<Number, Number> data : series.getData()) {
							data.getNode().setStyle(backgroundColorStyle);
						}

						// set legend item color
						final Set<Node> nodes = chart.lookupAll(".chart-legend-item-symbol.default-color" + index);
						for (final Node n : nodes) {
							n.setStyle(backgroundColorStyle);
						}
						index++;
					}
				}
			}
		});

	}

	@FXML
	private void setColor(final ActionEvent event) {

		final Object source = event.getSource();
		if (source instanceof ColorPicker) {
			final ColorPicker picker = (ColorPicker) source;
			final Color newColor = picker.getValue();
			if (newColor == null) {
				return;
			}
			seriesColors.set(colorPickers.indexOf(picker), newColor);
		}

	}

	private void loadDefaultDataSets() {
		ALL_DATA_SETS.get(0).setAll(DefaultDataSet1.getDataItems());
		ALL_DATA_SETS.get(1).setAll(DefaultDataSet2.getDataItems());
		ALL_DATA_SETS.get(2).setAll(DefaultDataSet3.getDataItems());
	}

	private void setDataItems(List<DataItem> items, int index) {
		Series<Number, Number> series = chart.getData().get(index);

		clearSeries(series);

		for (DataItem dataItem : items) {
			final Data<Number, Number> data = new XYChart.Data<Number, Number>();
			data.YValueProperty().bind(dataItem.yProperty());
			data.XValueProperty().bind(dataItem.xProperty());
			series.getData().add(data);
		}

	}

	private void clearSeries(Series<Number, Number> series) {
		for (Data<Number, Number> data : series.getData()) {
			data.XValueProperty().unbind();
			data.YValueProperty().unbind();
		}
		series.getData().clear();
	}

	private void createChart() {

		xAxis = new NumberAxis();// lowerBoundX, upperBoundX, CHART_AXIS_TICK_UNIT);
		yAxis = new NumberAxis();// lowerBoundY, upperBoundY, CHART_AXIS_TICK_UNIT);
		chart = new LineChart<>(xAxis, yAxis);
		xAxis.setLabel("X");
		yAxis.setLabel("Y");

		final List<XYChart.Series<Number, Number>> seriesList = createChartSeries();
		chart.getData().addAll(seriesList);

		chartPane = new StackPane(chart);

		VBox.setVgrow(chartPane, Priority.ALWAYS);

		chartBox.getChildren().add(0, chartPane);
		addDataItemChangeListener();
		addSelectionListener();
		addDataImportListener();
	}

	private void addDataImportListener() {
		rootPane.addEventHandler(DataItemImportEvent.TYPE, new EventHandler<DataItemImportEvent>() {

			@Override
			public void handle(DataItemImportEvent event) {
				int index = event.getDataSeriesIndex();
				List<DataItem> importedDataItems = event.getImportedDataItems();
				ALL_DATA_SETS.get(index).setAll(importedDataItems);
				setDataItems(importedDataItems, index);
			}
		});

	}

	private void initTabPane() {
		tabPaneContainer.maxHeightProperty().bind(chart.heightProperty().subtract(60.0));
	}

	private void addSelectionListener() {
		rootPane.addEventHandler(DataItemSelectionEvent.TYPE, new EventHandler<DataItemSelectionEvent>() {

			@Override
			public void handle(final DataItemSelectionEvent event) {
				final int dataSeriesIndex = event.getDataSeriesIndex();
				final List<Integer> selectedIndices = event.getSelectedIndices();
				setScale(dataSeriesIndex, selectedIndices);
			}
		});
	}

	private void setScale(final int dataSeriesIndex, final List<? extends Integer> indices) {
		clearChartSelections();
		final ObservableList<Data<Number, Number>> data = chart.getData().get(dataSeriesIndex).getData();
		for (final int i : indices) {
			if (i < data.size()) {
				final Node newNode = data.get(i).getNode();
				newNode.setScaleX(SELECTED_SCALE);
				newNode.setScaleY(SELECTED_SCALE);
			}
		}
	}

	/**
	 * Sets all scales to regular for all data item in the chart.
	 */
	private void clearChartSelections() {
		for (final Series<Number, Number> series : chart.getData()) {
			for (final Data<Number, Number> dataItem : series.getData()) {
				final Node newNode = dataItem.getNode();
				newNode.setScaleX(REGULAR_SCALE);
				newNode.setScaleY(REGULAR_SCALE);
			}
		}
	}

	private void setDataPointPopup() {
		final Popup popup = new Popup();
		popup.setHeight(DATA_POINT_POPUP_HEIGHT);
		popup.setWidth(DATA_POINT_POPUP_WIDTH);

		for (int i = 0; i < chart.getData().size(); i++) {
			final int dataSeriesIndex = i;
			final XYChart.Series<Number, Number> series = chart.getData().get(i);
			for (final Data<Number, Number> data : series.getData()) {
				final Node node = data.getNode();
				node.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {

					private static final int X_OFFSET = 15;
					private static final int Y_OFFSET = -5;
					final Label label = new Label();

					@Override
					public void handle(final MouseEvent event) {

						final String colorString = toRGBCode(seriesColors.get(dataSeriesIndex));
						popup.getContent().setAll(label);
						label.setStyle("-fx-background-color: " + colorString + "; -fx-border-color: " + colorString
								+ ";");
						label.setText("x=" + data.getXValue() + ", y=" + data.getYValue());
						popup.show(data.getNode().getScene().getWindow(), event.getScreenX() + X_OFFSET,
								event.getScreenY() + Y_OFFSET);
						event.consume();
					}

					public EventHandler<MouseEvent> init() {
						label.getStyleClass().add("chart-popup-label");
						return this;
					}

				}.init());

				node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {

					@Override
					public void handle(final MouseEvent event) {
						popup.hide();
						event.consume();
					}
				});

				// this handler selects the corresponding table item when a data
				// item in the chart was clicked.
				node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(final MouseEvent event) {
						final int dataItemIndex = series.getData().indexOf(data);
						tabPaneContainerController.selectDataItem(dataSeriesIndex, dataItemIndex);
						event.consume();
					}
				});
			}
		}

	}

	private List<XYChart.Series<Number, Number>> createChartSeries() {

		final List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
		for (int i = 0; i < ALL_DATA_SETS.size(); i++) {
			final XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName("Data Set " + i);
			seriesList.add(series);
		}

		return seriesList;

	}

}