package GameManager;

import Entity.Paddle;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

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
                gamePane.getPrefWidth()
        );
        setupKeyListeners();
    }
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
}