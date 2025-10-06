import javafx.scene.shape.Rectangle;
public class paddle {
    private Rectangle paddleShape;
    private double speed;
    private double sceneWidth;

    public paddle(double x, double y, double width, double height,
                  double speed, double sceneWidth) {
        this.paddleShape = new Rectangle(x, y, width, height);
        this.speed = speed;
        this.sceneWidth = sceneWidth;

    }

    public Rectangle getShape() {
        return paddleShape;
    }

    public void moveLeft() {
        double newX = this.paddleShape.getX() - speed;
        if (newX >= 0) {
            this.paddleShape.setX(newX);
        } else {
            this.paddleShape.setX(0);
        }
    }

    public void moveRight() {
        double newX = this.paddleShape.getX() + speed;
        double paddleWidth = this.paddleShape.getWidth();
        if ( newX + paddleWidth <= this.sceneWidth) {
            this.paddleShape.setX(newX);

        } else{
            this.paddleShape.setX(sceneWidth-paddleWidth);
        }

    }

    public double getX() {
        return this.paddleShape.getX();
    }
    public double getY() {
        return this.paddleShape.getY();
    }
    public double getWidth() {
        return this.paddleShape.getWidth();

    }
    public double getHeight() {
        return this.paddleShape.getHeight();
    }


}
