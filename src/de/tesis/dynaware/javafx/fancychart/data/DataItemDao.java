package de.tesis.dynaware.javafx.fancychart.data;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.tesis.dynaware.javafx.fancychart.data.formats.CsvDao;

public class DataItemDao {

	public static List<DataItem> importFromFile(String filePath, FileFormat fileFormat) {
		if (filePath != null) {
			if (FileFormat.CSV.equals(fileFormat)) {
				List<List<Double>> data = CsvDao.importCSV(filePath);
				return createDataItems(data);
			}
		}
		return FXCollections.observableArrayList();
	}

	public static void exportToFile(List<DataItem> dataItems, String filePath, FileFormat fileFormat) {
		List<List<Double>> data = createCsvEntries(dataItems);
		if (filePath != null) {
			if (FileFormat.CSV.equals(fileFormat)) {
				CsvDao.exportCSV(data, filePath);
			}
		}
	}

	private static List<List<Double>> createCsvEntries(List<DataItem> dataItems) {
		List<Double> xVals = new ArrayList<>();
		List<Double> yVals = new ArrayList<>();

		List<List<Double>> data = new ArrayList<>(2);
		data.add(xVals);
		data.add(yVals);

		for (DataItem item : dataItems) {
			xVals.add(item.getX());
			yVals.add(item.getY());
		}
		return data;
	}

	private static List<DataItem> createDataItems(List<List<Double>> data) {
		List<Double> xVals = new ArrayList<>();
		List<Double> yVals = new ArrayList<>();

		for (List<Double> entries : data) {
			xVals.add(entries.get(0));
			yVals.add(entries.get(1));
		}

		ObservableList<DataItem> items = FXCollections.observableArrayList();
		for (Double x : xVals) {
			int index = xVals.indexOf(x);
			Double y = yVals.get(index);
			DataItem item = new DataItem(x, y);
			items.add(item);
		}
		return items;
	}

	public enum FileFormat {
		CSV, EXCEL, HDF
	}

}
