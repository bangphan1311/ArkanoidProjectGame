package Entity;

public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public MovableObject(double x, double y, double width, double height){
        super(x, y, width, height);
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void setDirection(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }
}