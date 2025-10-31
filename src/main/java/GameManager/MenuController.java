package GameManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuController {

    @FXML
    private Button startButton, instructionsButton, settingsButton, highScoresButton, exitButton;

    // Start Game
    @FXML
    void handleStartGame(ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/Game.fxml");
            if (url == null)
                url = getClass().getResource("../../../RenderView/Game.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ARKANOID - Play");
            stage.show();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            showError("Lỗi khi load Game.fxml:\n" + ioe.getMessage());
        }
    }

    // Instructions
    @FXML
    void handleInstructions(ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/Instructions.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Instructions");
            stage.show();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // High Scores
    @FXML
    void handleHighScores(ActionEvent event) {
        System.out.println("High Scores clicked!");
    }

    // Settings
    @FXML
    void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Settings.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Settings");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Exit
    @FXML
    void handleExit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Exit.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Exit Game?");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể load Exit.fxml\n" + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}
