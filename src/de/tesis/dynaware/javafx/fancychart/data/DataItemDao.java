package de.tesis.dynaware.javafx.fancychart.data;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.tesis.dynaware.javafx.fancychart.data.formats.CsvDao;
import de.tesis.dynaware.javafx.fancychart.data.formats.Hdf5Dao;
import de.tesis.dynaware.javafx.fancychart.data.formats.XlsDao;

public class DataItemDao {

	public static List<DataItem> importFromFile(String filePath, FileFormat fileFormat) {
		if (filePath != null) {
			switch (fileFormat) {
			case CSV:
				SortedMap<Number, Number> csvData = CsvDao.importCsv(filePath);
				return createDataItems(csvData);
			case XLS:
				SortedMap<Number, Number> xlsData = XlsDao.importXls(filePath);
				return createDataItems(xlsData);
			case HDF5:
				SortedMap<Number, Number> hdf5Data = Hdf5Dao.importHdf5(filePath);
				return createDataItems(hdf5Data);
			default:
				break;
			}
		}
		return FXCollections.observableArrayList();
	}

	public static void exportToFile(List<DataItem> dataItems, String filePath, FileFormat fileFormat) {
		SortedMap<Number, Number> data = createEntries(dataItems);
		if (filePath != null) {
			switch (fileFormat) {
			case CSV:
				CsvDao.exportCsv(data, filePath);
				break;
			case XLS:
				XlsDao.exportXls(data, filePath);
				break;
			case HDF5:
				Hdf5Dao.exportHdf5(data, filePath);
				break;
			default:
				break;
			}
		}
	}

	private static SortedMap<Number, Number> createEntries(List<DataItem> dataItems) {
		SortedMap<Number, Number> data = new TreeMap<>();
		for (DataItem item : dataItems) {
			data.put(item.getX(), item.getY());
		}
		return data;
	}

	private static List<DataItem> createDataItems(SortedMap<Number, Number> data) {

		ObservableList<DataItem> items = FXCollections.observableArrayList();
		for (Number x : data.keySet()) {
			Number y = data.get(x);
			DataItem item = new DataItem(x, y);
			items.add(item);
		}

		return items;
	}

	public enum FileFormat {
		CSV("csv"), XLS("xls"), HDF5("h5");

		private final String extension;

		FileFormat(String extension) {
			this.extension = extension;
		}

		public String getFileExtension() {
			return extension;
		}
	}

}
