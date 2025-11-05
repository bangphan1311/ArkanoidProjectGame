package Entity;

import javafx.scene.image.Image;

public class DoubleBallBrick extends Brick {
    public DoubleBallBrick(double x, double y, double width, double height, Image image) {
        // Gọi constructor của lớp cha, thiết lập máu là 1 và loại là "DoubleBall".
        super(x, y, width, height, image, 1, "DoubleBall");
    }
    @Override
    public void takeHit() {
        this.hitPoints--;
        if (this.hitPoints <= 0) {
            destroy(); // Gọi hàm destroy() của lớp cha
        }
    }
}