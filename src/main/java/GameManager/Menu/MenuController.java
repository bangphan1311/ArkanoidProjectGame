package GameManager.Menu;
import GameManager.BaseGameController;

import GameManager.*;
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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class MenuController {
    private static final int LEVEL_TO_RUN = 3;

    @FXML
    private Button startButton, instructionsButton, settingsButton, highScoresButton, exitButton;

    // Start Game

    @FXML
    void handleStartGame(ActionEvent event) {
        String fxmlFile;
        String bgPath;
        String paddlePath;
        switch (LEVEL_TO_RUN) {
            case 1:
                fxmlFile = "/RenderView/Game.fxml"; // File FXML của Level 1
                bgPath = "/Images/level1.png";
                paddlePath = "/Images/paddle.png";
                break;
            case 2:
                fxmlFile = "/RenderView/Level2.fxml"; // File FXML của Level 2
                bgPath = "/Images/level2.png";
                paddlePath = "/Images/paddle.png";
                break;
            case 3:
                fxmlFile = "/RenderView/Level3.fxml"; // (Đảm bảo bạn đã tạo file này)
                bgPath = "/Images/background_level3.png"; // (Tên ảnh nền mới của bạn)
                paddlePath = "/Images/paddle.png";       // (Tạm dùng paddle cũ)
                break;
            // ⭐⭐⭐⭐⭐⭐⭐⭐⭐

            default:
                showError("Level " + LEVEL_TO_RUN + " không tồn tại!");
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            BaseGameController controller = loader.getController();

            controller.setupLevel(bgPath, paddlePath);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Arkanoid - Level " + LEVEL_TO_RUN);
            stage.show();
            root.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi khi tải level FXML: " + fxmlFile + "\n" + e.getMessage());
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/HighScores.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("High Scores");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể load HighScores.fxml\n" + e.getMessage());
        }
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

    // hiệu ứng

    @FXML
    public void initialize() {
        addHoverAnimation(startButton);
        addHoverAnimation(instructionsButton);
        addHoverAnimation(settingsButton);
        addHoverAnimation(highScoresButton);
        addHoverAnimation(exitButton);
    }

    private void addHoverAnimation(Button button) {
        // di chuột vào
        button.setOnMouseEntered(e -> {
            shakeButton(button);
            button.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
        });

        // di chuột ra
        button.setOnMouseExited(e -> {
            button.setTranslateX(0);
            button.setStyle("");
        });
    }


    private void shakeButton(Button button) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(button.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(button.translateXProperty(), -5)),
                new KeyFrame(Duration.millis(100), new KeyValue(button.translateXProperty(), 5)),
                new KeyFrame(Duration.millis(150), new KeyValue(button.translateXProperty(), -5)),
                new KeyFrame(Duration.millis(200), new KeyValue(button.translateXProperty(), 5)),
                new KeyFrame(Duration.millis(250), new KeyValue(button.translateXProperty(), 0))
        );
        timeline.play();
    }
}