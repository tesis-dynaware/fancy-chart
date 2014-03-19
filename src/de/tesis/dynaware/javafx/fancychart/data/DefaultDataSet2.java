/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data;

import java.util.List;

import de.tesis.dynaware.javafx.fancychart.data.DataItemDao.FileFormat;

public class DefaultDataSet2 {

	private static final String FILE_NAME = "random_data_2.csv";

	public static List<DataItem> getDataItems() {
		String filePath = DefaultDataSet2.class.getResource(FILE_NAME).getFile();
		return DataItemDao.importFromFile(filePath, FileFormat.CSV);
	}

}
