package Entity;

import javafx.scene.image.Image;

public class StrongBrick extends Brick {
    public StrongBrick(double x, double y, double width, double height, Image normalImage, Image damagedImage) {
        super(x, y, width, height, normalImage, 2, "Strong");
        this.damagedImage = damagedImage;
    }

    @Override
    public void takeHit() {
        hitPoints--;

        if (hitPoints == 1) {
            if (damagedImage != null) {
                imageView.setImage(damagedImage);
            }
        } else if (hitPoints <= 0) {
            destroy();
        }
    }
}