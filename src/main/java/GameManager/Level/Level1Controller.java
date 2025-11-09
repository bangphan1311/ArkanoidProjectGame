package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Level1Controller extends BaseGameController {

    @FXML
    private Button pauseBtn;

    @FXML
    protected Pane gamePane;

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        super.onBrickHit(brick, ball);
    }

    @Override
    public void initLevel() {

        setupLevel("/Images/MapLevel/level1.png", "/Images/Entity/paddle.png");

        pauseBtn.setOnAction(event -> {
            showPauseMenu(); // Gọi menu tạm dừng
        });
    }

    @Override
    protected int getCurrentLevelNumber() {
        return 1;
    }
}