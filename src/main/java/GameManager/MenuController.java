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

    // Start game
    @FXML
    void handleStartGame(ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/Game.fxml");
            if (url == null)
                url = getClass().getResource("../../../RenderView/Game.fxml");

            System.out.println("🔹 Resource path = " + url);

            if (url == null) {
                showError(" Không tìm thấy file Game.fxml!\n");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ARKANOID - Play");
            stage.show();
            System.out.println("Game.fxml loaded successfully!");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            showError("Lỗi khi load Game.fxml:\n" + ioe.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi không xác định:\n" + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    // Instructions
    @FXML
    void handleInstructions(ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/Instructions.fxml");
            if (url == null)
                url = getClass().getResource("../../../RenderView/Instructions.fxml");

            if (url == null) {
                showError("Không tìm thấy file Instructions.fxml!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ARKANOID - Instructions");
            stage.show();

            System.out.println("Instructions.fxml loaded successfully!");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            showError("Lỗi khi load Instructions.fxml:\n" + ioe.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi không xác định:\n" + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    @FXML
    void handleHighScores(ActionEvent event) {
        System.out.println("High Scores clicked!");
    }

    @FXML
    void handleSettings(ActionEvent event) {
        System.out.println("Settings clicked!");
    }

    // Exit
    @FXML
    void handleExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("...");
        alert.setContentText("..");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        }
    }

    // hthi lỗi
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}
