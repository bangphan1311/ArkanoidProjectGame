//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball {
    private Circle ballShape;
    private double dirX = (double)1.0F;
    private double dirY = (double)1.0F;
    private double speed = (double)3.0F;
    private double sceneWidth;
    private double sceneHeight;

    public Ball(double radius, double sceneWidth, double sceneHeight) {
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.ballShape = new Circle(radius, Color.RED);
        this.ballShape.setCenterX(sceneWidth / (double)2.0F);
        this.ballShape.setCenterY(sceneHeight / (double)2.0F);
    }

    public Circle getShape() {
        return this.ballShape;
    }

    public void update() {
        double newX = this.ballShape.getCenterX() + this.dirX * this.speed;
        double newY = this.ballShape.getCenterY() + this.dirY * this.speed;
        if (newX - this.ballShape.getRadius() <= (double)0.0F || newX + this.ballShape.getRadius() >= this.sceneWidth) {
            this.dirX *= (double)-1.0F;
        }

        if (newY - this.ballShape.getRadius() <= (double)0.0F || newY + this.ballShape.getRadius() >= this.sceneHeight) {
            this.dirY *= (double)-1.0F;
        }

        this.ballShape.setCenterX(newX);
        this.ballShape.setCenterY(newY);
    }
}
