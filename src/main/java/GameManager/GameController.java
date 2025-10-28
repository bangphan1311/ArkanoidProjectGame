package GameManager;

import Entity.Ball;
import Entity.Paddle;
import Entity.Brick;

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
    private Rectangle paddleRect;
    @FXML
    private Circle ballCircle;

    private Paddle paddle;
    private Ball ball;
    private final List<Brick> bricks = new ArrayList<>();
    private boolean moveLeft = false;
    private boolean moveRight = false;

    @FXML
    public void initialize() {
        double sceneWidth = gamePane.getPrefWidth();
        double sceneHeight = gamePane.getPrefHeight();

        paddle = new Paddle(
                paddleRect.getLayoutX(),
                paddleRect.getLayoutY(),
                paddleRect.getWidth(),
                paddleRect.getHeight(),
                8,
                sceneWidth
        );

        ball = new Ball(
                ballCircle.getRadius(),
                sceneWidth,
                sceneHeight
        );
        ball.setDirection(ballCircle.getCenterX(), ballCircle.getCenterY());

        setupControls();
        loadBricksFromPane();
        startGameLoop();
        gamePane.requestFocus();
    }

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

        for (Node node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof Rectangle r) {
                Brick brick = new Brick(
                        r.getLayoutX(),
                        r.getLayoutY(),
                        r.getWidth(),
                        r.getHeight(),
                        (Color) r.getFill()
                );
                bricks.add(brick);
                gamePane.getChildren().add(brick.getShape());
                r.setVisible(false);
            }
        }
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

        if (moveLeft) paddle.moveLeft();
        if (moveRight) paddle.moveRight();

        paddleRect.setLayoutX(paddle.getShape().getX());
        paddleRect.setLayoutY(paddle.getShape().getY());

        ball.update();
        ballCircle.setCenterX(ball.getShape().getCenterX());
        ballCircle.setCenterY(ball.getShape().getCenterY());

        if (checkCollisionWithPaddle()) {
            ball.reverseY();
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            double hitPosition = (ball.getX() - paddleCenter) / (paddle.getWidth() / 2);

            hitPosition = Math.max(-1, Math.min(1, hitPosition));

            ball.setDirX(hitPosition);
        }

        checkCollisionWithBricks();
        if (ball.getY() - ball.getRadius() > sceneHeight) {
            Platform.exit();
        }
    }

    private boolean checkCollisionWithPaddle() {
        return ballCircle.getBoundsInParent().intersects(paddleRect.getBoundsInParent());
    }

    private void checkCollisionWithBricks() {
        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;

            Rectangle r = brick.getShape();
            if (ball.getShape().getBoundsInParent().intersects(r.getBoundsInParent())) {
                brick.destroy();
                ball.reverseY();
                break;
            }
        }
    }
}
