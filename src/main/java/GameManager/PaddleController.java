package GameManager;

import Entity.Paddle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class PaddleController {
    @FXML
    private Rectangle paddleRect;

    @FXML
    private Pane gamePane;
    private static final double PADDLE_SPEED = 35;
    private Paddle paddle;

    @FXML
    public void initialize() {
        paddle = new Paddle(
                paddleRect.getLayoutX(),
                paddleRect.getLayoutY(),
                paddleRect.getWidth(),
                paddleRect.getHeight(),
                PADDLE_SPEED,
                gamePane.getPrefWidth()   // 👈 thêm dòng này
        );
        setupKeyListeners();
    }

    /**
     * Thiết lập phím di chuyển trái/phải.
     */
    private void setupKeyListeners() {
        gamePane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                paddle.moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                paddle.moveRight();
            }
            updatePaddleView();
        });

        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
    }

    /**
     * Cập nhật vị trí hiển thị của Paddle trên giao diện.
     */
    private void updatePaddleView() {
        if (paddle.getX() < 0) {
            paddle.setX(0);
        } else if (paddle.getX() + paddle.getWidth() > gamePane.getWidth()) {
            paddle.setX(gamePane.getWidth() - paddle.getWidth());
        }
        paddleRect.setLayoutX(paddle.getX());
        paddleRect.setLayoutY(paddle.getY());
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public static class SettingController {

        @FXML
        private ToggleButton musicToggle, soundToggle;

        @FXML
        void handleMenu(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/RenderView/Menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ARKANOID - Menu");
            stage.show();
        }

        @FXML
        public void initialize() {
            musicToggle.setOnAction(e -> {
                if (musicToggle.isSelected()) {
                    musicToggle.setText("Bật");
                    System.out.println("Nhạc nền: BẬT");
                } else {
                    musicToggle.setText("Tắt");
                    System.out.println("Nhạc nền: TẮT");
                }
            });

            soundToggle.setOnAction(e -> {
                if (soundToggle.isSelected()) {
                    soundToggle.setText("Bật");
                    System.out.println("Hiệu ứng âm thanh: BẬT");
                } else {
                    soundToggle.setText("Tắt");
                    System.out.println("Hiệu ứng âm thanh: TẮT");
                }
            });
        }
    }
}
