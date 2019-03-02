package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import main.Consts;
import processing.javafx.PSurfaceFX;

public class App extends Application {
	
    public static PSurfaceFX surface;

	@Override
	public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PFLAP.fxml"));
        
        Scene scene = new Scene(loader.load(), Consts.WIDTH, Consts.HEIGHT);
        scene.getStylesheets().add("javafx/stylesheet.css");
        
        primaryStage.setTitle(Consts.title);
        primaryStage.getIcons().add(new Image("data/icon_small.png"));
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(300);
        primaryStage.show();

        Controller.stage = primaryStage;
        surface.stage = primaryStage;
	}
}