package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;

public class Level5Controller extends BaseGameController {

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";

        // logic
        if (fxId.contains("magnetic")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                // công cụ vật phẩm dính
                spawnPowerUp(brick, "MAGNETIC_PADDLE");
            }
        }
        else if (fxId.contains("doublescore")) { // (fx:id của gạch trong Scene Builder)
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "DOUBLE_SCORE"); // Gọi "công cụ" thả x2 điểm
            }
        }

        // --- Tái sử dụng logic CŨ (gạch thường, mạnh, v.v.) ---
        else {
            super.onBrickHit(brick, ball);
        }
    }
}