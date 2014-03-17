/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * 
 */
public class DataItem {

	private final DoubleProperty xProperty = new SimpleDoubleProperty(this, "x");
	private final DoubleProperty yProperty = new SimpleDoubleProperty(this, "y");

	public DataItem(final double x, final double y) {
		xProperty.set(x);
		yProperty.set(y);
	}

	public DoubleProperty xProperty() {
		return xProperty;
	}

	public DoubleProperty yProperty() {
		return yProperty;
	}

	public double getX() {
		return xProperty.get();
	}

	public double getY() {
		return yProperty.get();
	}

	public void setX(final int x) {
		xProperty.set(x);
	}

	public void setY(final double v) {
		yProperty.set(v);
	}
}