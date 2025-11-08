package Entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

/**
 * Lớp đại diện cho một vật phẩm (Power-up) rơi xuống.
 * Nâng cấp để chứa "type" (loại vật phẩm).
 */
public class PowerUp {

    private ImageView imageView;
    private double speedY = 1.5; // Tốc độ rơi
    private boolean isCollected = false;
    private String type; // Loại vật phẩm: "BANANA", "SPEED_UP", "PADDLE_CHANGE"

    public PowerUp(Image image, double x, double y, String type) {
        this.imageView = new ImageView(image);
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
        this.imageView.setFitWidth(60); // Bạn có thể đổi kích thước ở đây
        this.imageView.setFitHeight(60);
        this.type = type; // Lưu loại vật phẩm
    }

    public void update() {
        imageView.setLayoutY(imageView.getLayoutY() + speedY);
    }

    public Node getShape() {
        return imageView;
    }

    public boolean isOutOfScreen(double screenHeight) {
        return imageView.getLayoutY() > screenHeight;
    }

    public void setCollected() {
        this.isCollected = true;
    }

    public boolean isCollected() {
        return isCollected;
    }

    // Hàm mới để BaseGameController biết cần làm gì
    public String getType() {
        return type;
    }
}