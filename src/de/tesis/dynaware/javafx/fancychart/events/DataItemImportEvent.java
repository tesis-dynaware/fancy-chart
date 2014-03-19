/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.events;

import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;
import de.tesis.dynaware.javafx.fancychart.data.DataItem;

/**
 * 
 */
public class DataItemImportEvent extends Event {

	private static final long serialVersionUID = -7359387432338173103L;

	/**
	 * The type of this event. Used here and when the event is handled.
	 */
	public static final EventType<DataItemImportEvent> TYPE = new EventType<>("DATA_ITEMS_IMPORTED");

	private final int dataSeriesIndex;
	private final List<DataItem> importedDataItems;

	public DataItemImportEvent(final List<DataItem> importedDataItems, final int dataSeriesIndex) {
		super(TYPE);
		this.importedDataItems = importedDataItems;
		this.dataSeriesIndex = dataSeriesIndex;
	}

	/**
	 * 
	 * @return
	 */
	public int getDataSeriesIndex() {
		return dataSeriesIndex;
	}

	/**
	 * 
	 * @return
	 */
	public List<DataItem> getImportedDataItems() {
		return importedDataItems;
	}

}
