package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;

public class Level1Controller extends BaseGameController {
    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        super.onBrickHit(brick, ball);
    }
    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level1.png", "/Images/Entity/paddle.png");
    }


}