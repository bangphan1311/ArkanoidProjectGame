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

        // Va chạm viền trái/phải
        if (newX - ballShape.getRadius() <= 0 || newX + ballShape.getRadius() >= sceneWidth) {
            dirX *= -1;
        }

        // Va chạm viền trên/dưới
        if (newY - ballShape.getRadius() <= 0 || newY + ballShape.getRadius() >= sceneHeight) {
            dirY *= -1;
        }

        ballShape.setCenterX(newX);
        ballShape.setCenterY(newY);

        // Cập nhật lại toạ độ cho lớp cha
        this.x = ballShape.getCenterX();
        this.y = ballShape.getCenterY();
    }

    @Override
    public void update() {
        move();
    }

    // ✅ Trả về Node để hiển thị lên Scene
    @Override
    public Node render() {
        return ballShape;
    }

    // Kiểm tra va chạm với đối tượng khác
    public boolean checkCollision(GameObject other) {
        return ballShape.getBoundsInParent().intersects(
                other.getX(), other.getY(), other.getWidth(), other.getHeight());
    }

    // Phản xạ khi va chạm (đơn giản hóa)
    public void bounceOff(GameObject other) {
        dirY *= -1;
    }

    // ✅ Các phương thức cần cho GameController
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
