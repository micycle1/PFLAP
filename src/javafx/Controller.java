package javafx;

import static main.PFLAP.p;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import commands.Batch;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Consts;
import main.HistoryHandler;
import main.PFLAP;
import main.PFLAP.PApplet;
import main.Step;
import model.Model;
import p5.Notification;
import processing.javafx.PSurfaceFX;

public class Controller implements Initializable {

	public static PSurfaceFX surface;
	protected static Stage stage;
	private static ExtensionFilter f;

	static {
		f = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.dat"); // todo
	}

	@FXML
	StackPane pflap;
	@FXML
	MenuItem undo, redo;
	@FXML
	MenuItem machine_DFA, machine_DPA, machine_mealy, machine_moore;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Canvas canvas = (Canvas) surface.getNative();
		surface.fx.context = canvas.getGraphicsContext2D();
		pflap.getChildren().add(canvas);
		canvas.widthProperty().bind(pflap.widthProperty());
		canvas.heightProperty().bind(pflap.heightProperty());

		undo.setDisable(true);
		redo.setDisable(true);
		machine_DFA.setDisable(true);
	}

	@FXML
	private void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Machine File");
		fileChooser.showOpenDialog(stage);
		fileChooser.getExtensionFilters().add(f);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		// HistoryHandler.loadHistory(path); todo
	}

	@FXML
	private void save() {
		// HistoryHandler.save
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(f);
		fileChooser.setTitle("Save Image");
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
		}
		// HistoryHandler.saveHistory(path); todo
	}

	@FXML
	private void close() {
		p.exit();
	}

	@FXML
	private void reset() {
		PFLAP.reset();
	}

	@FXML
	private void selectAllStates() {
		PApplet.view.selectAllStates();
	}

	@FXML
	private void deleteAllStates() {
		HistoryHandler.buffer(new Batch(Batch.createDeleteBatch(Model.getStates())));
	}

	@FXML
	private void invertSelection() {
		PApplet.view.invertSelectedStates();
	}

	@FXML
	private void resetZoom() {
		PFLAP.PApplet.setZoom(1);
	}
	
	@FXML
	private void saveAsImage() {
//		FileDialog saveImage = new FileDialog(f, "Save", FileDialog.SAVE);
//		saveImage.setDirectory(Consts.directory);
//		saveImage.setTitle("Save Frame");
//		saveImage.setFile("*.png");
//		saveImage.setVisible(true);
//		if (saveImage.getFile() != null) {
//			String directory = saveImage.getDirectory() + saveImage.getFile();
//			if (!directory.toLowerCase().endsWith(".png")) {
//				directory += ".png";
//			}
//			p.saveFrame(directory);
//			Notification.addNotification("Screenshot", "Image saved to " + directory);
//		}
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			String directory = file.getAbsoluteFile().toString();
			if (!directory.toLowerCase().endsWith(".png")) {
				directory += ".png";
			}
			p.saveFrame(directory);
			Notification.addNotification("Screenshot", "Image saved to " + directory);
		}
	}
	
	@FXML
	private void help() {
		p.print("asdas");
		Alert alert = new Alert(AlertType.INFORMATION, Consts.helpPFLAP, ButtonType.OK);
		alert.initOwner(stage);
		alert.setTitle("Help");
		alert.setHeaderText("Help");
		alert.setGraphic(new ImageView("data/icon.png"));
		alert.showAndWait();
	}

	@FXML
	private void about() {
		Alert alert = new Alert(AlertType.INFORMATION, Consts.about, ButtonType.OK);
		alert.initOwner(stage);
		alert.setTitle("About PFLAP");
		alert.setHeaderText(Consts.title);
		alert.setGraphic(new ImageView("data/icon.png"));
		alert.showAndWait();
	}

	@FXML
	private void step() { // todo
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Step By State Input");
		dialog.setHeaderText("Look, a Text Input Dialog");
		dialog.setContentText("Machine Input: ");
		dialog.initStyle(StageStyle.UTILITY);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			switch (PFLAP.mode) {
				case DPA :
					break;
				case DFA :
				case MEALY :
				case MOORE :
					Step.beginStep(result.get());
				default :
					break;
			}
		}

	}

	@FXML
	private void fastRun() { // todo
		switch (PFLAP.mode) {
			case DFA :
				Model.runMachine("");
				break;
			case DPA :
				break;
			case MEALY :
				break;
			case MOORE :
				break;
			default :
				break;

		}
	}

}
