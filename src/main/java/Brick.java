import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick {
    private Rectangle shape;
    private boolean destroyed = false;

    public Brick(double x, double y, double width, double height, Color color) {
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

    public double getX() { return shape.getX(); }
    public double getY() { return shape.getY(); }
    public double getWidth() { return shape.getWidth(); }
    public double getHeight() { return shape.getHeight(); }
}
