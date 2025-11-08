package GameManager.Level;

import Entity.Ball;
import Entity.Brick;
import GameManager.BaseGameController;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controller CHỈ DÀNH RIÊNG cho Level 5.
 * - Có 2 tàu vũ trụ và 1 thanh di chuyển qua lại.
 * - Khi bóng đập vào gạch "nhân bóng" thì sinh thêm 1 quả bóng mới.
 */
public class Level5Controller extends BaseGameController {

    // --- Các ImageView trong FXML ---
    @FXML
    private ImageView tau1, tau2, thanhLv5;

    private double speed1 = 2;      // tốc độ tàu 1
    private double speed2 = -2;     // tốc độ tàu 2
    private double thanhSpeed = 3;  // tốc độ thanh

    @Override
    public void initialize() {
        super.initialize();

        // --- Set fitWidth nếu chưa có ---
        if (tau1.getFitWidth() == 0) tau1.setFitWidth(60);
        if (tau2.getFitWidth() == 0) tau2.setFitWidth(60);
        if (thanhLv5.getFitWidth() == 0) thanhLv5.setFitWidth(120);

        // --- Tạo bộ đếm khung hình để di chuyển liên tục ---
        AnimationTimer movement = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveSpaceship(tau1, true);
                moveSpaceship(tau2, false);
                moveBar(thanhLv5);
            }
        };
        movement.start();
    }

    /**
     * Di chuyển tàu qua lại giữa 2 biên
     */
    private void moveSpaceship(ImageView tau, boolean isFirst) {
        if (tau == null) return;

        // Cập nhật vị trí
        double speed = isFirst ? speed1 : speed2;
        tau.setLayoutX(tau.getLayoutX() + speed);

        // chạm biên thì đổi hướng
        if (tau.getLayoutX() <= 0 || tau.getLayoutX() + tau.getFitWidth() >= gamePane.getWidth()) {
            if (isFirst) speed1 = -speed1;
            else speed2 = -speed2;

            // --- Hiệu ứng rung nhẹ khi đổi hướng ---
            tau.setLayoutX(tau.getLayoutX() + (isFirst ? 2 : -2));
        }
    }

    /**
     * Di chuyển thanh giữa qua lại
     */
    private void moveBar(ImageView thanh) {
        if (thanh == null) return;

        thanh.setLayoutX(thanh.getLayoutX() + thanhSpeed);

        // Đổi hướng khi chạm biên
        if (thanh.getLayoutX() <= 0 || thanh.getLayoutX() + thanh.getFitWidth() >= gamePane.getWidth()) {
            thanhSpeed = -thanhSpeed;

            // --- Hiệu ứng rung nhẹ khi đổi hướng ---
            thanh.setLayoutX(thanh.getLayoutX() + (thanhSpeed > 0 ? 2 : -2));
        }
    }

    /**
     * Khi bóng đập vào gạch — kiểm tra loại gạch đặc biệt
     */
    @Override
    protected void onBrickHit(Brick brick, Ball ball) {

        String fxId = brick.getFxId() != null ? brick.getFxId().toLowerCase() : "";

        // --- Gạch nhân đôi bóng ---
        if (fxId.contains("doubleball")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnDoubleBall(ball); // Gọi hàm nhân bóng
            }
        }

        // --- Gạch cho thanh từ tính ---
        else if (fxId.contains("magnetic")) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                gamePane.getChildren().remove(brick.getShape());
                spawnPowerUp(brick, "MAGNETIC_PADDLE");
            }
        }

        // --- Gạch thường ---
        else {
            super.onBrickHit(brick, ball);
        }
    }

    /**
     * Nhân đôi bóng — tạo 1 quả bóng mới dựa trên bóng hiện tại
     */
    private void spawnDoubleBall(Ball originalBall) {
        if (originalBall == null) return;

        // Lấy thông tin bóng hiện tại
        double x = originalBall.getShape().getCenterX();
        double y = originalBall.getShape().getCenterY();
        double radius = originalBall.getShape().getRadius();

        // Tạo quả bóng mới
        Circle newCircle = new Circle(x + 15, y, radius, Color.CYAN);
        Ball newBall = new Ball(newCircle, gamePane.getWidth(), gamePane.getHeight());

        // Hướng bay ngược lại quả cũ
        newBall.setDirX(-originalBall.getDirX());
        newBall.setDirY(-originalBall.getDirY());
        newBall.setSpeed(originalBall.getSpeed());

        // Thêm vào giao diện và danh sách quản lý
        gamePane.getChildren().add(newBall.getShape());
        balls.add(newBall); // dùng danh sách bóng có sẵn trong BaseGameController
    }
}
