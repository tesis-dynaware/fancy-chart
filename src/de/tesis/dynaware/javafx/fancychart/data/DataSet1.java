/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data;

import java.util.ArrayList;
import java.util.List;

import de.tesis.dynaware.javafx.fancychart.data.formats.CsvDao;

public class DataSet1 {

	private static final String FILE_NAME = "random_data_1.csv";
	private static List<Double> X_VALS = new ArrayList<>();
	private static List<Double> Y_VALS = new ArrayList<>();

	static {
		String filePath = DataSet1.class.getResource(FILE_NAME).getFile();
		List<List<Double>> data = CsvDao.importCSV(filePath);
		for (List<Double> entries : data) {
			X_VALS.add(entries.get(0));
			Y_VALS.add(entries.get(1));
		}
	}

	public static List<Double> getX() {
		return X_VALS;
	}

	public static List<Double> getY() {
		return Y_VALS;
	}

	public static List<DataItem> getDataItems() {
		final List<DataItem> items = new ArrayList<>();
		for (int i = 0; i < getX().size(); i++) {
			items.add(new DataItem(getX().get(i), getY().get(i)));
		}
		return items;
	}

}
