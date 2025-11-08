package GameManager;

import Entity.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGameController {

    @FXML protected Pane gamePane;
    @FXML protected Pane brickPane;
    @FXML protected Rectangle paddleRect;
    @FXML protected Circle ballCircle;
    @FXML protected ImageView obstacle1;
    @FXML protected Label scoreLabel;

    protected final List<Ball> balls = new ArrayList<>();
    protected final List<Brick> bricks = new ArrayList<>();
    protected int score = 1000;
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    protected AnimationTimer gameLoop;
    protected boolean isGameOver = false; // Chỉ cần 1 cờ
    protected static final long COLLISION_COOLDOWN_NANOS = 40_000_000L;
    protected double sceneWidth = 800;
    protected double sceneHeight = 600;
    protected final double paddleSpeed = 8.0;
    protected final List<MovingObstacle> obstacles = new ArrayList<>();
    protected double obstacleSpeed = 3.5;

    protected Image magneticPowerUpImage;
    private boolean isPaddleMagnetic = false;

    protected final List<PowerUp> powerUps = new ArrayList<>();
    protected Image bananaImage;
    protected Image speedImage;

    protected Image paddleChangeImage;
    protected Image newPaddleImage;
    private Paint originalPaddleFill;
    protected Image doubleScoreImage;

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
                if (paddleRect != null) {
                    originalPaddleFill = paddleRect.getFill();
                }
            } else if (pUrl == null) {
                System.err.println("Không tìm thấy paddle: " + paddlePath);
            }
        } catch (Exception e) {
            System.err.println("Lỗi áp dụng đồ họa: " + e.getMessage());
        }
        Platform.runLater(() -> {
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
            Ball mainBall = new Ball(ballCircle, sceneWidth, sceneHeight);
            mainBall.setPosition(ballCircle.getCenterX(), ballCircle.getCenterY());
            balls.add(mainBall);
        }


        try {
            bananaImage = new Image(getClass().getResource("/Images/banana.png").toExternalForm());

            // 1. Ảnh RƠI cho Tăng tốc (Vd: speedPowerup.jpg)
            speedImage = new Image(getClass().getResource("/Images/PSpeed.png").toExternalForm());

            // 2. Ảnh RƠI cho Đổi Paddle (Tạo 1 ảnh mới, vd: paddle_powerup.png)
            paddleChangeImage = new Image(getClass().getResource("/Images/sp.png").toExternalForm());

            // 3. Ảnh SKIN MỚI của paddle (Vd: paddlepuc.png)
            newPaddleImage = new Image(getClass().getResource("/Images/paddlechange.png").toExternalForm());

            magneticPowerUpImage = new Image(getClass().getResource("/Images/powerupnamcham.png").toExternalForm());
            doubleScoreImage = new Image(getClass().getResource("/Images/xutang2.png").toExternalForm());

        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh power-up: " + e.getMessage());
        }
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + this.score);
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
                if (fxId.contains("obstacle")) {
                    MovingObstacle obs = new MovingObstacle(img, sceneWidth, obstacleSpeed);
                    obs.setBounds(0, sceneWidth);
                    obstacles.add(obs);
                    // Không ẩn (setVisible(false)) và không thêm vào list gạch
                    continue; // Chuyển sang node tiếp theo
                }

                if (fxId.contains("strong")) {
                    brick = new StrongBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage(), damagedImg);
                } else {
                    NormalBrick nb = new NormalBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage());
                    if (fxId.contains("doubleball")) nb.setType("doubleBall"); // Gán type
                    brick = nb;
                }
                brick.setFxId(fxId);
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
        for (MovingObstacle obs : obstacles) {
            obs.update();
        }

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
            if (b.isCaught()) {
                double newBallX = paddleRect.getLayoutX() + b.getCatchOffset();
                b.setPosition(newBallX, paddleRect.getLayoutY() - b.getRadius() - 1);
            }
            else {
                // Nếu bóng không dính, cho nó di chuyển tự do
                b.update();
            }

            handlePaddleCollision(b);
            handleBrickCollisions(b, now);
            handleObstacleCollision(b);


            // ✅ Sửa lỗi "getHeight" bằng cách dùng biến "sceneHeight"
            if (b.getY() - b.getRadius() > this.sceneHeight) {
                toRemove.add(b);
            }
        }
        for (Ball b : toRemove) {
            gamePane.getChildren().remove(b.getShape());
            balls.remove(b);
        }
        updatePowerUps();
        checkLevelComplete();
        checkGameOver();
    }

    private void handlePaddleCollision(Ball ball) {
        Node ballNode = ball.getShape();
        if (ball.getDirY() > 0 && ballNode.getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {

            // --- LOGIC BẮT BÓNG (CỦA BẠN) ---
            if (isPaddleMagnetic && !ball.isCaught()) {
                System.out.println("Bóng đã dính!");
                ball.setCaught(true, paddleRect.getLayoutX());
                ball.setDirX(0);
                ball.setDirY(0);
                double newBallX = paddleRect.getLayoutX() + ball.getCatchOffset();
                ball.setPosition(newBallX, paddleRect.getLayoutY() - ball.getRadius() - 1);

                PauseTransition timer = new PauseTransition(Duration.seconds(2));
                timer.setOnFinished(e -> {
                    System.out.println("Tự động phóng!");
                    ball.setCaught(false, 0);
                    double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
                    double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0);
                    hitOffset = Math.max(-1, Math.min(1, hitOffset));
                    ball.setDirX(hitOffset);
                    ball.setDirY(-1);
                });
                timer.play();
            }
            else if(!ball.isCaught()) {
                ball.reverseY();
                double paddleY = paddleRect.getLayoutY();
                double newY = paddleY - ball.getRadius() - 1;
                ball.setPosition(ball.getX(), newY);
                double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
                double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0);
                hitOffset = Math.max(-1, Math.min(1, hitOffset));
                ball.setDirX(hitOffset);
                ball.addSmallRandomAngle();
            }
        }
    }

    private void handleBrickCollisions(Ball ball, long now) {
        if (now - ball.getLastCollisionTime() < COLLISION_COOLDOWN_NANOS) return;
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            Node ballNode = ball.getShape();
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

                ball.setLastCollisionTime(now);
                onBrickHit(brick, ball);
                break;
            }
        }
    }

    private void handleObstacleCollision(Ball ball) {
        // Lặp qua TẤT CẢ các vật cản trong danh sách
        for (MovingObstacle obs : obstacles) {

            // 1. Kiểm tra va chạm với vật cản 'obs'
            if (ball.getShape().getBoundsInParent().intersects(obs.getShape().getBoundsInParent())) {

                // 2. ✅ DI CHUYỂN TOÀN BỘ LOGIC VÀO ĐÂY
                double ballX = ball.getX();
                double ballY = ball.getY();
                double radius = ball.getRadius();

                // 3. ✅ SỬA LỖI: Dùng 'obs' (vật cản trong vòng lặp), KHÔNG dùng 'movingObstacle'
                double obsX = obs.getX();
                double obsY = obs.getY();
                double obsW = obs.getWidth();
                double obsH = obs.getHeight();

                // (Toàn bộ logic tính toán va chạm của bạn giữ nguyên)
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

                // Cập nhật shape (quan trọng cho bóng phụ)
                if (!balls.isEmpty() && balls.get(0) == ball) {
                    if (ballCircle != null) {
                        ballCircle.setCenterX(ball.getShape().getCenterX());
                        ballCircle.setCenterY(ball.getShape().getCenterY());
                    }
                } else {
                    ball.getShape().setLayoutX(ball.getShape().getLayoutX());
                }

                // 4. Thoát khỏi hàm sau khi xử lý va chạm ĐẦU TIÊN
                return;
            }
        }

        // 5. ❌ PHẦN CODE CŨ BÊN NGOÀI NÀY PHẢI BỊ XÓA
    }

    protected void spawnDoubleBall() {
        if (balls.size() >= 2) return;

        Ball base = balls.get(0);

        Circle extraBallShape = new Circle(base.getShape().getCenterX(), base.getShape().getCenterY(), base.getRadius());
        extraBallShape.setFill(base.getShape().getFill());

        gamePane.getChildren().add(extraBallShape);

        Ball extraBall = new Ball(extraBallShape, sceneWidth, sceneHeight);
        extraBall.setPosition(base.getX(), base.getY());
        extraBall.setDirX(-base.getDirX());
        extraBall.setDirY(base.getDirY());

        balls.add(extraBall);
    }

    public void resetPositions() {
        if (paddleRect != null) {
            paddleRect.setLayoutX(sceneWidth / 2.0 - paddleRect.getWidth() / 2.0);
            paddleRect.setLayoutY(sceneHeight - 40);
        }

        if (ballCircle == null) return;

        ballCircle.setCenterX(sceneWidth / 2.0);
        ballCircle.setCenterY(sceneHeight / 2.0);

        balls.clear();
        Ball newBall = new Ball(ballCircle, sceneWidth, sceneHeight);
        newBall.setPosition(sceneWidth / 2.0, sceneHeight / 2.0);
        newBall.setDirX(0);
        newBall.setDirY(-1);
        balls.add(newBall);

        isGameOver = false;
    }
    protected void onBrickHit(Brick brick, Ball ball) {

        brick.takeHit();

        if (brick.isDestroyed()) {
            gamePane.getChildren().remove(brick.getShape());

            if (brick instanceof StrongBrick || brick instanceof NormalBrick) {
                spawnBanana(brick);
            }

            if ("doubleBall".equals(brick.getType())) {
                spawnDoubleBall();
            }
        }
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

            // ✅ TẠO 2 NHÃN: 1 cho thông báo, 1 cho điểm
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

            Label finalScoreLabel = new Label("Final Score: " + this.score);
            finalScoreLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: gold;");

            // Căn giữa 2 nhãn
            messageLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                messageLabel.setLayoutX(paneWidth / 2 - newBounds.getWidth() / 2);
                messageLabel.setLayoutY(paneHeight / 2 - 50); // Dịch lên trên 1 chút
            });

            finalScoreLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                finalScoreLabel.setLayoutX(paneWidth / 2 - newBounds.getWidth() / 2);
                finalScoreLabel.setLayoutY(paneHeight / 2 + 10); // Dịch xuống dưới 1 chút
            });

            overlay.getChildren().addAll(messageLabel, finalScoreLabel); // Thêm cả 2 nhãn
            gamePane.getChildren().add(overlay);
        });
    }
    protected void spawnPowerUp(Brick brick, String type) {
        Image imageToSpawn = null;
        switch (type) {
            case "BANANA":
                imageToSpawn = bananaImage;
                break;
            case "SPEED_UP":
                imageToSpawn = speedImage;
                break;
            case "PADDLE_CHANGE":
                imageToSpawn = paddleChangeImage;
                break;
            case "MAGNETIC_PADDLE":
                imageToSpawn = magneticPowerUpImage;
                break;
            case "DOUBLE_SCORE":
                imageToSpawn = doubleScoreImage;
                break;
        }
        if (imageToSpawn == null) {
            System.err.println("Không tìm thấy ảnh cho " + type);
            return;
        }

        double x = brick.getShape().getLayoutX() + brick.getShape().getFitWidth() / 2 - 15;
        double y = brick.getShape().getLayoutY() + brick.getShape().getFitHeight() / 2;

        PowerUp item = new PowerUp(imageToSpawn, x, y, type); // Truyền type vào
        powerUps.add(item);
        gamePane.getChildren().add(item.getShape());
    }

    private void updatePowerUps() {
        for (PowerUp item : new ArrayList<>(powerUps)) {
            item.update();

            if (item.getShape().getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {
                item.setCollected();
                gamePane.getChildren().remove(item.getShape());
                switch (item.getType()) {
                    case "BANANA":
                        addScore(100);
                        System.out.println("Hứng được chuối! Điểm: " + score);
                        break;
                    case "SPEED_UP":
                        activateSpeedUp(2);
                        break;
                    case "PADDLE_CHANGE":
                        activatePaddleChange(newPaddleImage, 1.0);
                        break;
                    case "MAGNETIC_PADDLE":
                        activateMagneticPaddle(2.0); // Kích hoạt paddle dính trong 10 giây
                        break;
                    case "DOUBLE_SCORE":
                        System.out.println("EFFECT: Score Doubled!");
                        addScore(this.score);
                        break;
                }
            }
            if (item.isCollected() || item.isOutOfScreen(this.sceneHeight)) {
                powerUps.remove(item);
            }
        }
    }

    protected void activateSpeedUp(double durationSeconds) {
        System.out.println("EFFECT: Ball Speed Up!");
        for (Ball ball : balls) {
            ball.multiplySpeed(2.0);
        }
        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Ball Speed Reset.");
            for (Ball ball : balls) {
                ball.resetSpeed();
            }
        });
        timer.play();
    }

    protected void activatePaddleChange(Image newImage, double durationSeconds) {
        System.out.println("EFFECT: Paddle Change!");
        paddleRect.setFill(new ImagePattern(newImage));

        // Đặt hẹn giờ để trả lại skin cũ
        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Paddle Restored.");
            paddleRect.setFill(originalPaddleFill); // Dùng lại skin gốc đã lưu
        });
        timer.play();
    }
    protected void spawnBanana(Brick brick) {
        if (bananaImage == null) return;

        double x = brick.getShape().getLayoutX() + brick.getShape().getFitWidth() / 2 - 15;
        double y = brick.getShape().getLayoutY() + brick.getShape().getFitHeight() / 2;

        // ✅ SỬA DÒNG NÀY: Thêm "BANANA" làm tham số thứ tư
        PowerUp banana = new PowerUp(bananaImage, x, y, "BANANA");

        powerUps.add(banana);
        gamePane.getChildren().add(banana.getShape());
    }
    protected void addScore(int pointsToAdd) {
        this.score += pointsToAdd; // Cộng điểm

        // Cập nhật Label
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + this.score);
        }
    }
    protected void activateMagneticPaddle(double durationSeconds) {
        System.out.println("EFFECT: Paddle is Magnetic!");
        isPaddleMagnetic = true;

        // Đặt hẹn giờ để tắt hiệu ứng
        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Paddle is normal.");
            isPaddleMagnetic = false;
        });
        timer.play();
    }
}