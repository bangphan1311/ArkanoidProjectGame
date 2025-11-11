package GameManager.Menu;

import GameManager.BaseGameController;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import GameManager.SoundManager;
import java.io.IOException;
import java.net.URL;

public class MapController {
    public static MediaPlayer gameMusicPlayer;

    @FXML
    private Button level1Button, level2Button, level3Button, level4Button, level5Button, level6Button, menuButton;

    @FXML
    private void handleLevel1() {
        loadLevel("/RenderView/Level/Level1.fxml", "/Images/MapLevel/level1.png", "/Images/Entity/paddle.png", "Level 1");
    }

    @FXML
    private void handleLevel2() {
        loadLevel("/RenderView/Level/Level2.fxml", "/Images/MapLevel/level2.png", "/Images/Entity/paddle.png", "Level 2");
    }

    @FXML
    private void handleLevel3() {
        loadLevel("/RenderView/Level/Level3.fxml", "/Images/MapLevel/nenlv3.png", "/Images/Entity/paddle.png", "Level 3");
    }

    @FXML
    private void handleLevel4() {
        loadLevel("/RenderView/Level/Level4.fxml", "/Images/MapLevel/level4.jpg", "/Images/Entity/paddle.png", "Level 4");
    }

    @FXML
    private void handleLevel5() {
        loadLevel("/RenderView/Level/Level5.fxml", "/Images/MapLevel/level5.png", "/Images/Entity/paddle.png", "Level 5");
    }

    @FXML
    private void handleLevel6() {
        loadLevel("/RenderView/Level/Level6.fxml", "/Images/MapLevel/level6.png", "/Images/Entity/paddle.png", "Level 6");
    }

    // nút Menu
    @FXML
    private void handleMenu() {
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RenderView/Menu/Menu.fxml"));
            Stage stage = (Stage) menuButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Menu");
            stage.show();
            stage.centerOnScreen();
            root.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        startGameMusic();
    }

    // hàm load level
    private void loadLevel(String fxmlFile, String bgPath, String paddlePath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            BaseGameController controller = loader.getController();
            controller.setupLevel(bgPath, paddlePath);

            Stage stage = (Stage) level1Button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Arkanoid - " + title);
            stage.show();

            stage.centerOnScreen();
            root.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // hiệu ứng
    @FXML
    private void onHover(MouseEvent event) {
        Node node = (Node) event.getSource();

        // Phóng to
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.play();

        // Rung nhẹ
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), node);
        shake.setByX(5);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.play();

        node.setEffect(new Glow(0.6));
    }

    // âm thah
    @FXML
    private void onExit(MouseEvent event) {
        Node node = (Node) event.getSource();
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
        node.setEffect(null);
    }
    private void startGameMusic() {
        if (gameMusicPlayer == null && !SoundManager.isMusicMuted) {
            try {
                URL resource = getClass().getResource("/sounds/startgame.mp3");
                if (resource != null) {
                    Media media = new Media(resource.toString());
                    gameMusicPlayer = new MediaPlayer(media);
                    gameMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    gameMusicPlayer.setVolume(0.5);
                    gameMusicPlayer.play();
                } else {
                    System.err.println("Không tìm thấy file /sounds/game_music.mp3");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (gameMusicPlayer != null && gameMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING && !SoundManager.isMusicMuted) {
            gameMusicPlayer.play();
        }
    }
}
