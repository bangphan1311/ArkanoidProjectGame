package GameManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LevelCompleteController {

    @FXML
    private void handleNextLevel(ActionEvent event) {
        try {
            System.out.println("LevelComplete: loading Level2.fxml ...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Level2.fxml"));
            Pane nextRoot = loader.load();
            System.out.println("LevelComplete: FXML loaded: " + nextRoot);
            GameController level2Controller = loader.getController();
            System.out.println("LevelComplete: controller = " + level2Controller);

            if (level2Controller == null) {
                System.err.println("LevelComplete: ERROR - level2Controller is null. Check fx:controller in Level2.fxml.");
            } else {
                // cấu hình resources cho level 2
                level2Controller.configureGraphics(
                        "/Images/level2.png",   // background mới cho level 2
                        "/Images/level2.png" // paddle image mới cho level 2
                );
                level2Controller.setNextLevel(null);
            }

            // thay scene
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(nextRoot);
            stage.setScene(newScene);
            stage.show();
            if (level2Controller != null) {
                Platform.runLater(() -> {
                    try {
                        level2Controller.applyGraphics();
                        System.out.println("sai");
                        nextRoot.requestFocus();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}