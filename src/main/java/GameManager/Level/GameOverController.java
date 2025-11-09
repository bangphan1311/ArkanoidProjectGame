package GameManager.Level;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameOverController {

    @FXML private Button homeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;


    private int level;
    private int score;
    private boolean isWin;

    // phương thức thiết lập dữ liệu nhưng không set Text
    public void setData(int level, int score, boolean isWin) {
        this.level = level;
        this.score = score;
        this.isWin = isWin;

        // nếu muốn in ra console để kiểm tra
        System.out.println("Level: " + level + ", Score: " + score + ", isWin: " + isWin);
    }

    @FXML
    public void initialize() {
        // xử lý nút Home
        if (homeBtn != null) {
            homeBtn.setOnMouseClicked(e -> {
                // code chuyển scene về menu
                System.out.println("Về Home");
            });
        }

        // xử lý nút Replay
        if (replayBtn != null) {
            replayBtn.setOnMouseClicked(e -> {
                System.out.println("Chơi lại level: " + level);
            });
        }

        // Next và Previous tương tự
        if (nextBtn != null) {
            nextBtn.setOnMouseClicked(e -> {
                System.out.println("Next Level: " + (level + 1));
            });
        }

        if (previousBtn != null) {
            previousBtn.setOnMouseClicked(e -> {
                System.out.println("Previous Level: " + (level - 1));
            });
        }
    }
}
