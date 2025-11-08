package Entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Brick {
    protected ImageView imageView;
    protected boolean destroyed = false;
    protected int hitPoints;
    public String type;
    protected Image normalImage;
    protected Image damagedImage;
    protected String fxId;

    public Brick(double x, double y, double width, double height, Image image, int hitPoints, String type) {
        this.normalImage = image;
        this.hitPoints = hitPoints;
        this.type = type;

        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setLayoutX(x);
        this.imageView.setLayoutY(y);
    }

    public ImageView getShape() {
        return imageView;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * SỬA ĐỔI: Thay đổi kiểu trả về từ void thành String.
     * Bây giờ, khi một viên gạch bị phá hủy, nó sẽ trả về loại của nó.
     * @return Loại của viên gạch (ví dụ: "Normal", "Strong", "DoubleBall") hoặc null nếu đã bị phá hủy từ trước.
     */
    public String destroy() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.imageView.setVisible(false);
            return this.type; // Trả về loại gạch như một tín hiệu
        }
        return null;
    }

    public abstract void takeHit();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFxId() {
        return fxId;
    }

    public void setFxId(String fxId) {
        this.fxId = fxId;
    }
    public int getHitPoints() {
        return this.hitPoints;
    }
}