package GameManager.Level;

import Entity.Ball;
import Entity.Brick;

public class Level5Controller extends Level4Controller {

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";
        if (fxId.contains("slowdown")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "SLOW_DOWN");
            }
        }
        else {
            super.onBrickHit(brick, ball);
        }
    }
    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level5.png", "/Images/Entity/paddle.png");
    }
    @Override
    protected int getCurrentLevelNumber() {
        return 5;
    }

}