package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;

public class Level2Controller extends Level1Controller {
    @Override
    protected void onBrickHit(Brick brick, Ball ball) {
        if ("2xMoney".equals(brick.getType())) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                System.out.println("2x TIỀN!");
            }
        }
        else {
            super.onBrickHit(brick, ball);
        }
    }
    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level2.png", "/Images/Entity/paddle.png");
    }


}