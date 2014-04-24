/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package de.tesis.dynaware.javafx.fancychart.zoom;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * This class adds a zoom functionality to a given XY chart. Zoom means that a user can select a region in the chart
 * that should be displayed at a larger scale.
 *
 */
public class Zoom {

	private static final String INFO_LABEL_ID = "zoomInfoLabel";

	private final double X_LOWER_BOUND_DEFAULT = 0;
	private final double X_UPPER_BOUND_DEFAULT = 100;
	private final double Y_LOWER_BOUND_DEFAULT = 0;
	private final double Y_UPPER_BOUND_DEFAULT = 100;

	private double defaultLowerBoundX = X_LOWER_BOUND_DEFAULT;
	private double defaultUpperBoundX = X_UPPER_BOUND_DEFAULT;
	private double defaultLowerBoundY = Y_LOWER_BOUND_DEFAULT;
	private double defaultUpperBoundY = Y_UPPER_BOUND_DEFAULT;

	private final Pane pane;
	private final XYChart<Number, Number> chart;
	private final NumberAxis xAxis;
	private final NumberAxis yAxis;
	private final SelectionRectangle selectionRectangle;
	private Label infoLabel;

	private Point2D selectionRectangleStart;
	private Point2D selectionRectangleEnd;

	/**
	 * Create a new instance of this class with the given chart and pane instances. The {@link Pane} instance is needed
	 * as a parent for the rectangle that represents the user selection.
	 * 
	 * @param chart
	 *            the xy chart to which the zoom support should be added
	 * @param pane
	 *            the pane on which the selection rectangle will be drawn.
	 */
	public Zoom(XYChart<Number, Number> chart, Pane pane) {
		this.pane = pane;
		this.chart = chart;
		this.xAxis = (NumberAxis) chart.getXAxis();
		this.yAxis = (NumberAxis) chart.getYAxis();
		selectionRectangle = new SelectionRectangle();
		pane.getChildren().add(selectionRectangle);
		addDragSelectionMechanism();
		addInfoLabel();
	}

	/**
	 * @return the defaultLowerBoundX
	 */
	public final double getDefaultLowerBoundX() {
		return defaultLowerBoundX;
	}

	/**
	 * @param defaultLowerBoundX
	 *            the defaultLowerBoundX to set
	 */
	public final void setDefaultLowerBoundX(double defaultLowerBoundX) {
		this.defaultLowerBoundX = defaultLowerBoundX;
	}

	/**
	 * @return the defaultUpperBoundX
	 */
	public final double getDefaultUpperBoundX() {
		return defaultUpperBoundX;
	}

	/**
	 * @param defaultUpperBoundX
	 *            the defaultUpperBoundX to set
	 */
	public final void setDefaultUpperBoundX(double defaultUpperBoundX) {
		this.defaultUpperBoundX = defaultUpperBoundX;
	}

	/**
	 * @return the defaultLowerBoundY
	 */
	public final double getDefaultLowerBoundY() {
		return defaultLowerBoundY;
	}

	/**
	 * @param defaultLowerBoundY
	 *            the defaultLowerBoundY to set
	 */
	public final void setDefaultLowerBoundY(double defaultLowerBoundY) {
		this.defaultLowerBoundY = defaultLowerBoundY;
	}

	/**
	 * @return the defaultUpperBoundY
	 */
	public final double getDefaultUpperBoundY() {
		return defaultUpperBoundY;
	}

	/**
	 * @param defaultUpperBoundY
	 *            the defaultUpperBoundY to set
	 */
	public final void setDefaultUpperBoundY(double defaultUpperBoundY) {
		this.defaultUpperBoundY = defaultUpperBoundY;
	}

	/**
	 * The info label shows a short info text that tells the user how to unreset the zoom level.
	 */
	private void addInfoLabel() {
		infoLabel = new Label("Click ESC to reset the zoom level.");
		infoLabel.setId(INFO_LABEL_ID);
		pane.getChildren().add(infoLabel);
		StackPane.setAlignment(infoLabel, Pos.TOP_RIGHT);
		infoLabel.setVisible(false);
	}

