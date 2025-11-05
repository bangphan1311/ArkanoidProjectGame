package Entity;

import javafx.scene.image.ImageView;

public class MovingObstacle {
    private final ImageView shape;
    private double speed;
    private double direction = 1; // 1 = phải, -1 = trái
    private double minX, maxX;

    public MovingObstacle(ImageView imageView, double sceneWidth, double speed) {
        this.shape = imageView;
        this.speed = speed;

        // Cài biên mặc định
        this.minX = 0;
        this.maxX = sceneWidth - imageView.getFitWidth();
    }

    public void setBounds(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX - shape.getFitWidth();
    }

    public void update() {
        // Di chuyển ngang
        double newX = shape.getLayoutX() + direction * speed;

        // Đảo hướng khi chạm biên
        if (newX < minX) {
            newX = minX;
            direction = 1;
        } else if (newX > maxX) {
            newX = maxX;
            direction = -1;
        }

        shape.setLayoutX(newX);
    }

    // Getter
    public ImageView getShape() {
        return shape;
    }

    public double getX() {
        return shape.getLayoutX();
    }

    public double getY() {
        return shape.getLayoutY();
    }

    public double getWidth() {
        return shape.getFitWidth();
    }

    public double getHeight() {
        return shape.getFitHeight();
    }
}