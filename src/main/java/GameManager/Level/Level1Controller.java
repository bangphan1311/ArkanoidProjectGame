package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.animation.AnimationTimer;

public class Level1Controller extends BaseGameController {

    @FXML
    protected Pane gamePane;

    @FXML
    private AnchorPane pauseMenuPane;

    @FXML
    private Button pauseButton, resumeButton, restartButton, menuButton, nextButton, prevButton;

    private AnimationTimer gameLoop;

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        super.onBrickHit(brick, ball);
    }

    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level1.png", "/Images/Entity/paddle.png");

        // Khởi tạo game loop nếu chưa có
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Game vẫn chạy nhờ BaseGameController
                }
            };
        }

        // Ẩn pause menu lúc bắt đầu
        if (pauseMenuPane != null) {
            pauseMenuPane.setVisible(false);
        }

        startGame();
    }

    @Override
    protected int getCurrentLevelNumber() {
        return 1;
    }

    /*** Pause / Resume ***/
    @FXML
    private void handlePause() {
        if (pauseMenuPane != null) {
            pauseMenuPane.setVisible(true);
        }
        stopGame();
    }

    @FXML
    private void handleResume() {
        if (pauseMenuPane != null) {
            pauseMenuPane.setVisible(false);
        }
        startGame();
    }

    /*** Restart Level ***/
    @FXML
    private void handleRestart() {
        if (pauseMenuPane != null) {
            pauseMenuPane.setVisible(false);
        }
        initLevel(); // reset level
    }

    /*** Menu / Next / Previous ***/
    @FXML
    private void handleMenu() {
        System.out.println("Menu button pressed");
        // Chuyển scene về menu chính nếu muốn
    }

    @FXML
    private void handleNext() {
        System.out.println("Next button pressed");
        // Chuyển sang level tiếp theo nếu muốn
    }

    @FXML
    private void handlePrevious() {
        System.out.println("Previous button pressed");
        // Chuyển sang level trước nếu muốn
    }

    /*** Game control ***/
    private void startGame() {
        if (gameLoop != null) gameLoop.start();
    }

    private void stopGame() {
        if (gameLoop != null) gameLoop.stop();
    }
}