	/**
	 * Adds a mechanism to select an area in the chart that should be displayed at larged scale.
	 */
	private void addDragSelectionMechanism() {

		EventHandler<MouseEvent> mousePressedHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				// do nothing for a right-click
				if (event.isSecondaryButtonDown()) {
					return;
				}

				// store position of initial click
				selectionRectangleStart = computeRectanglePoint(event.getX(), event.getY());
			}
		};

		EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				// do nothing for a right-click
				if (event.isSecondaryButtonDown()) {
					return;
				}

				// store current cursor position
				selectionRectangleEnd = computeRectanglePoint(event.getX(), event.getY());

				double x = Math.min(selectionRectangleStart.getX(), selectionRectangleEnd.getX());
				double y = Math.min(selectionRectangleStart.getY(), selectionRectangleEnd.getY());
				double width = Math.abs(selectionRectangleStart.getX() - selectionRectangleEnd.getX());
				double height = Math.abs(selectionRectangleStart.getY() - selectionRectangleEnd.getY());

				drawSelectionRectangle(x, y, width, height);

			}

		};

		EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {
				if (selectionRectangleStart != null && selectionRectangleEnd != null) {
					hideSelectionRectangle();

					// compute new bounds for the chart's x and y axes
					double selectionMinX = Math.min(selectionRectangleStart.getX(), selectionRectangleEnd.getX());
					double selectionMaxX = Math.max(selectionRectangleStart.getX(), selectionRectangleEnd.getX());
					double selectionMinY = Math.min(selectionRectangleStart.getY(), selectionRectangleEnd.getY());
					double selectionMaxY = Math.max(selectionRectangleStart.getY(), selectionRectangleEnd.getY());

					setHorizontalBounds(selectionMinX, selectionMaxX);
					setVerticalBounds(selectionMinY, selectionMaxY);
					showInfo();
				}
				// needed for the key event handler to receive events
				pane.requestFocus();
			}

		};

		EventHandler<KeyEvent> escapeKeyHandler = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				// the ESCAPE key lets the user reset the zoom level
				if (KeyCode.ESCAPE.equals(event.getCode())) {
					resetAxisBounds();
					hideInfo();
				}
			}

		};

		pane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
		pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
		pane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
		pane.addEventHandler(KeyEvent.KEY_RELEASED, escapeKeyHandler);

	}

	private Point2D computeRectanglePoint(double eventX, double eventY) {
		double lowerBoundX = computeOffsetInChart(xAxis, false);
		double upperBoundX = lowerBoundX + xAxis.getWidth();
		double lowerBoundY = computeOffsetInChart(yAxis, true);
		double upperBoundY = lowerBoundY + yAxis.getHeight();
		// make sure the rectangle's end point is in the interval defined by the lower and upper bounds for each
		// dimension
		double x = Math.max(lowerBoundX, Math.min(eventX, upperBoundX));
		double y = Math.max(lowerBoundY, Math.min(eventY, upperBoundY));
		return new Point2D(x, y);
	}

	private void resetAxisBounds() {
		xAxis.setLowerBound(defaultLowerBoundX);
		xAxis.setUpperBound(defaultUpperBoundX);
		yAxis.setLowerBound(defaultLowerBoundY);
		yAxis.setUpperBound(defaultUpperBoundY);
		System.out.println("Axis bounds reset.");
	}

	private void showInfo() {
		infoLabel.setVisible(true);
	}

	private void hideInfo() {
		infoLabel.setVisible(false);
	}

	/**
	 * Sets new bounds for the chart's x axis.
	 * 
	 * @param minPixelPosition
	 *            the x position of the selection rectangle's left edge (in pixels)
	 * @param maxPixelPosition
	 *            the x position of the selection rectangle's right edge (in pixels)
	 */
	private void setHorizontalBounds(double minPixelPosition, double maxPixelPosition) {
		double currentLowerBound = xAxis.getLowerBound();
		double currentUpperBound = xAxis.getUpperBound();
		double offset = computeOffsetInChart(xAxis, false);
		setLowerBoundX(minPixelPosition, currentLowerBound, currentUpperBound, offset);
		setUpperBoundX(maxPixelPosition, currentLowerBound, currentUpperBound, offset);
	}

	/**
	 * Sets new bounds for the chart's y axis.
	 * 
	 * @param minPixelPosition
	 *            the y position of the selection rectangle's upper edge (in pixels)
	 * @param maxPixelPosition
	 *            the y position of the selection rectangle's lower edge (in pixels)
	 */
	private void setVerticalBounds(double minPixelPosition, double maxPixelPosition) {
		double currentLowerBound = yAxis.getLowerBound();
		double currentUpperBound = yAxis.getUpperBound();
		double offset = computeOffsetInChart(yAxis, true);
		setLowerBoundY(maxPixelPosition, currentLowerBound, currentUpperBound, offset);
		setUpperBoundY(minPixelPosition, currentLowerBound, currentUpperBound, offset);
	}

	private void setLowerBoundX(double pixelPosition, double currentLowerBound, double currentUpperBound, double offset) {
		double newLowerBound = computeBound(pixelPosition, offset, xAxis.getWidth(), currentLowerBound,
				currentUpperBound, false);
		xAxis.setLowerBound(newLowerBound);
	}

	private void setUpperBoundX(double pixelPosition, double currentLowerBound, double currentUpperBound, double offset) {
		double newUpperBound = computeBound(pixelPosition, offset, xAxis.getWidth(), currentLowerBound,
				currentUpperBound, false);
		xAxis.setUpperBound(newUpperBound);
	}

	private void setLowerBoundY(double pixelPosition, double currentLowerBound, double currentUpperBound, double offset) {
		double newLowerBound = computeBound(pixelPosition, offset, yAxis.getHeight(), currentLowerBound,
				currentUpperBound, true);
		yAxis.setLowerBound(newLowerBound);
	}

	private void setUpperBoundY(double pixelPosition, double currentLowerBound, double currentUpperBound, double offset) {
		double newUpperBound = computeBound(pixelPosition, offset, yAxis.getHeight(), currentLowerBound,
				currentUpperBound, true);
		yAxis.setUpperBound(newUpperBound);
	}

	/**
	 * Computes the pixel offset of the given node inside the chart node.
	 * 
	 * @param node
	 *            the node for which to compute the pixel offset
	 * @param vertical
	 *            flag that indicates whether the horizontal or the vertical dimension should be taken into account
	 * @return the offset inside the chart node
	 */
	private double computeOffsetInChart(Node node, boolean vertical) {
		double offset = 0;
		do {
			if (vertical) {
				offset += node.getLayoutY();
			} else {
				offset += node.getLayoutX();
			}
			node = node.getParent();
		} while (node != chart);
		return offset;
	}

	private double computeBound(double pixelPosition, double pixelOffset, double pixelLength, double lowerBound,
			double upperBound, boolean axisInverted) {
		double pixelPositionWithoutOffset = pixelPosition - pixelOffset;
		double relativePosition = pixelPositionWithoutOffset / pixelLength;
		double axisLength = upperBound - lowerBound;

		// The screen's y axis grows from top to bottom, whereas the chart's y axis goes from bottom to top. That's
		// why we need to have this distinction here.
		double offset = 0;
		int sign = 0;
		if (axisInverted) {
			offset = upperBound;
			sign = -1;
		} else {
			offset = lowerBound;
			sign = 1;
		}

		double newBound = offset + sign * relativePosition * axisLength;
		return newBound;
	}

	/**
	 * Draws a selection box in the view.
	 * 
	 * @param x
	 *            the x position of the selection box
	 * @param y
	 *            the y position of the selection box
	 * @param width
	 *            the width of the selection box
	 * @param height
	 *            the height of the selection box
	 */
	private void drawSelectionRectangle(final double x, final double y, final double width, final double height) {

		selectionRectangle.setVisible(true);
		selectionRectangle.setX(x);
		selectionRectangle.setY(y);
		selectionRectangle.setWidth(width);
		selectionRectangle.setHeight(height);
	}

	/**
	 * Hides the selection box.
	 */
	private void hideSelectionRectangle() {
		selectionRectangle.setVisible(false);
	}

}
