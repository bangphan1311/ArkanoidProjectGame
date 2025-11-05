package Entity;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends MovableObject {
    private Circle ballShape;
    private double speed = 3.5;
    private double dirX = 0;
    private double dirY = -1;
    private double sceneWidth;
    private double sceneHeight;

    private static final double MIN_SPEED = 2.5;
    private static final double MAX_SPEED = 5;

    public Ball(double radius, double sceneWidth, double sceneHeight) {
        super(sceneWidth / 2, sceneHeight / 2, radius * 2, radius * 2);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        // Khởi tạo đúng vị trí khớp FXML
        ballShape = new Circle(radius, Color.RED);
        ballShape.setCenterX(sceneWidth / 2);
        ballShape.setCenterY(sceneHeight / 2 + 50);
    }

    public Circle getShape() {
        return ballShape;
    }

    @Override
    public void move() {
        double newX = ballShape.getCenterX() + dirX * speed;
        double newY = ballShape.getCenterY() + dirY * speed;

        // Giới hạn biên
        if (newX - ballShape.getRadius() <= 0 || newX + ballShape.getRadius() >= sceneWidth) {
            reverseX();
            newX = clamp(newX, ballShape.getRadius(), sceneWidth - ballShape.getRadius());
        }

        if (newY - ballShape.getRadius() <= 0) {
            reverseY();
            newY = ballShape.getRadius() + 1;
        }

        ballShape.setCenterX(newX);
        ballShape.setCenterY(newY);
        this.x = newX;
        this.y = newY;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public Node render() {
        return ballShape;
    }

    // Đảm bảo đồng bộ toạ độ giữa logic và hiển thị
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        ballShape.setCenterX(x);
        ballShape.setCenterY(y);
    }

    public void reflect(double normalX, double normalY) {
        double dot = dirX * normalX + dirY * normalY;
        dirX = dirX - 2 * dot * normalX;
        dirY = dirY - 2 * dot * normalY;
        normalizeDirection();
        addSmallRandomAngle();
    }

    public void addSmallRandomAngle() {
        double angleChange = (Math.random() - 0.5) * Math.toRadians(6);
        double angle = Math.atan2(dirY, dirX) + angleChange;
        double speedMag = Math.sqrt(dirX * dirX + dirY * dirY);
        dirX = Math.cos(angle) * speedMag;
        dirY = Math.sin(angle) * speedMag;
    }

    private void normalizeDirection() {
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        if (length != 0) {
            dirX /= length;
            dirY /= length;
        }
    }

    public void setDirX(double dirX) { this.dirX = dirX; }
    public void setDirY(double dirY) { this.dirY = dirY; }
    public void reverseX() { dirX = -dirX; }
    public void reverseY() { dirY = -dirY; }
    public double getRadius() { return ballShape.getRadius(); }
    public double getDirX() { return dirX; }
    public double getDirY() { return dirY; }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double s) {
        this.speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, s));
    }
}