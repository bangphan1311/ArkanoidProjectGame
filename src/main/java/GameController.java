import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML
    private Pane gamePane;

    @FXML
    private Pane brickPane;

    @FXML
    private Rectangle Paddle;

    @FXML
    private Circle Ball;

    private double ballDirX = 1;
    private double ballDirY = -1;
    private double ballSpeed = 3;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    private final List<Brick> bricks = new ArrayList<>();

    @FXML
    public void initialize() {
        setupControls();
        loadBricksFromPane();
        startGameLoop();
        gamePane.requestFocus();
    }
  ///  thiêts lập sao cho paddle di chuyển theo hướng phím
    private void setupControls() {
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight = true;
        });
        gamePane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight = false;
        });
    }

    private void loadBricksFromPane() {
        if (brickPane == null) return;

       /// duyệt các hình gạch
        for (Node node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof Rectangle) {
                Rectangle r = (Rectangle) node;
                double lx = r.getLayoutX();
                double ly = r.getLayoutY();
                double w = r.getWidth();
                double h = r.getHeight();
                Color c = Color.GRAY;
                if (r.getFill() instanceof Color) {
                    c = (Color) r.getFill();
                }

                Brick brick = new Brick(lx, ly, w, h, c);

                ///đảm bảo shape có đúng toạ độ: brick constructor dùng (x,y,width,height)
                /// add vào danh sách và lên gamePane để render
                bricks.add(brick);
                gamePane.getChildren().add(brick.getShape());

              /// ẩn các HCN mẫu
                r.setVisible(false);
            }
        }
    }
   ///  gem loop , thiết lập AnimationTimer
    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }
/// xử lý paddle di chuyển
    private void update() {
        double sceneWidth = gamePane.getWidth();
        double sceneHeight = gamePane.getHeight();

        // Paddle movement
        double paddleSpeed = 5;
        if (moveLeft && Paddle.getLayoutX() > 0) {
            Paddle.setLayoutX(Math.max(0, Paddle.getLayoutX() - paddleSpeed));
        }
        if (moveRight && Paddle.getLayoutX() + Paddle.getWidth() < sceneWidth) {
            Paddle.setLayoutX(Math.min(sceneWidth - Paddle.getWidth(), Paddle.getLayoutX() + paddleSpeed));
        }
        ///  xử lý bóng di chuyển
        double newX = Ball.getCenterX() + ballDirX * ballSpeed;
        double newY = Ball.getCenterY() + ballDirY * ballSpeed;

        // walls
        if (newX - Ball.getRadius() <= 0 || newX + Ball.getRadius() >= sceneWidth) {
            ballDirX *= -1;
        }
        if (newY - Ball.getRadius() <= 0) {
            ballDirY *= -1;
        }

        ///  va chạm giữa bóng và thanh
        if (checkCollisionWithPaddle()) {
            Ball.setCenterY(Paddle.getLayoutY() - Ball.getRadius() - 1);
            ballDirY = -Math.abs(ballDirY); // bật lên
            // tùy chọn: thay đổi ballDirX dựa trên vị trí chạm để tăng điều khiển
            double hitPos = (Ball.getCenterX() - (Paddle.getLayoutX() + Paddle.getWidth()/2)) / (Paddle.getWidth()/2);
            ballDirX = hitPos;
        }

        // brick collision
        checkCollisionWithBricks();

        ///  xử lý game over sau này, hiện tại là bóng rơi thì đóng chương trình
        if (newY - Ball.getRadius() > sceneHeight) {
            Platform.exit();
            return;
        }

        Ball.setCenterX(newX);
        Ball.setCenterY(newY);
    }

    private boolean checkCollisionWithPaddle() {
        double ballX = Ball.getCenterX();
        double ballY = Ball.getCenterY();
        double radius = Ball.getRadius();

        double paddleX = Paddle.getLayoutX();
        double paddleY = Paddle.getLayoutY();
        double paddleW = Paddle.getWidth();
        double paddleH = Paddle.getHeight();

        return (ballY + radius >= paddleY &&
                ballY - radius <= paddleY + paddleH &&
                ballX >= paddleX &&
                ballX <= paddleX + paddleW);
    }
   ///  các va chạm lần lượt là thanh và gạch
    private void checkCollisionWithBricks() {
        double ballX = Ball.getCenterX();
        double ballY = Ball.getCenterY();
        double radius = Ball.getRadius();

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;

            Rectangle r = brick.getShape();
            /// gạch( tọa độ)
            double bx = r.getX();
            double by = r.getY();
            double bw = r.getWidth();
            double bh = r.getHeight();

            boolean intersects = (ballX + radius >= bx &&
                    ballX - radius <= bx + bw &&
                    ballY + radius >= by &&
                    ballY - radius <= by + bh);

            if (intersects) {
                brick.destroy();
                // đơn giản đảo chiều theo Y
                ballDirY *= -1;
                break; // chỉ 1 viên/khung hình
            }
        }
    }
}
