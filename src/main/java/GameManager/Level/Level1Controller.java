package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Level1Controller extends BaseGameController {

    @FXML
    protected Pane gamePane;

    @FXML
    private Label highScoreLabel;

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        super.onBrickHit(brick, ball);
    }

    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level1.png", "/Images/Entity/paddle.png");

        showHighScore(); // htih highscore
    }

    private void showHighScore() {
        int level = getCurrentLevelNumber(); // level hiện tại
        int highScore = getHighScoreForLevel(level); // gọi phương thức lấy điểm cao nhất
        highScoreLabel.setText("High Score: " + highScore);
    }

    @Override
    protected int getCurrentLevelNumber() {
        return 1;
    }
}
