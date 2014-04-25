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
		pane.addEventHandler(MouseEvent.MOUSE_PRESSED, new MousePressedHandler());
		pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new MouseDraggedHandler());
		pane.addEventHandler(MouseEvent.MOUSE_RELEASED, new MouseReleasedHandler());
		pane.addEventHandler(KeyEvent.KEY_RELEASED, new EscapeKeyHandler());
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

	/**
	 *
	 */
	private final class MousePressedHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(final MouseEvent event) {

			// do nothing for a right-click
			if (event.isSecondaryButtonDown()) {
				return;
			}

			// store position of initial click
			selectionRectangleStart = computeRectanglePoint(event.getX(), event.getY());
			event.consume();
		}
	}

	/**
	 *
	 */
	private final class MouseDraggedHandler implements EventHandler<MouseEvent> {
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
			event.consume();
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
	}

	/**
	 *
	 */
	private final class MouseReleasedHandler implements EventHandler<MouseEvent> {

		/**
		 * Defines a minimum width for the selected area. If the selected rectangle is not wider than this value, no
		 * zooming will take place. This helps prevent accidental zooming.
		 */
		private static final double MIN_RECTANGE_WIDTH = 10;

		/**
		 * Defines a minimum height for the selected area. If the selected rectangle is not wider than this value, no
		 * zooming will take place. This helps prevent accidental zooming.
		 */
		private static final double MIN_RECTANGLE_HEIGHT = 10;

		@Override
		public void handle(final MouseEvent event) {
			hideSelectionRectangle();

			if (selectionRectangleStart == null || selectionRectangleEnd == null) {
				return;
			}

			if (isRectangleSizeTooSmall()) {
				return;
			}

			setAxisBounds();
			showInfo();
			selectionRectangleStart = null;
			selectionRectangleEnd = null;

			// needed for the key event handler to receive events
			pane.requestFocus();
			event.consume();
		}

		private boolean isRectangleSizeTooSmall() {
			double width = Math.abs(selectionRectangleEnd.getX() - selectionRectangleStart.getX());
			double height = Math.abs(selectionRectangleEnd.getY() - selectionRectangleStart.getY());
			return width < MIN_RECTANGE_WIDTH || height < MIN_RECTANGLE_HEIGHT;
		}

		/**
		 * Hides the selection rectangle.
		 */
		private void hideSelectionRectangle() {
			selectionRectangle.setVisible(false);
		}

		private void setAxisBounds() {
			disableAutoRanging();

			// compute new bounds for the chart's x and y axes
			double selectionMinX = Math.min(selectionRectangleStart.getX(), selectionRectangleEnd.getX());
			double selectionMaxX = Math.max(selectionRectangleStart.getX(), selectionRectangleEnd.getX());
			double selectionMinY = Math.min(selectionRectangleStart.getY(), selectionRectangleEnd.getY());
			double selectionMaxY = Math.max(selectionRectangleStart.getY(), selectionRectangleEnd.getY());

			setHorizontalBounds(selectionMinX, selectionMaxX);
			setVerticalBounds(selectionMinY, selectionMaxY);
		}

		private void disableAutoRanging() {
			xAxis.setAutoRanging(false);
			yAxis.setAutoRanging(false);
		}

		private void showInfo() {
			infoLabel.setVisible(true);
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

		private void setLowerBoundX(double pixelPosition, double currentLowerBound, double currentUpperBound,
				double offset) {
			double newLowerBound = computeBound(pixelPosition, offset, xAxis.getWidth(), currentLowerBound,
					currentUpperBound, false);
			xAxis.setLowerBound(newLowerBound);
		}

		private void setUpperBoundX(double pixelPosition, double currentLowerBound, double currentUpperBound,
				double offset) {
			double newUpperBound = computeBound(pixelPosition, offset, xAxis.getWidth(), currentLowerBound,
					currentUpperBound, false);
			xAxis.setUpperBound(newUpperBound);
		}

		private void setLowerBoundY(double pixelPosition, double currentLowerBound, double currentUpperBound,
				double offset) {
			double newLowerBound = computeBound(pixelPosition, offset, yAxis.getHeight(), currentLowerBound,
					currentUpperBound, true);
			yAxis.setLowerBound(newLowerBound);
		}

		private void setUpperBoundY(double pixelPosition, double currentLowerBound, double currentUpperBound,
				double offset) {
			double newUpperBound = computeBound(pixelPosition, offset, yAxis.getHeight(), currentLowerBound,
					currentUpperBound, true);
			yAxis.setUpperBound(newUpperBound);
		}

		private double computeBound(double pixelPosition, double pixelOffset, double pixelLength, double lowerBound,
				double upperBound, boolean axisInverted) {
			double pixelPositionWithoutOffset = pixelPosition - pixelOffset;
			double relativePosition = pixelPositionWithoutOffset / pixelLength;
			double axisLength = upperBound - lowerBound;

			// The screen's y axis grows from top to bottom, whereas the chart's y axis goes from bottom to top.
			// That's
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
	}

	/**
	 *
	 */
	private final class EscapeKeyHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {

			// the ESCAPE key lets the user reset the zoom level
			if (KeyCode.ESCAPE.equals(event.getCode())) {
				resetAxisBounds();
				hideInfo();
			}
		}

		private void resetAxisBounds() {
			xAxis.setAutoRanging(true);
			yAxis.setAutoRanging(true);
		}

		private void hideInfo() {
			infoLabel.setVisible(false);
		}
	}

}
