package GameManager.Level;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform; // ✅ Import
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

// ✅ 1. THÊM CÁC IMPORT CẦN THIẾT CHO ĐA LUỒNG
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
    @FXML private Label highScoreStatusLabel; // ✅ 2. Đảm bảo FXML của bạn CÓ Label này
    @FXML private Button homeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;

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

    /**
     * ✅ 3. SỬA LẠI HÀM SETDATA
     * Hàm này sẽ nhận dữ liệu, cập nhật UI, và BẮT ĐẦU luồng nền.
     */
    public void setData(int level, int score, boolean isWin) {
        this.level = level;
        this.score = score;
        this.isWin = isWin;

        // Cập nhật UI ngay lập tức
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }

        // Ẩn/hiện nút "Next"


        // ✅ 4. BẮT ĐẦU TÁC VỤ NẶNG (Lưu điểm) TRÊN LUỒNG RIÊNG
        startSaveScoreTask();
    }

    /**
     * ✅ 5. HÀM MỚI: TẠO VÀ CHẠY LUỒNG NỀN (Task)
     * (Đây là code "Đa luồng" của bạn)
     */
    private void startSaveScoreTask() {
        // Lấy tên người dùng hiện tại
        final String currentUser = Session.getUsername();
        if (currentUser == null || currentUser.isEmpty()) {
            highScoreStatusLabel.setText("Không thể lưu điểm (Chưa đăng nhập).");
            return;
        }

        // Tạo một "Task" (nhiệm vụ) để chạy ngầm
        Task<Void> saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // ĐÂY LÀ TÁC VỤ NẶNG (ĐỌC/GHI FILE)
                // Nó chạy trên một luồng riêng, không làm treo UI
                try {
                    HighScoresController highScoresController = new HighScoresController();

                    // 1. Lấy điểm cao cũ (Tác vụ I/O)
                    int oldHighScore = highScoresController.getHighScore(currentUser);

                    // 2. Lưu điểm mới nếu cao hơn (Tác vụ I/O)
                    if (score > oldHighScore) {
                        highScoresController.saveScore(currentUser, score);

                        // Cập nhật UI (phải dùng Platform.runLater)
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

        // Báo cho label biết là đang tải
        highScoreStatusLabel.setText("Đang kiểm tra điểm cao...");

        // ✅ 6. KHỞI CHẠY LUỒNG NỀN
        new Thread(saveTask).start();
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

        // ❌ XÓA DÒNG NÀY:
        // if (scoreLabel != null) {
        //     scoreLabel.setText("FINAL SCORE: " + score);
        // }
        // (Vì `score` vẫn là 0 ở thời điểm này, `setData` sẽ cập nhật nó)
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

    // Phương thức chuyển scene
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