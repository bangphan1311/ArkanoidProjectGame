import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class paddleController {

    @FXML
    private Rectangle Paddle;

    @FXML
    private Pane gamePane;

    private static final double PADDLE_SPEED = 35;

    @FXML
    public void initialize() {
        setupKeyListeners();
    }

    private void setupKeyListeners() {
        gamePane.setOnKeyPressed(event -> {
            double currentX = Paddle.getLayoutX();
            double paddleWidth = Paddle.getWidth();

            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                double newX = currentX - PADDLE_SPEED;
                if (newX >= 0) {
                    Paddle.setLayoutX(newX);
                } else {
                    Paddle.setLayoutX(0);
                }
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                double newX = currentX + PADDLE_SPEED;
                if (newX + paddleWidth <= gamePane.getWidth()) {
                    Paddle.setLayoutX(newX);
                } else {
                    Paddle.setLayoutX(gamePane.getWidth() - paddleWidth);
                }
            }
        });

        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
    }
}
