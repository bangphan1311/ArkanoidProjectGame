package GameManager.Level;

import GameManager.BaseGameController;
import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;

public class Level5Controller extends BaseGameController {

    private ImageView tau1, tau2, thanhLv5;
    private double speed1 = 2;      // tốc độ tàu 1
    private double speed2 = -2;     // tốc độ tàu 2
    private double thanhSpeed = 3;  // tốc độ thanh

    @Override
    public void initialize() {
        super.initialize();

        // Lấy đối tượng từ FXML
        tau1 = (ImageView) gamePane.lookup("#obstacle11");   // tàu 1
        tau2 = (ImageView) gamePane.lookup("#magnetic_111"); // tàu 2
        thanhLv5 = (ImageView) gamePane.lookup("#thanhlv5"); // thanh

        // Tạo bộ đếm khung hình để di chuyển liên tục
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
        }
    }
}
