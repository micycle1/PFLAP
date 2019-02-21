package javafx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
        
//        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.SHIFT, KeyCombination.CONTROL_DOWN);
//            public void handle(KeyEvent ke) {
//                if (keyComb.match(ke)) {
//                    System.out.println("Key Pressed: " + keyComb);
//                    ke.consume(); // <-- stops passing the event to next node
//                }
//            }
//        });

        primaryStage.setTitle(Consts.title);
        primaryStage.getIcons().add(new Image("data/icon_small.png"));
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.show();

        surface.stage = primaryStage;
	}

}
