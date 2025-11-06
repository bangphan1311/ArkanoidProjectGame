package GameManager;

import Entity.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label; // Thêm import
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// ✅ Lớp "abstract" (trừu tượng) làm lớp cha
public abstract class BaseGameController {

    // ✅ "protected" để các lớp con (L1, L2) có thể thấy
    @FXML protected Pane gamePane;
    @FXML protected Pane brickPane;
    @FXML protected Rectangle paddleRect;
    @FXML protected Circle ballCircle;
    @FXML protected ImageView obstacle1;

    // --- Biến logic chung ---
    protected final List<Ball> balls = new ArrayList<>();
    protected final List<Brick> bricks = new ArrayList<>();
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    protected AnimationTimer gameLoop;
    protected boolean isGameOver = false; // Chỉ cần 1 cờ
    protected long lastCollisionTime = 0;
    protected static final long COLLISION_COOLDOWN_NANOS = 40_000_000L;
    protected double sceneWidth = 800;
    protected double sceneHeight = 600;
    protected final double paddleSpeed = 8.0;
    protected MovingObstacle movingObstacle = null;
    protected double obstacleSpeed = 3.5;

    // ❌ ĐÃ XÓA: Toàn bộ logic "v2" (paddle_v2, ball_v2, bricks_v2, v.v.)
    // ❌ ĐÃ XÓA: Toàn bộ logic "nextLevelFile", "isLevelLoading", "showNextLevelScreen", "configureGraphics", "setNextLevel", "applyGraphics"

    /**
     * ✅ HÀM SETUP MỚI (Đơn giản)
     * Được gọi bởi Main.java để áp dụng đồ họa và khởi động game.
     */
    public void setupLevel(String bgPath, String paddlePath) {
        try {
            var bgUrl = getClass().getResource(bgPath);
            if (bgUrl != null && gamePane != null) {
                gamePane.setStyle(String.format("""
                        -fx-background-image: url('%s');
                        -fx-background-repeat: no-repeat;
                        -fx-background-position: center;
                        -fx-background-size: cover;
                        """, bgUrl.toExternalForm()));
            } else if (bgUrl == null) {
                System.err.println("Không tìm thấy background: " + bgPath);
            }

            var pUrl = getClass().getResource(paddlePath);
            if (pUrl != null && paddleRect != null) {
                paddleRect.setFill(new ImagePattern(new Image(pUrl.toExternalForm())));
                paddleRect.setStroke(null);
            } else if (pUrl == null) {
                System.err.println("Không tìm thấy paddle: " + paddlePath);
            }
        } catch (Exception e) {
            System.err.println("Lỗi áp dụng đồ họa: " + e.getMessage());
        }

        // Bắt đầu game
        Platform.runLater(() -> {
            if (obstacle1 != null) {
                double width = (obstacle1.getFitWidth() > 0) ? obstacle1.getFitWidth() : obstacle1.getBoundsInParent().getWidth();
                double sceneW = (gamePane.getWidth() > 0) ? gamePane.getWidth() : sceneWidth;
                movingObstacle = new MovingObstacle(obstacle1, sceneW, obstacleSpeed);
                movingObstacle.setBounds(0, sceneW);
            }
            resetPositions();
            startGameLoop();
            if (gamePane != null) gamePane.requestFocus();
        });
    }

