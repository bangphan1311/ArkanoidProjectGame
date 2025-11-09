package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.animation.AnimationTimer;

public class Level1Controller extends BaseGameController {

    @FXML
    protected Pane gamePane;

    @FXML
    private Label highScoreLabel;

    @FXML
    private AnchorPane pauseMenuPane;

    @FXML
    private Button resumeButton, restartButton, menuButton, nextButton, prevButton, pauseButton;

    private AnimationTimer gameLoop;

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        super.onBrickHit(brick, ball);
    }

    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level1.png", "/Images/Entity/paddle.png");
        showHighScore();

        // Khởi tạo game loop nếu chưa có
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Game vẫn chạy nhờ BaseGameController
                }
            };
        }
        startGame();
    }

    private void showHighScore() {
        int level = getCurrentLevelNumber();
        int highScore = getHighScoreForLevel(level);
        highScoreLabel.setText("High Score: " + highScore);
    }

    @Override
    protected int getCurrentLevelNumber() {
        return 1;
    }

    /*** Pause / Resume ***/
    @FXML
    private void handlePause() {
        pauseMenuPane.setVisible(true);
        stopGame();
    }

    @FXML
    private void handleResume() {
        pauseMenuPane.setVisible(false);
        startGame();
    }

    /*** Restart ***/
    @FXML
    private void handleRestart() {
        pauseMenuPane.setVisible(false);
        initLevel(); // reset level
    }

    /*** Menu / Next / Previous ***/
    @FXML
    private void handleMenu() {
        System.out.println("Menu button pressed");
        // Thêm chuyển scene về Menu chính nếu muốn
    }

    @FXML
    private void handleNext() {
        System.out.println("Next button pressed");
        // Chuyển sang level tiếp theo
    }

    @FXML
    private void handlePrevious() {
        System.out.println("Previous button pressed");
        // Chuyển sang level trước
    }

    private void startGame() {
        if (gameLoop != null) gameLoop.start();
    }

    private void stopGame() {
        if (gameLoop != null) gameLoop.stop();
    }
}
