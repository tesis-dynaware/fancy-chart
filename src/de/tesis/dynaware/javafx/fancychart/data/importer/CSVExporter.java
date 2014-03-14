package de.tesis.dynaware.javafx.fancychart.data.importer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVExporter {

	private static CSVWriter writer;

	public static void exportCSV(List<List<Double>> data, String filePath) {
		try {
			writer = new CSVWriter(new FileWriter(filePath));

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
