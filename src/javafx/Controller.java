package javafx;

import static main.Consts.notificationData.noInitialState;
import static main.PFLAP.p;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import main.Consts;
import main.HistoryHandler;
import main.HistoryList;
import main.PFLAP;
import main.PFLAP.PApplet;
import main.Step;

import commands.Batch;
import model.Model;
import p5.Notification;
import processing.javafx.PSurfaceFX;

public class Controller implements Initializable {

	public static PSurfaceFX surface;
	protected static Stage stage;
	private static final ExtensionFilter dat, png;

	static {
		dat = new FileChooser.ExtensionFilter("Machine files (*.dat)", "*.dat");
		png = new FileChooser.ExtensionFilter("Image File", "*.png");
	}

	@FXML
	StackPane pflap;
	@FXML
	MenuItem undo, redo;
	@FXML
	MenuItem machine_DFA, machine_DPA, machine_mealy, machine_moore;
	@FXML
	ColorPicker colourPicker_state, colourPicker_stateSelected, colourPicker_transition, colourPicker_background;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PApplet.controller = this;
		Canvas canvas = (Canvas) surface.getNative();
		surface.fx.context = canvas.getGraphicsContext2D();
		pflap.getChildren().add(canvas);
		canvas.widthProperty().bind(pflap.widthProperty());
		canvas.heightProperty().bind(pflap.heightProperty());

		undo.setDisable(true);
		redo.setDisable(true);
		machine_DFA.setDisable(true);
		
//		colourPicker_state = new ColorPicker(PFLAP.stateColour);
		
		
//		colourPicker_state.setOnAction((ActionEvent e) -> {
//			PFLAP.stateColour = colourPicker_state.getValue();
//		});
//		colourPicker_state.setValue(PFLAP.stateColour);

//		colourPicker_stateSelected.setValue(PFLAP.stateSelectedColour);
//		colourPicker_transition.setValue(PFLAP.transitionColour);
//		colourPicker_background.setValue(PFLAP.bgColour);
	}

	public void setUndoEnable(boolean b) {
		undo.setDisable(!b);
	}

	public void setRedoEnable(boolean b) {
		redo.setDisable(!b);
	}

	public void stepModeHelp() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION, Consts.helpStep, ButtonType.OK);
				alert.initOwner(stage);
				alert.setTitle("Help: Step Mode");
				alert.setHeaderText("Help: Step Mode");
				alert.showAndWait();
			}
		});
	}

	/*
	 * File Menu
	 */

	@FXML
	private void open() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Machine File");
		fileChooser.getExtensionFilters().add(dat);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File f = fileChooser.showOpenDialog(stage);
		if (f != null) {
			HistoryHandler.loadHistory(f.getAbsolutePath());
		}
	}

	@FXML
	private void save() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Machine File");
		fileChooser.getExtensionFilters().add(dat);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File f = fileChooser.showSaveDialog(stage);
		if (f != null) {
			HistoryHandler.saveHistory(f.getAbsolutePath());
		}
	}

	@FXML
	private void close() {
		p.exit();
	}

	/*
	 * Edit Menu
	 */

	@FXML
	private void undo() {
		HistoryHandler.undo();
		redo.setDisable(false);
		if (HistoryHandler.getHistoryStateIndex() < -1) {
			undo.setDisable(true);
		}
	}

	@FXML
	private void redo() {
		HistoryHandler.redo();
		undo.setDisable(false);
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

	/*
	 * View Menu
	 */

	@FXML
	private void resetZoom() {
		PFLAP.PApplet.setZoom(1);
	}

	@FXML
	private void saveAsImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Stage as Image");
		fileChooser.getExtensionFilters().add(png);
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			String directory = file.getAbsoluteFile().toString();
			p.saveFrame(directory);
			Notification.addNotification("Screenshot", "Image saved to " + directory);
		}
	}

	@FXML
	private void machineInformation() {
		String info = "Transitions: " + Model.nTransitions() + "\r\n" + "States: " + Model.nStates() + "\r\n" + "Type: "
				+ PFLAP.mode;
		Alert alert = new Alert(AlertType.INFORMATION, info, ButtonType.OK);
		alert.initOwner(stage);
		alert.setTitle("Machine Information");
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	@FXML
	/**
	 * Toggle history-viewer.
	 */
	private void history() {
		HistoryList.toggleVisible();
	}

	/*
	 * Machine Menu
	 */

	// todo

	/*
	 * Input Menu
	 */

	@FXML
	private void step() { // todo
		if (Model.initialState != -1) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Step By State Input");
			dialog.setHeaderText(null);
			dialog.setContentText("Machine Input:");
			dialog.initOwner(stage);

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
		} else {
			Notification.addNotification(noInitialState);
		}
	}

	@FXML
	private void fastRun() { // todo
		if (Model.initialState != -1) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("Fast-Run Input");
			dialog.setHeaderText(null);
			dialog.setContentText("Machine Input:");
			dialog.initOwner(stage);

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (result.get().contains(" ")) {
					Notification.addNotification("Invalid Input", "Input cannot contain ' ' characters.");
				}
				switch (PFLAP.mode) {
					case DFA :
						Model.runMachine(result.get());
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
		} else {
			Notification.addNotification(noInitialState);
		}
	}

	/*
	 * Help Menu
	 */

	@FXML
	private void help() {
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
}
