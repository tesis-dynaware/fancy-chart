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

/**
 * 
 */
public class DataItemSelectionEvent extends Event {

	/**
     * 
     */
	private static final long serialVersionUID = -845991321525777597L;

	/**
	 * The type of this event. Used here and when the event is handled.
	 */
	public static final EventType<DataItemSelectionEvent> TYPE = new EventType<>("DATA_ITEM_SELECTION_CHANGED");

	private final int dataSeriesIndex;
	private final List<Integer> selectedIndices;

	public DataItemSelectionEvent(final int dataSeriesIndex, final List<Integer> selectedIndices) {
		super(TYPE);
		this.dataSeriesIndex = dataSeriesIndex;
		this.selectedIndices = selectedIndices;
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
	public List<Integer> getSelectedIndices() {
		return selectedIndices;
	}

}
