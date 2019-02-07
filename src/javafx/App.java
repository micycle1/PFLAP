package javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        Parent root = loader.load();
        Controller.stage = primaryStage;
        Scene scene = new Scene(root, Consts.WIDTH, Consts.HEIGHT);

        primaryStage.setTitle(Consts.title);
        primaryStage.getIcons().add(new Image("data/icon_small.png"));
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.show();

        surface.stage = primaryStage;
	}

}
