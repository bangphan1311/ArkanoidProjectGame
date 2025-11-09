package GameManager.Level;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import GameManager.Menu.Session;
import GameManager.Menu.HighScoresController;



public class GameOverController {

    @FXML private Button homeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;
    @FXML private Label scoreLabel;

    private int level;
    private int score;
    private boolean isWin;

    // Map liên kết level với controller tương ứng
    private static final Map<Integer, Class<?>> levelControllerMap = new HashMap<>();
    static {
        levelControllerMap.put(1, Level1Controller.class);
        levelControllerMap.put(2, Level2Controller.class);
        levelControllerMap.put(3, Level3Controller.class);
        levelControllerMap.put(4, Level4Controller.class);
        levelControllerMap.put(5, Level5Controller.class);
        levelControllerMap.put(6, Level6Controller.class);
    }

    // ===== Phương thức để set dữ liệu trước khi hiển thị GameOver =====
    public void setData(int level, int score, boolean isWin) {
        this.level = level;
        this.score = score;
        this.isWin = isWin;

        // Hiển thị điểm trên Label
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }

        // Cập nhật điểm cao cho tài khoản hiện tại
        String currentUser = Session.getUsername();
        if (currentUser != null && !currentUser.isEmpty()) {
            HighScoresController highScoresController = new HighScoresController();
            highScoresController.saveScore(currentUser, score);
        }
    }


    @FXML
    public void initialize() {
        // Thiết lập hành động cho các nút
        homeBtn.setOnAction(e -> switchScene("/RenderView/Menu/Menu.fxml", null));
        replayBtn.setOnAction(e ->
                switchScene("/RenderView/Level/Level" + level + ".fxml", levelControllerMap.get(level))
        );
        nextBtn.setOnAction(e -> {
            int nextLevel = level + 1;
            if (nextLevel <= 6) {
                switchScene("/RenderView/Level/Level" + nextLevel + ".fxml", levelControllerMap.get(nextLevel));
            }
        });
        previousBtn.setOnAction(e -> {
            int prevLevel = level - 1;
            if (prevLevel >= 1) {
                switchScene("/RenderView/Level/Level" + prevLevel + ".fxml", levelControllerMap.get(prevLevel));
            }
        });

        // Hover effect cho các nút
        addHoverEffect(homeBtn);
        addHoverEffect(replayBtn);
        addHoverEffect(nextBtn);
        addHoverEffect(previousBtn);

        // Nếu setData được gọi trước khi initialize, update Label
        if (scoreLabel != null) {
            scoreLabel.setText("FINAL SCORE: " + score);
        }
    }

    private void addHoverEffect(Button btn) {
        btn.setOnMouseEntered(this::onHover);
        btn.setOnMouseExited(this::onExit);
    }

    private void onHover(MouseEvent event) {
        Button btn = (Button) event.getSource();

        // Phóng to
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.play();

        // Rung nhẹ
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), btn);
        shake.setByX(5);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.play();

        btn.setEffect(new Glow(0.6));
    }

    private void onExit(MouseEvent event) {
        Button btn = (Button) event.getSource();

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();

        btn.setEffect(null);
    }

    // ===== Phương thức chuyển scene =====
    private void switchScene(String fxmlPath, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Nếu có controller level, gọi initLevel()
            if (controllerClass != null) {
                Object controller = loader.getController();
                if (controller != null) {
                    try {
                        controllerClass.getMethod("initLevel").invoke(controller);
                    } catch (NoSuchMethodException ignored) {
                        System.err.println("Controller " + controllerClass.getSimpleName() + " chưa có method initLevel()");
                    }
                }
            }

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Không load được FXML: " + fxmlPath);
        }
    }
}