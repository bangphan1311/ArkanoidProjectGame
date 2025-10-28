package Entity;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends MovableObject {
    private Circle ballShape;
    private double speed = 1;
    private double dirX = 1;
    private double dirY = 1;
    private double sceneWidth;
    private double sceneHeight;

    /**
     * Khởi tạo bóng
     */
    public Ball(double radius, double sceneWidth, double sceneHeight) {
        super(sceneWidth / 2, sceneHeight / 2, radius * 2, radius * 2);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        ballShape = new Circle(radius, Color.RED);
        ballShape.setCenterX(sceneWidth / 2);
        ballShape.setCenterY(sceneHeight / 2 - 100); // tránh chồng paddle
    }

    public Circle getShape() {
        return ballShape;
    }

    @Override
    public void move() {
        double newX = ballShape.getCenterX() + dirX * speed;
        double newY = ballShape.getCenterY() + dirY * speed;

        /**
         * Đảo hướng khi chạm biên trái/phải
          */
        if (newX - ballShape.getRadius() <= 0 || newX + ballShape.getRadius() >= sceneWidth) {
            dirX *= -1;
        }

        /**
         * Đảo hướng khi chạm trần
         */
        if (newY - ballShape.getRadius() <= 0) {
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

    /**
     * va chạm of bóng
     */
    public boolean checkCollision(GameObject other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
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

    public double getDirX() {
        return dirX;
    }

    public double getDirY() {
        return dirY;
    }

}
