package GameManager;

import GameManager.BaseGameController;
import Entity.Ball;
import Entity.Brick;
import Entity.NormalBrick;
import Entity.PowerUp;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreUpdateTest {
    private BaseGameController controller;

    @BeforeEach
    void setup() {
        // Khởi tạo JavaFX Platform nếu chưa khởi tạo
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}

        // Khởi tạo controller abstract
        controller = new BaseGameController() {};

        // Khởi tạo các Node cần thiết
        controller.gamePane = new Pane();
        controller.scoreLabel = new Label();
        controller.paddleRect = new Rectangle(100, 20); // Paddle
        controller.ballCircle = new Circle(10);         // Ball

        // Thiết lập vị trí ban đầu
        controller.paddleRect.setLayoutX(200);
        controller.paddleRect.setLayoutY(400);
        controller.ballCircle.setLayoutX(250);
        controller.ballCircle.setLayoutY(390);

        controller.resetPositions();
    }

    @Test
    void testInitialScore() {
        assertEquals(1000, controller.score, "Score ban đầu phải là 1000");
    }

    @Test
    void testAddScoreDirectly() {
        controller.addScore(200);
        assertEquals(1200, controller.score, "Score sau khi addScore 200 phải là 1200");
    }

    @Test
    void testAddScoreUpdatesLabel() {
        controller.addScore(150);
        assertTrue(controller.scoreLabel.getText().contains("1150"), "Label phải cập nhật score mới");
    }

    @Test
    void testBananaPowerUpIncreasesScore() {
        PowerUp banana = new PowerUp(controller.bananaImage, 0, 0, "BANANA");
        banana.getShape().setLayoutX(controller.paddleRect.getLayoutX());
        banana.getShape().setLayoutY(controller.paddleRect.getLayoutY());
        controller.powerUps.add(banana);

        controller.updateAllPowerUps();

        assertEquals(1100, controller.score, "Hứng banana powerup phải tăng 100 điểm");
        assertTrue(banana.isCollected(), "Powerup phải được đánh dấu collected");
    }

    @Test
    void testDoubleScorePowerUpDoublesScore() {
        PowerUp doubleScore = new PowerUp(controller.doubleScoreImage, 0, 0, "DOUBLE_SCORE");
        doubleScore.getShape().setLayoutX(controller.paddleRect.getLayoutX());
        doubleScore.getShape().setLayoutY(controller.paddleRect.getLayoutY());
        controller.powerUps.add(doubleScore);

        controller.updateAllPowerUps();

        assertEquals(2000, controller.score, "DOUBLE_SCORE phải nhân đôi score hiện tại");
        assertTrue(doubleScore.isCollected(), "Powerup phải được đánh dấu collected");
    }

    @Test
    void testMultipleBananas() {
        PowerUp b1 = new PowerUp(controller.bananaImage, controller.paddleRect.getLayoutX(), controller.paddleRect.getLayoutY(), "BANANA");
        PowerUp b2 = new PowerUp(controller.bananaImage, controller.paddleRect.getLayoutX(), controller.paddleRect.getLayoutY(), "BANANA");
        controller.powerUps.add(b1);
        controller.powerUps.add(b2);

        controller.updateAllPowerUps();

        assertEquals(1200, controller.score, "Hứng 2 banana PowerUp tăng 200 điểm");
    }

    @Test
    void testBananaPowerUpOutOfScreenDoesNotAddScore() {
        PowerUp banana = new PowerUp(controller.bananaImage, 0, controller.sceneHeight + 50, "BANANA");
        controller.powerUps.add(banana);

        controller.updateAllPowerUps();

        assertEquals(1000, controller.score, "PowerUp ngoài màn hình không tăng score");
        assertFalse(controller.powerUps.contains(banana), "PoweUp ngoài màn hình phải bị xóa");
    }

    @Test
    void testScoreAfterBrickDestroyed() {
        Image img = new Image(getClass().getResource("/Images/Entity/banana.png").toExternalForm());
        Brick brick = new NormalBrick(0, 0, 50, 20, img);
        controller.bricks.add(brick);

        controller.onBrickHit(brick, controller.balls.get(0));

        assertTrue(brick.isDestroyed(), "Brick phải bị phá hủy");
        assertTrue(controller.score >= 1000, "Score nên tăng sau khi brick bị phá hủy");
    }

    @Test
    void testAddScoreNegativeValue() {
        int oldScore = controller.score;
        controller.addScore(-500);
        assertEquals(oldScore - 500, controller.score, "Score có thể giảm nếu addScore âm");
    }

    @Test
    void testScoreCanBeNegative() {
        int oldScore = controller.score;
        controller.addScore(-2000);
        assertEquals(oldScore - 2000, controller.score, "Score có thể giảm xuống âm");
    }
}
