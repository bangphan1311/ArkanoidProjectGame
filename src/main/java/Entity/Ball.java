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

    private double originalSpeed = -1;
    private long lastCollisionTime = 0;
    private boolean isCaught = false;
    private double catchOffset = 0;

    public Ball(Circle shape, double sceneWidth, double sceneHeight) {
        super(shape.getCenterX(), shape.getCenterY(), shape.getRadius() * 2, shape.getRadius() * 2);
        this.ballShape = shape; // Không tạo mới, mà gán từ tham số
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
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
        if (isCaught) return;
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
    public void multiplySpeed(double multiplier) {
        // Chỉ lưu tốc độ gốc lần đầu tiên
        if (originalSpeed == -1) {
            originalSpeed = getSpeed(); // Giả sử bạn có hàm getSpeed()
        }
        setSpeed(originalSpeed * multiplier); // Giả sử bạn có hàm setSpeed()
    }
    public void resetSpeed() {
        if (originalSpeed != -1) {
            setSpeed(originalSpeed);
            originalSpeed = -1; // Reset lại
        }
    }
    public long getLastCollisionTime() {
        return lastCollisionTime;
    }

    public void setLastCollisionTime(long time) {
        this.lastCollisionTime = time;
    }
    public boolean isCaught() {
        return isCaught;
    }

    public void setCaught(boolean caught, double paddleX) {
        this.isCaught = caught;
        if (caught) {
            this.catchOffset = getX() - paddleX;
            this.dirX = 0;
            this.dirY = 0;
        } else {
            if (dirY == 0) dirY = -1;
            if (dirX == 0) dirX = (Math.random() < 0.5) ? -1 : 1;
            normalizeDirection();
        }
    }

    public double getCatchOffset() {
        return catchOffset;
    }
}