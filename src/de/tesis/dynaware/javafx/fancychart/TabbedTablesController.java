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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import de.tesis.dynaware.javafx.fancychart.data.DataItem;

/**
 * 
 */
public class TabbedTablesController {

	@FXML
	private StackPane tabPaneContainer;
	@FXML
	private TabPane tableTabPane;
	@FXML
	private TabTableController table0Controller;
	@FXML
	private TabTableController table1Controller;
	@FXML
	private TabTableController table2Controller;

	private List<TabTableController> tableControllers;

	public void initialize() {

		tableControllers = new ArrayList<>(3);
		tableControllers.add(table0Controller);
		tableControllers.add(table1Controller);
		tableControllers.add(table2Controller);

		for (int i = 0; i < tableControllers.size(); i++) {
			tableControllers.get(i).setDataSetIndex(i);
		}

		tableTabPane.getSelectionModel().selectedIndexProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(final Observable observable) {
				clearAllTableSelections();
			}
		});

	}

	public void initTable(final int index, final ObservableList<DataItem> items) {
		tableControllers.get(index).initTable(items);
	}

	public void selectDataItem(final int dataSeriesIndex, final int dataItemIndex) {
		tableTabPane.getSelectionModel().select(dataSeriesIndex);
		clearAllTableSelections();
		tableControllers.get(dataSeriesIndex).selectDataItem(dataItemIndex);

	}

	private void clearAllTableSelections() {
		for (TabTableController tableController : tableControllers) {
			tableController.clearTableSelection();
		}
	}

}
