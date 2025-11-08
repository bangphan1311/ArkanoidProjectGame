package GameManager.Menu;

import GameManager.BaseGameController;
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

    @FXML
    private Button startButton, instructionsButton, settingsButton, highScoresButton, exitButton;
    private static final int LEVEL_TO_RUN = 5;

    @FXML
    void handleStartGame(ActionEvent event) {
        boolean useLevelToRun = false;
        if (useLevelToRun) {
            String fxmlFile;
            String bgPath;
            String paddlePath;
            switch (LEVEL_TO_RUN) {
                case 1:
                    fxmlFile = "/RenderView/Level/Level1.fxml";
                    bgPath = "/Images/MapLevel/level1.png";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
                case 2:
                    fxmlFile = "/RenderView/Level/Level2.fxml";
                    bgPath = "/Images/MapLevel/level2.png";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
                case 3:
                    fxmlFile = "/RenderView/Level/Level3.fxml";
                    bgPath = "/Images/MapLevel/nenlv3.png";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
                case 4:
                    fxmlFile = "/RenderView/Level/Level4.fxml";
                    bgPath = "/Images/MapLevel/level4.jpg";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
                case 5:
                    fxmlFile = "/RenderView/Level/Level5.fxml";
                    bgPath = "/Images/MapLevel/level5.png";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
                case 6:
                    fxmlFile = "/RenderView/Level/Level6.fxml";
                    bgPath = "/Images/MapLevel/level6.png";
                    paddlePath = "/Images/Entity/paddle.png";
                    break;
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

        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Map.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Select Level");
                stage.show();

                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);


            } catch (IOException ioe) {
                ioe.printStackTrace();
                showError("Lỗi khi load Map.fxml:\n" + ioe.getMessage());
            }
        }
    }

    // ========== INSTRUCTIONS ==========
    @FXML
    void handleInstructions(ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/Menu/Instructions.fxml");
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

    // ========== HIGH SCORES ==========
    @FXML
    void handleHighScores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/HighScores.fxml"));
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

    // ========== SETTINGS ==========
    @FXML
    void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Settings.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Settings");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== EXIT ==========
    @FXML
    void handleExit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Exit.fxml"));
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

    // ========== HOVER EFFECT ==========
    @FXML
    public void initialize() {
        addHoverAnimation(startButton);
        addHoverAnimation(instructionsButton);
        addHoverAnimation(settingsButton);
        addHoverAnimation(highScoresButton);
        addHoverAnimation(exitButton);
    }

    private void addHoverAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            shakeButton(button);
            button.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
        });

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
