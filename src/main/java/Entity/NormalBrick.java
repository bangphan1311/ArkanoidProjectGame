package Entity;

import javafx.scene.image.Image;

public class NormalBrick extends Brick {
    public NormalBrick(double x, double y, double width, double height, Image image) {
        super(x, y, width, height, image, 1, "Normal");
    }

    @Override
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            destroy();
        }
    }
}