package Entity;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class Paddle extends MovableObject {
    private Rectangle paddleShape;
    private double speed;
    private double sceneWidth;
    private String currentPowerUp = "None";

    public Paddle(double x, double y, double width, double height, double speed, double sceneWidth) {
        super(x, y, width, height);
        this.speed = speed;
        this.sceneWidth = sceneWidth;

        paddleShape = new Rectangle(x, y, width, height);
        paddleShape.setFill(Color.BLUE);
    }

    public Rectangle getShape() {
        return paddleShape;
    }

    @Override
    public void move() {
    }

    @Override
    public void update() {
        this.x = paddleShape.getX();
        this.y = paddleShape.getY();
    }

    @Override
    public Node render() {
        return paddleShape;
    }

    public void moveLeft() {
        double newX = paddleShape.getX() - speed;
        if (newX >= 0) paddleShape.setX(newX);
        else paddleShape.setX(0);
    }

    public void moveRight() {
        double newX = paddleShape.getX() + speed;
        double paddleWidth = paddleShape.getWidth();
        if (newX + paddleWidth <= sceneWidth)
            paddleShape.setX(newX);
        else
            paddleShape.setX(sceneWidth - paddleWidth);
    }

    public void applyPowerUp(String powerUpType) {
        this.currentPowerUp = powerUpType;
    }

    public String getCurrentPowerUp() {
        return currentPowerUp;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        paddleShape.setX(x);
        paddleShape.setY(y);
    }

}