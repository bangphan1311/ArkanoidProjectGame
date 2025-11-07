package GameManager;

import Entity.Ball;
import Entity.Brick;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
public class Level3Controller extends BaseGameController {

    private double originalPaddleWidth;
    private boolean isShrunk = false;
    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";

        if (fxId.contains("shrink")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                activateShrinkPaddle(5.0);
            }
        }
        else if (fxId.contains("speedup")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "SPEED_UP");
            }
        }
        else if (fxId.contains("paddlechange")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "PADDLE_CHANGE");
            }
        }
        else {
            super.onBrickHit(brick, ball);
        }
    }
    private void activateShrinkPaddle(double durationSeconds) {
        if (isShrunk) return;
        System.out.println("EFFECT: Paddle Shrinking!");
        isShrunk = true;

        originalPaddleWidth = paddleRect.getWidth();
        paddleRect.setWidth(originalPaddleWidth / 2);

        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Paddle Restored.");
            paddleRect.setWidth(originalPaddleWidth);
            isShrunk = false;
        });
        timer.play();
    }
}