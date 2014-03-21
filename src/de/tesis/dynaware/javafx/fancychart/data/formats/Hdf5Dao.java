/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data.formats;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import ch.systemsx.cisd.hdf5.IHDF5SimpleWriter;

public class Hdf5Dao {

	private static final String DATA_ID = "xy";
	private static IHDF5SimpleWriter writer;
	private static IHDF5SimpleReader reader;

	public static SortedMap<Number, Number> importHdf5(String filePath) {

		reader = HDF5Factory.openForReading(filePath);
		double[][] hdf5Data = reader.readDoubleMatrix(DATA_ID);

		SortedMap<Number, Number> data = new TreeMap<>();
		for (int i = 0; i < hdf5Data[0].length; i++) {
			double x = hdf5Data[0][i];
			double y = hdf5Data[1][i];
			Number key = new Double(x);
			Number value = new Double(y);
			data.put(key, value);
		}

		return data;
	}

	public static void exportHdf5(SortedMap<Number, Number> data, String filePath) {

		writer = HDF5Factory.open(filePath);
		int size = data.keySet().size();
		double[][] dataArray = new double[2][size];

		int counter = 0;
		Iterator<Number> iter = data.keySet().iterator();
		while (iter.hasNext()) {
			Number x = iter.next();
			Number y = data.get(x);
			if (x instanceof Double) {
				dataArray[0][counter] = ((Double) x).doubleValue();
			} else if (x instanceof Long) {
				dataArray[0][counter] = ((Long) x).doubleValue();
			}

			if (y instanceof Double) {
				dataArray[1][counter] = ((Double) y).doubleValue();
			} else if (y instanceof Long) {
				dataArray[1][counter] = ((Long) y).doubleValue();
			}

			counter++;
		}
		writer.writeDoubleMatrix(DATA_ID, dataArray);
		writer.close();

	}
}
