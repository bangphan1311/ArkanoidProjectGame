package GameManager.Menu;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import java.io.IOException;


public class SettingsController {

    @FXML
    private ToggleButton musicToggle;

    @FXML
    private ToggleButton soundToggle;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Hiệu ứng hover cho nút
        addHoverEffect(musicToggle);
        addHoverEffect(soundToggle);
        addHoverEffect(backButton);

        // music
        musicToggle.setOnAction(e -> {
            if (musicToggle.isSelected()) {
                musicToggle.setText("ON");
                musicToggle.setStyle(
                        "-fx-background-color: #4CAF50;" +  // xah
                                "-fx-text-fill: #0b3d02;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 25;" +
                                "-fx-pref-width: 120;" +
                                "-fx-pref-height: 40;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                );
            } else {
                musicToggle.setText("OFF");
                musicToggle.setStyle(
                        "-fx-background-color: #F44336;" +  // nền đỏ
                                "-fx-text-fill: white;" +           // chữ trắng
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 25;" +
                                "-fx-pref-width: 120;" +
                                "-fx-pref-height: 40;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                );
            }
        });

        // sound
        soundToggle.setOnAction(e -> {
                    if (soundToggle.isSelected()) {
                        soundToggle.setText("ON");
                        soundToggle.setStyle(
                                "-fx-background-color: #4CAF50;" +
                                        "-fx-text-fill: #0b3d02;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-background-radius: 25;" +
                                        "-fx-pref-width: 120;" +
                                        "-fx-pref-height: 40;" +
                                        "-fx-cursor: hand;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                        );
                    } else {
                        soundToggle.setText("OFF");
                        soundToggle.setStyle(
                                "-fx-background-color: #F44336;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-background-radius: 25;" +
                                        "-fx-pref-width: 120;" +
                                        "-fx-pref-height: 40;" +
                                        "-fx-cursor: hand;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                        );
                    }
        });
    }

    // back to menu
    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    // hiệu ứng
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            // Hiệu ứng sáng nhẹ
            button.setStyle(button.getStyle() +
                    "-fx-effect: dropshadow(three-pass-box, yellow, 15, 0.6, 0, 0);");

            // phóng to
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), button);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();

            // rug
            TranslateTransition shake = new TranslateTransition(Duration.millis(80), button);
            shake.setFromX(0);
            shake.setByX(4);
            shake.setCycleCount(4);
            shake.setAutoReverse(true);
            shake.play();
        });

        button.setOnMouseExited(e -> {
            // trở về bthg
            button.setStyle(button.getStyle().replace("-fx-effect: dropshadow(three-pass-box, yellow, 15, 0.6, 0, 0);", ""));
            button.setScaleX(1);
            button.setScaleY(1);
            button.setTranslateX(0);
        });
    }

    private void addHoverEffect(ToggleButton toggle) {
        toggle.setOnMouseEntered(e -> {
            toggle.setStyle(toggle.getStyle() +
                    "-fx-effect: dropshadow(three-pass-box, yellow, 15, 0.6, 0, 0);");

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), toggle);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();

            TranslateTransition shake = new TranslateTransition(Duration.millis(80), toggle);
            shake.setFromX(0);
            shake.setByX(4);
            shake.setCycleCount(4);
            shake.setAutoReverse(true);
            shake.play();
        });

        toggle.setOnMouseExited(e -> {
            toggle.setStyle(toggle.getStyle().replace("-fx-effect: dropshadow(three-pass-box, yellow, 15, 0.6, 0, 0);", ""));
            toggle.setScaleX(1);
            toggle.setScaleY(1);
            toggle.setTranslateX(0);
        });
    }
}