    @FXML
    public void initialize() {
        if (gamePane != null) {
            if (gamePane.getPrefWidth() > 0) sceneWidth = gamePane.getPrefWidth();
            if (gamePane.getPrefHeight() > 0) sceneHeight = gamePane.getPrefHeight();
        }
        if (ballCircle != null) {
            Ball mainBall = new Ball(ballCircle.getRadius(), sceneWidth, sceneHeight);
            mainBall.setPosition(ballCircle.getCenterX(), ballCircle.getCenterY());
            balls.add(mainBall);
        }
        resetPositions();
        setupControls();
        loadBricksFromPane();
    }
    private void setupControls() {
        if (gamePane == null) return;
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight = true;
        });
        gamePane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight = false;
        });
    }

    private void loadBricksFromPane() {
        if (brickPane == null) return;
        Image damagedImg = null;
        try {
            var damagedUrl = getClass().getResource("/Images/brick_strong_damaged.png");
            if (damagedUrl != null) damagedImg = new Image(damagedUrl.toExternalForm());
        } catch (Exception e) { System.err.println("Không load được ảnh brick damaged: " + e.getMessage()); }

        for (var node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof ImageView img) {
                String fxId = img.getId() != null ? img.getId().toLowerCase() : "";
                Brick brick;
                if (fxId.contains("obstacle")) continue;

                if (fxId.contains("strong")) {
                    brick = new StrongBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage(), damagedImg);
                } else {
                    NormalBrick nb = new NormalBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage());
                    if (fxId.contains("doubleball")) nb.setType("doubleBall"); // Gán type
                    brick = nb;
                }
                bricks.add(brick);
                gamePane.getChildren().add(brick.getShape());
                img.setVisible(false);
            }
        }
    }

    public void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
            }
        };
        gameLoop.start();
    }

    public void stopGameLoop() {
        if (gameLoop != null) gameLoop.stop();
    }

    private void update(long now) {
        if (isGameOver) return; // Dừng nếu đã thắng/thua
        if (movingObstacle != null) movingObstacle.update();

        if (moveLeft) {
            double newX = paddleRect.getLayoutX() - paddleSpeed;
            if (newX < 0) newX = 0;
            paddleRect.setLayoutX(newX);
        }
        if (moveRight) {
            double newX = paddleRect.getLayoutX() + paddleSpeed;
            if (newX + paddleRect.getWidth() > sceneWidth) newX = sceneWidth - paddleRect.getWidth();
            paddleRect.setLayoutX(newX);
        }

        List<Ball> toRemove = new ArrayList<>();
        for (Ball b : new ArrayList<>(balls)) {
            b.update();
            if (!balls.isEmpty() && b == balls.get(0) && ballCircle != null) {
                ballCircle.setCenterX(b.getShape().getCenterX());
                ballCircle.setCenterY(b.getShape().getCenterY());
            } else {
                if (!gamePane.getChildren().contains(b.getShape())) {
                    gamePane.getChildren().add(b.getShape());
                }
            }
            handlePaddleCollision(b);
            handleBrickCollisions(b, now);
            if (movingObstacle != null) handleObstacleCollision(b);

            // ✅ Sửa lỗi "getHeight" bằng cách dùng biến "sceneHeight"
            if (b.getY() - b.getRadius() > this.sceneHeight) {
                toRemove.add(b);
            }
        }
        for (Ball b : toRemove) {
            gamePane.getChildren().remove(b.getShape());
            balls.remove(b);
        }
        checkLevelComplete();
        checkGameOver();
    }

    private void handlePaddleCollision(Ball ball) {
        Node ballNode = (balls.size() > 0 && ball == balls.get(0)) ? ballCircle : ball.getShape();
        if (ball.getDirY() > 0 && ballNode.getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {
            ball.reverseY();
            double paddleY = paddleRect.getLayoutY();
            double newY = paddleY - ball.getRadius() - 1;
            ball.setPosition(ball.getX(), newY);
            if (ballNode == ballCircle) ballCircle.setCenterY(newY);
            else ball.getShape().setLayoutY(newY);

            double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
            double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0);
            hitOffset = Math.max(-1, Math.min(1, hitOffset));
            ball.setDirX(hitOffset);
            ball.addSmallRandomAngle();
        }
    }

    private void handleBrickCollisions(Ball ball, long now) {
        if (now - lastCollisionTime < COLLISION_COOLDOWN_NANOS) return;
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            Node ballNode = (balls.size() > 0 && ball == balls.get(0)) ? ballCircle : ball.getShape();
            if (ballNode.getBoundsInParent().intersects(r.getBoundsInParent())) {
                double ballX = ball.getX(), ballY = ball.getY(), radius = ball.getRadius();
                double brickX = r.getLayoutX(), brickY = r.getLayoutY();
                double brickW, brickH;
                if (r instanceof ImageView iv) { brickW = iv.getFitWidth(); brickH = iv.getFitHeight(); }
                else if (r instanceof Rectangle rect) { brickW = rect.getWidth(); brickH = rect.getHeight(); }
                else { brickW = r.getBoundsInParent().getWidth(); brickH = r.getBoundsInParent().getHeight(); }

                double overlapLeft = (ballX + radius) - brickX, overlapRight = (brickX + brickW) - (ballX - radius);
                double overlapTop = (ballY + radius) - brickY, overlapBottom = (brickY + brickH) - (ballY - radius);
                double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

                if (minOverlap == overlapLeft) ball.reflect(-1, 0);
                else if (minOverlap == overlapRight) ball.reflect(1, 0);
                else if (minOverlap == overlapTop) ball.reflect(0, -1);
                else ball.reflect(0, 1);

                lastCollisionTime = now;
                onBrickHit(brick, ball);
                break;
            }
        }
    }

    private void handleObstacleCollision(Ball ball) {
        if (movingObstacle == null) return;
        if (!ball.getShape().getBoundsInParent().intersects(movingObstacle.getShape().getBoundsInParent()))
            return;

        double ballX = ball.getX();
        double ballY = ball.getY();
        double radius = ball.getRadius();

        double obsX = movingObstacle.getX();
        double obsY = movingObstacle.getY();
        double obsW = movingObstacle.getWidth();
        double obsH = movingObstacle.getHeight();

        double overlapLeft = (ballX + radius) - obsX;
        double overlapRight = (obsX + obsW) - (ballX - radius);
        double overlapTop = (ballY + radius) - obsY;
        double overlapBottom = (obsY + obsH) - (ballY - radius);

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapLeft) ball.reflect(-1, 0);
        else if (minOverlap == overlapRight) ball.reflect(1, 0);
        else if (minOverlap == overlapTop) ball.reflect(0, -1);
        else ball.reflect(0, 1);

        double newX = ballX;
        double newY = ballY;
        if (minOverlap == overlapLeft) newX = obsX - radius - 1;
        else if (minOverlap == overlapRight) newX = obsX + obsW + radius + 1;
        else if (minOverlap == overlapTop) newY = obsY - radius - 1;
        else newY = obsY + obsH + radius + 1;

        ball.setPosition(newX, newY);

        if (!balls.isEmpty() && balls.get(0) == ball) {
            if (ballCircle != null) {
                ballCircle.setCenterX(ball.getShape().getCenterX());
                ballCircle.setCenterY(ball.getShape().getCenterY());
            }
        } else {
            // ensure layout updated for extra ball shape
            ball.getShape().setLayoutX(ball.getShape().getLayoutX());
        }
    }

    protected void spawnDoubleBall() {
        if (balls.size() >= 2) return;
        Ball base = balls.get(0);
        Ball extraBall = new Ball(base.getRadius(), sceneWidth, sceneHeight);
        extraBall.setPosition(base.getX(), base.getY());
        extraBall.setDirX(-base.getDirX());
        extraBall.setDirY(base.getDirY());
        balls.add(extraBall);
        gamePane.getChildren().add(extraBall.getShape());
    }

    public void resetPositions() {
        if (paddleRect != null) {
            paddleRect.setLayoutX(sceneWidth / 2.0 - paddleRect.getWidth() / 2.0);
            paddleRect.setLayoutY(sceneHeight - 40);
        }
        if (ballCircle != null) {
            ballCircle.setCenterX(sceneWidth / 2.0);
            ballCircle.setCenterY(sceneHeight / 2.0);
        }
        balls.clear();
        Ball newBall = new Ball(ballCircle != null ? ballCircle.getRadius() : 12, sceneWidth, sceneHeight);
        newBall.setPosition(sceneWidth / 2.0, sceneHeight / 2.0);
        balls.add(newBall);
        isGameOver = false;
    }
    protected void onBrickHit(Brick brick, Ball ball) {
        brick.takeHit();
        if (brick.isDestroyed() && "doubleBall".equals(brick.getType())) {
            spawnDoubleBall();
        }
        //  cho "2xMoney"
    }

    private void checkLevelComplete() {
        if (isGameOver) return;
        boolean allDestroyed = bricks.stream().allMatch(Brick::isDestroyed);
        if (allDestroyed) {
            isGameOver = true;
            gameLoop.stop();
            showGameEndScreen("YOU WIN!");
        }
    }

    private void checkGameOver() {
        if (isGameOver) return;
        if (balls.isEmpty()) {
            System.out.println(" Tất cả bóng đã rơi!");
            isGameOver = true;
            gameLoop.stop();
            showGameEndScreen("GAME OVER");
        }
    }

    private void showGameEndScreen(String message) {
        Platform.runLater(() -> {
            double paneWidth = gamePane.getPrefWidth();
            double paneHeight = gamePane.getPrefHeight();

            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
            overlay.setPrefSize(paneWidth, paneHeight);

            Label label = new Label(message);
            label.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

            label.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                label.setLayoutX(paneWidth / 2 - newBounds.getWidth() / 2);
                label.setLayoutY(paneHeight / 2 - newBounds.getHeight() / 2);
            });

            overlay.getChildren().add(label);
            gamePane.getChildren().add(overlay);
        });
    }
}