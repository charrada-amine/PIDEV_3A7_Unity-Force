package tn.esprit.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class SceneManager {
    public static void loadView(VBox container, String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            container.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}