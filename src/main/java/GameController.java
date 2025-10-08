
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GameController {

    @FXML
    private Pane gamePane;

    @FXML
    private Rectangle Paddle;

    @FXML
    private Circle Ball;

    private double ballDirX = 1;
    private double ballDirY = 1;
    private double ballSpeed = 3;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    @FXML
    public void initialize() {
        setupControls();
        startGameLoop();
    }

    private void setupControls() {
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            if (e.getCode()==KeyCode.LEFT) moveLeft = true;
            if (e.getCode()==KeyCode.RIGHT) moveRight = true;
        });
        gamePane.setOnKeyReleased(e -> {
            if (e.getCode()== KeyCode.LEFT) moveLeft = false;
            if (e.getCode()== KeyCode.RIGHT) moveRight = false;
        });
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    private void update() {
        double sceneWidth = gamePane.getWidth();
        double sceneHeight = gamePane.getHeight();

        // Paddle movement
        if (moveLeft && Paddle.getLayoutX() > 0) {
            Paddle.setLayoutX(Paddle.getLayoutX() - 5);
        }
        if (moveRight && Paddle.getLayoutX() + Paddle.getWidth() < sceneWidth) {
            Paddle.setLayoutX(Paddle.getLayoutX() + 5);
        }
        double newX = Ball.getCenterX() + ballDirX * ballSpeed;
        double newY = Ball.getCenterY() + ballDirY * ballSpeed;
        if (newX - Ball.getRadius() <= 0 ||newX + Ball.getRadius() >= sceneWidth)
            ballDirX *= -1;
        if (newY - Ball.getRadius() <= 0)
            ballDirY *= -1;
        if (checkCollision()) {
            Ball.setCenterY(Paddle.getLayoutY() - Ball.getRadius() - 1);
            ballDirY = -Math.abs(ballDirY); // Luôn đi lên
        }

      // phanf sau
        if (newY + Ball.getRadius() > sceneHeight) {
            resetBall();
        }

        Ball.setCenterX(newX);
        Ball.setCenterY(newY);
    }

    private boolean checkCollision() {
        double ballX = Ball.getCenterX();
        double ballY = Ball.getCenterY();
        double radius = Ball.getRadius();

        double paddleX = Paddle.getLayoutX();
        double paddleY = Paddle.getLayoutY();
        double paddleW = Paddle.getWidth();
        double paddleH = Paddle.getHeight();

        return (ballY + radius >= paddleY &&
                ballY + radius <= paddleY + paddleH &&
                ballX >=paddleX &&
                ballX <= paddleX + paddleW);
    }

    private void resetBall() {
        Ball.setCenterX(gamePane.getWidth() / 2);
        Ball.setCenterY(gamePane.getHeight() / 2);
        ballDirY = -1;
    }
}

