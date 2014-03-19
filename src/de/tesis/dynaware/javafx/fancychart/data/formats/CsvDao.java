/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data.formats;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvDao {

	private static CSVReader reader;
	private static CSVWriter writer;

	public static List<List<Double>> importCSV(String filePath) {
		try {
			reader = new CSVReader(new FileReader(filePath));
			List<String[]> entries = reader.readAll();
			List<List<Double>> vals = new ArrayList<>(entries.size());

			for (String[] entry : entries) {
				List<Double> l = new ArrayList<>();
				for (String string : entry) {
					l.add(Double.parseDouble(string));
				}
				vals.add(l);

			}
			reader.close();
			return vals;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return Collections.emptyList();
	}

	public static void exportCSV(List<List<Double>> data, String filePath) {
		try {
			writer = new CSVWriter(new FileWriter(filePath), ',', '\0');

			List<String[]> entries = new ArrayList<>();
			for (Double col : data.get(0)) {
				int index = data.get(0).indexOf(col);
				String[] strings = new String[data.size()];
				for (int i = 0; i < data.size(); i++) {
					strings[i] = Double.toString(data.get(i).get(index));
				}
				entries.add(strings);

			}
			writer.writeAll(entries);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
