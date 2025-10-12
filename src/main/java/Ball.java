import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
public class Ball {
    private Circle ballShape;
    private double dirX = 1;
    private double dirY = 1;
    private double speed = 3;
    private double sceneWidth;
    private double sceneHeight;

    public Ball(double radius, double sceneWidth, double sceneHeight) {
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        ballShape = new Circle(radius, Color.RED);
        ballShape.setCenterX(sceneWidth / 2);
        ballShape.setCenterY(sceneHeight / 2);
    }

    public Circle getShape() {
        return ballShape;
    }

    public void update() {
        double newX = ballShape.getCenterX() + dirX * speed;
        double newY = ballShape.getCenterY() + dirY * speed;

        if (newX - ballShape.getRadius() <= 0 || newX + ballShape.getRadius() >= sceneWidth) {
            dirX *= -1;
        }

        if (newY - ballShape.getRadius() <= 0 || newY + ballShape.getRadius() >= sceneHeight) {
            dirY *= -1;
        }

        ballShape.setCenterX(newX);
        ballShape.setCenterY(newY);
    }
}