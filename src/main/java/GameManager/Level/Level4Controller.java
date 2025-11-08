package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;

/**
 * Controller CHỈ DÀNH RIÊNG cho Level 4.
 */
public class Level4Controller extends BaseGameController {

    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";

        // --- Logic MỚI của Level 4 ---
        if (fxId.contains("magnetic")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                // Gọi "công cụ" thả vật phẩm "Dính"
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