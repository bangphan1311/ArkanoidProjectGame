package GameManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {
    @FXML
    private Button startButton, instructionsButton, settingsButton, highScoresButton, exitButton;

    @FXML
    void handleStartGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Game.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void handleInstructions(ActionEvent event) {
        System.out.println("Instructions clicked!");
    }

    @FXML
    void handleSettings(ActionEvent event) {
        System.out.println("Settings clicked!");
    }

    @FXML
    void handleHighScores(ActionEvent event) {
        System.out.println("High Scores clicked!");
    }

    @FXML
    void handleExit(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}