/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 */
public class DataItem {

	private final Property<Number> xProperty = new SimpleObjectProperty<Number>(this, "x");
	private final Property<Number> yProperty = new SimpleObjectProperty<Number>(this, "y");

	public DataItem(final Number x, final Number y) {
		xProperty.setValue(x);
		yProperty.setValue(y);
	}

	public Property<Number> xProperty() {
		return xProperty;
	}

	public Property<Number> yProperty() {
		return yProperty;
	}

	public Number getX() {
		return xProperty.getValue();
	}

	public Number getY() {
		return yProperty.getValue();
	}

	public void setX(final Number x) {
		xProperty.setValue(x);
	}

	public void setY(final Number v) {
		yProperty.setValue(v);
	}
}