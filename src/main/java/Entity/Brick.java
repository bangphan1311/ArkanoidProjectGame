package Entity;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends GameObject {
    private Rectangle shape;
    private boolean destroyed = false;

    private int hitPoints = 1;
    private String type = "Normal";

    public Brick(double x, double y, double width, double height, Color color) {
        super(x, y, width, height);
        shape = new Rectangle(x, y, width, height);
        shape.setFill(color);
        shape.setStroke(Color.BLACK);
    }

    public Rectangle getShape() {
        return shape;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
        shape.setVisible(false);
    }

    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) destroy();
    }

    @Override
    public void update() {
    }

    @Override
    public Node render() {
        return shape;
    }
}
