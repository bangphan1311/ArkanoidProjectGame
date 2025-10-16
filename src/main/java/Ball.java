import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends MovableObject {
    private Circle ballShape;
    private double speed = 3;
    private double dirX = 1;
    private double dirY = 1;
    private double sceneWidth;
    private double sceneHeight;

    public Ball(double radius, double sceneWidth, double sceneHeight) {
        super(sceneWidth / 2, sceneHeight / 2, radius * 2, radius * 2);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        ballShape = new Circle(radius, Color.RED);
        ballShape.setCenterX(sceneWidth / 2);
        ballShape.setCenterY(sceneHeight / 2);
    }

    public Circle getShape() {
        return ballShape;
    }

    @Override
    public void move() {
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

        this.x = ballShape.getCenterX();
        this.y = ballShape.getCenterY();
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public Node render() {
        return ballShape;
    }

    public boolean checkCollision(GameObject other) {
        return ballShape.getBoundsInParent().intersects(
                other.getX(), other.getY(), other.getWidth(), other.getHeight());
    }

    public void bounceOff(GameObject other) {
        dirY *= -1;
    }

    public void setPosition(double x, double y) {
        ballShape.setCenterX(x);
        ballShape.setCenterY(y);
        this.x = x;
        this.y = y;
    }

    public void setDirX(double dirX) {
        this.dirX = dirX;
    }

    public void setDirY(double dirY) {
        this.dirY = dirY;
    }

    public void reverseX() {
        dirX = -dirX;
    }

    public void reverseY() {
        dirY = -dirY;
    }

    public double getRadius() {
        return ballShape.getRadius();
    }
}
