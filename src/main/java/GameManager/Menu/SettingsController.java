package GameManager.Menu;

import GameManager.SoundManager;
import GameManager.Menu.MenuController;
import GameManager.Menu.MapController;
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
import javafx.scene.media.MediaPlayer;

public class SettingsController {

    @FXML private ToggleButton musicToggle;
    @FXML private ToggleButton soundToggle;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        musicToggle.setSelected(!SoundManager.isMusicMuted);
        soundToggle.setSelected(!SoundManager.isSoundMuted);
        updateToggleStyle(musicToggle, !SoundManager.isMusicMuted);
        updateToggleStyle(soundToggle, !SoundManager.isSoundMuted);

        musicToggle.setOnAction(e -> handleMusicToggle());
        soundToggle.setOnAction(e -> handleSoundToggle());

        addHoverEffect(musicToggle);
        addHoverEffect(soundToggle);
        addHoverEffect(backButton);
    }

    private void handleMusicToggle() {
        boolean isSelected = musicToggle.isSelected();
        SoundManager.isMusicMuted = !isSelected;
        updateToggleStyle(musicToggle, isSelected);
        if (isSelected) {
            if (MenuController.menuMusicPlayer != null &&
                    MenuController.menuMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                MenuController.menuMusicPlayer.play();
            }
            if (MapController.gameMusicPlayer != null &&
                    MapController.gameMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                MapController.gameMusicPlayer.play();
            }
        }else {
            if (MenuController.menuMusicPlayer != null) {
                MenuController.menuMusicPlayer.stop();
            }
            if (MapController.gameMusicPlayer != null) {
                MapController.gameMusicPlayer.stop();
            }
        }

    }

    private void handleSoundToggle() {
        boolean isSelected = soundToggle.isSelected();
        SoundManager.isSoundMuted = !isSelected;
        updateToggleStyle(soundToggle, isSelected);
    }

    private void updateToggleStyle(ToggleButton toggle, boolean isSelected) {
        if (isSelected) {
            toggle.setText("ON");
            toggle.setStyle(
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
            toggle.setText("OFF");
            toggle.setStyle(
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
    }


    // back to menu
    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        if (!SoundManager.isMusicMuted && MenuController.menuMusicPlayer != null) {
            MenuController.menuMusicPlayer.play();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Menu.fxml"));
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

