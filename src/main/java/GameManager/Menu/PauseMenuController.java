package GameManager.Menu;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import GameManager.BaseGameController;


import java.io.IOException;

public class PauseMenuController {

    @FXML private Button menuBtn;
    @FXML private Button resumeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;

    private int level;
    private AnimationTimer gameLoop;
    private Parent pauseRoot;

    public void setData(int level, AnimationTimer gameLoop, Parent pauseRoot) {
        this.level = level;
        this.gameLoop = gameLoop;
        this.pauseRoot = pauseRoot;

        // Disable nút Previous nếu level 1, Next nếu level 6
        previousBtn.setDisable(level == 1);
        nextBtn.setDisable(level == 6);
    }

    @FXML
    public void initialize() {
        menuBtn.setOnAction(e -> switchScene("/RenderView/Menu/Menu.fxml", null));
        replayBtn.setOnAction(e -> switchLevel(level));
        nextBtn.setOnAction(e -> switchLevel(level + 1));
        previousBtn.setOnAction(e -> switchLevel(level - 1));
        resumeBtn.setOnAction(e -> resumeGame());

        addHoverEffect(menuBtn);
        addHoverEffect(replayBtn);
        addHoverEffect(nextBtn);
        addHoverEffect(previousBtn);
        addHoverEffect(resumeBtn);
    }

    private void resumeGame() {
        pauseRoot.setVisible(false);
        if (gameLoop != null) gameLoop.start();
    }

    private void switchLevel(int targetLevel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Level/Level" + targetLevel + ".fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("initLevel").invoke(controller);
                } catch (NoSuchMethodException ignored) {
                    // nếu Level không có initLevel(), bỏ qua
                }
            }

            Stage stage = (Stage) menuBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException | ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }


    private void switchScene(String fxmlPath, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) menuBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addHoverEffect(Button btn) {
        btn.setOnMouseEntered(this::onHover);
        btn.setOnMouseExited(this::onExit);
    }

    private void onHover(MouseEvent event) {
        Button btn = (Button) event.getSource();

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.play();

        TranslateTransition shake = new TranslateTransition(Duration.millis(60), btn);
        shake.setByX(5);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.play();
    }

    private void onExit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    private BaseGameController baseGameController;

    public void setBaseGameController(BaseGameController controller) {
        this.baseGameController = controller;
    }
}
