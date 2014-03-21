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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvDao {

	private static final Locale LOCALE = Locale.US;
	private static CSVReader reader;
	private static CSVWriter writer;

	public static SortedMap<Number, Number> importCsv(String filePath) {
		try {
			reader = new CSVReader(new FileReader(filePath));
			List<String[]> entries = reader.readAll();
			SortedMap<Number, Number> data = new TreeMap<>();

			for (String[] line : entries) {
				if (line.length == 2) {
					Number x = NumberFormat.getNumberInstance(LOCALE).parse(line[0]);
					Number y = NumberFormat.getNumberInstance(LOCALE).parse(line[1]);
					data.put(x, y);
				}
			}

			reader.close();
			return data;
		} catch (IOException | ParseException e) {
			System.err.println(e.getMessage());
		}
		return Collections.emptySortedMap();
	}

	public static void exportCsv(SortedMap<Number, Number> data, String filePath) {
		try {
			writer = new CSVWriter(new FileWriter(filePath), ',', '\0');

			List<String[]> entries = new ArrayList<>(data.size());
			for (Number x : data.keySet()) {
				String xString = String.valueOf(x);
				String yString = String.valueOf(data.get(x));
				entries.add(new String[] { xString, yString });
			}

			writer.writeAll(entries);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
