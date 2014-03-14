/*
 * Copyright (C) 2005 - 2013 by TESIS DYNAware GmbH
 */
package de.tesis.dynaware.javafx.fancychart;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FancyChartViewer extends Application {

	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("A Fancy Chart Control in JavaFX");

		final URL location = FancyChartController.class.getResource("FancyChart.fxml");
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(location);
		final Parent root = (Parent) loader.load(location.openStream());

		final Scene scene = new Scene(root, 1400, 800);

		final String fancyChartCss = "css/fancychart.css";

		scene.getStylesheets().addAll(fancyChartCss);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
