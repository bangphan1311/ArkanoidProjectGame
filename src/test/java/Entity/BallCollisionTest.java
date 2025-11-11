// kiểm tra va chạm of ball và brick
package Entity;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BallCollisionTest {
    private Ball ball;

    @BeforeEach
    void setup() {
        Circle shape = new Circle(400, 300, 10, Color.RED);
        ball = new Ball(shape, 800, 600);
        ball.setDirX(1);
        ball.setDirY(-1);
    }

    // ball di chuyển
    @Test
    void testBallMoveNormally() {
        double oldX = ball.getShape().getCenterX();
        double oldY = ball.getShape().getCenterY();
        ball.move();
        assertNotEquals(oldX, ball.getShape().getCenterX(), "Bóng phải di chuyển theo trục X");
        assertNotEquals(oldY, ball.getShape().getCenterY(), "Bóng phải di chuyển theo trục Y");
    }

    // ball chạm tường trái
    @Test
    void testBounceOnLeftWall() {
        ball.getShape().setCenterX(5);
        ball.setDirX(-1);
        ball.move();
        assertTrue(ball.getDirX() > 0, "Khi chạm tường trái -> bóng phải bật qua phải");
    }

    // ball chạm tường pk
    @Test
    void testBounceOnRightWall() {
        ball.getShape().setCenterX(795);
        ball.setDirX(1);
        ball.move();
        assertTrue(ball.getDirX() < 0, "Khi chạm tường phải -> bóng phải bật qua trái");
    }

    // ball chạm trần thì bật xg
    @Test
    void testBounceOnTopWall() {
        ball.getShape().setCenterY(5);
        ball.setDirY(-1);
        ball.move();
        assertTrue(ball.getDirY() > 0, "Khi chạm tường trên -> bóng phải bật xuống dưới");
    }

    // thay đổi hướng
    @Test
    void testReflectChangesDirection() {
        double oldX = ball.getDirX();
        double oldY = ball.getDirY();
        ball.reflect(0, 1); // Pxa theo trục Y
        assertNotEquals(oldY, ball.getDirY(), "Sau khi reflect, hướng Y phải thay đổi");
    }

    // hướng là vector đơn vị
    @Test
    void testNormalizeDirectionKeepsUnitVector() {
        ball.setDirX(3);
        ball.setDirY(4);
        // gọi reflect gián tiếp để chuẩn hóa
        ball.reflect(1, 0);
        double length = Math.sqrt(ball.getDirX() * ball.getDirX() + ball.getDirY() * ball.getDirY());
        assertEquals(1.0, length, 0.0001, "Vector hướng phải được chuẩn hóa về độ dài 1");
    }

    // tốc độ
    @Test
    void testSpeedWithinRange() {
        ball.setSpeed(10);
        assertEquals(5, ball.getSpeed(), "Tốc độ không được vượt quá MAX_SPEED");
        ball.setSpeed(0.5);
        assertEquals(2.5, ball.getSpeed(), "Tốc độ không được nhỏ hơn MIN_SPEED");
    }

    // tăng và reset tốc độ
    @Test
    void testMultiplyAndResetSpeed() {
        double original = ball.getSpeed();
        ball.multiplySpeed(1.5);
        double afterMultiply = ball.getSpeed();
        assertTrue(afterMultiply >= original, "multiplySpeed phải làm tăng hoặc không thay đổi tốc độ");
        ball.resetSpeed();
        double afterReset = ball.getSpeed();
        assertTrue(afterReset >= original, "resetSpeed phải khôi phục về tốc độ ban đầu hoặc về MIN_SPEED nếu original nhỏ hơn MIN_SPEED");
        ball.resetSpeed();
        assertEquals(afterReset, ball.getSpeed(), "Gọi resetSpeed nhiều lần không thay đổi tốc độ");
    }

    // bóng dừng khi bị bắt
    @Test
    void testCaughtStopsMovement() {
        ball.setCaught(true, 400);
        double xBefore = ball.getShape().getCenterX();
        double yBefore = ball.getShape().getCenterY();
        ball.update(); // 0 đc di chuyển khi bị bắt
        assertEquals(xBefore, ball.getShape().getCenterX(), "Khi bị bắt, bóng không được di chuyển");
        assertEquals(yBefore, ball.getShape().getCenterY(), "Khi bị bắt, bóng không được di chuyển");
    }

    // hướng chuẩn hóa khi thả bóng
    @Test
    void testUncaughtRestoresDirection() {
        ball.setCaught(true, 400);
        ball.setCaught(false, 400);
        double length = Math.sqrt(ball.getDirX() * ball.getDirX() + ball.getDirY() * ball.getDirY());
        assertEquals(1.0, length, 0.0001, "Khi thả bóng, hướng phải được chuẩn hóa lại");
    }
}
