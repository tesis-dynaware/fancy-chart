/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data.formats;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class XlsDao {

	/**
	 * We set the locale to US to make sure "1.0" means "one", and "1,0" does
	 * not.
	 */
	private static final Locale LOCALE = Locale.getDefault();

	public static SortedMap<java.lang.Number, java.lang.Number> importXls(String filePath) {

		File inputWorkbook = new File(filePath);
		try {
			Workbook w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);

			SortedMap<java.lang.Number, java.lang.Number> data = new TreeMap<>();
			for (int i = 0; i < sheet.getRows(); i++) {
				Cell xCell = sheet.getCell(0, i);
				Cell yCell = sheet.getCell(1, i);
				CellType xType = xCell.getType();
				CellType yType = yCell.getType();
				if (CellType.NUMBER.equals(xType) && CellType.NUMBER.equals(yType)) {
					java.lang.Number key = java.text.NumberFormat.getNumberInstance(LOCALE).parse(xCell.getContents());
					java.lang.Number value = java.text.NumberFormat.getNumberInstance(LOCALE)
							.parse(yCell.getContents());
					data.put(key, value);
				}
			}
			return data;
		} catch (BiffException | IOException | ParseException exception) {
			exception.printStackTrace();
		}

		return Collections.emptySortedMap();
	}

	public static void exportXls(SortedMap<java.lang.Number, java.lang.Number> data, String filePath) {
		File file = new File(filePath);
		WorkbookSettings workBookSettings = new WorkbookSettings();
		workBookSettings.setLocale(LOCALE);

		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file, workBookSettings);
			workbook.createSheet("Exported data", 0);
			WritableSheet sheet = workbook.getSheet(0);

			fillSheet(sheet, data);

			workbook.write();
			workbook.close();
		} catch (IOException | WriteException exception) {
			System.err.println(exception.getMessage());
		}
	}

	private static void fillSheet(WritableSheet sheet, SortedMap<java.lang.Number, java.lang.Number> data)
			throws WriteException, RowsExceededException {

		int row = 0;
		for (java.lang.Number x : data.keySet()) {
			// first column
			addNumber(sheet, 0, row, x);
			// second column
			addNumber(sheet, 1, row++, data.get(x));
		}
	}

	private static void addNumber(WritableSheet sheet, int col, int row, java.lang.Number value) throws WriteException,
			RowsExceededException {
		Number number;
		if (value instanceof Long) {
			number = new Number(col, row, (Long) value);
			sheet.addCell(number);
		} else if (value instanceof Double) {
			WritableCellFormat format = new WritableCellFormat(new NumberFormat("0.###############"));
			number = new Number(col, row, (Double) value, format);
			sheet.addCell(number);
		}
	}
}
