package GameManager.Level;

import GameManager.Menu.MapController;
import GameManager.SoundManager;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import GameManager.Menu.Session;
import GameManager.Menu.HighScoresController;

// import cho đa luong
import javafx.concurrent.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class GameOverController {

    @FXML private Label scoreLabel;
    @FXML private Label highScoreStatusLabel;
    @FXML private Button homeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;

    private int level;
    private int score;
    private boolean isWin;

    // map liên kết level với controller tương ứng of nó
    private static final Map<Integer, Class<?>> levelControllerMap = new HashMap<>();
    static {
        levelControllerMap.put(1, Level1Controller.class);
        levelControllerMap.put(2, Level2Controller.class);
        levelControllerMap.put(3, Level3Controller.class);
        levelControllerMap.put(4, Level4Controller.class);
        levelControllerMap.put(5, Level5Controller.class);
        levelControllerMap.put(6, Level6Controller.class);
    }

    /**
     *  nhận dữ liệu, cập nhật UI, và bắt đầu luồng nền
     */
    public void setData(int level, int score, boolean isWin) {
        this.level = level;
        this.score = score;
        this.isWin = isWin;

        // cập nhật UI ngay cho scoreLabel
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }

        // lưu điểm trên luồng riêng ( tác vụ nặng)
        startSaveScoreTask();
    }

    /**
     * Lưu điểm cao trên luồng nền
     */
    private void startSaveScoreTask() {
        // Lấy tên người dùng hiện tại
        final String currentUser = Session.getUsername();
        if (currentUser == null || currentUser.isEmpty()) {
            highScoreStatusLabel.setText("Không thể lưu điểm (Chưa đăng nhập).");
            return;
        }

        // Tạo Task để chạy ngầm
        Task<Void> saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // chạy trên một luồng riêng, không làm treo UI ( tác vụ nặng )
                try {
                    HighScoresController highScoresController = new HighScoresController();

                    // lấy điểm cao cũ (Tác vụ I/O)
                    int oldHighScore = highScoresController.getHighScore(currentUser);

                    // nếu cao hơn - lưu điểm cũ  (Tác vụ I/O)
                    if (score > oldHighScore) {
                        highScoresController.saveScore(currentUser, score);

                        // cập nhật UI (dùng Platform.runLater)
                        Platform.runLater(() -> {
                            highScoreStatusLabel.setText("⭐ ĐIỂM CAO MỚI! ⭐");
                            highScoreStatusLabel.setStyle("-fx-text-fill: gold;");
                        });
                    } else {
                        Platform.runLater(() -> {
                            highScoreStatusLabel.setText("Điểm cao của bạn: " + oldHighScore);
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        highScoreStatusLabel.setText("Lỗi khi lưu điểm cao.");
                    });
                }
                return null;
            }
        };

        highScoreStatusLabel.setText("Đang kiểm tra điểm cao...");

        // khởi chạy luồng nền
        new Thread(saveTask).start();
    }

    /**
     * thiết lập hành động cho các nút
     */
    @FXML
    public void initialize() {
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

        addHoverEffect(homeBtn);
        addHoverEffect(replayBtn);
        addHoverEffect(nextBtn);
        addHoverEffect(previousBtn);
    }

    /**
     * hiệu ứng cho nút
      */
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

    // trở về ban đầu
    private void onExit(MouseEvent event) {
        Button btn = (Button) event.getSource();

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();

        btn.setEffect(null);
    }

    /**
     * phg thức chuyển scene
     */
    private void switchScene(String fxmlPath, Class<?> controllerClass) {
        try {
            if (MapController.gameMusicPlayer != null &&
                    MapController.gameMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING &&
                    !SoundManager.isMusicMuted)
            {
                MapController.gameMusicPlayer.play();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // nếu là controller level, gọi initLevel()
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