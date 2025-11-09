package GameManager.Level;


import Entity.Ball;
import Entity.Brick;
public class Level6Controller extends Level5Controller {

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";
        if (fxId.contains("bomb")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "BOMB");
            }
        }
        else if (fxId.contains("enlarge")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "ENLARGE_PADDLE");
            }
        }
        else {
            super.onBrickHit(brick, ball);
        }
    }
    @Override
    public void initLevel() {
        setupLevel("/Images/MapLevel/level6.png", "/Images/Entity/paddle.png");
    }

}