package de.tesis.dynaware.javafx.fancychart;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import de.tesis.dynaware.javafx.fancychart.data.DataItemDao.FileFormat;

public class ImportExportPanelController {

	@FXML
	ChoiceBox<FileFormat> formatChoiceBox;
	@FXML
	HBox buttonContainer;
	@FXML
	Button importButton;
	@FXML
	Button exportButton;
	@FXML
	StackPane rootPane;

	public void initialize() {
		formatChoiceBox.setItems(FXCollections.observableArrayList(FileFormat.values()));
		formatChoiceBox.getSelectionModel().select(0);
	}

	public Button getImportButton() {
		return importButton;
	}

	public Button getExportButton() {
		return exportButton;
	}

	public FileFormat getSelectedFileFormat() {
		return formatChoiceBox.getValue();
	}

}
