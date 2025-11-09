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

import java.util.ArrayList;
import java.util.List;

import GameManager.Menu.HighScoresController;

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

    protected boolean ballLaunched = false; // bóng chx chạy

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
    protected Image slowDownImage;
    protected Image bombPowerUpImage;
    protected Image enlargePowerUpImage;
    private double originalPaddleWidth = -1;
    private boolean isPaddleAltered = false;

    protected final List<ImageView> staticWalls = new ArrayList<>();

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
            bananaImage = new Image(getClass().getResource("/Images/Entity/banana.png").toExternalForm());

            // 1. Ảnh RƠI cho Tăng tốc
            speedImage = new Image(getClass().getResource("/Images/PowerUp/PSpeed.png").toExternalForm());

            // 2. Ảnh RƠI cho Đổi Paddle
            paddleChangeImage = new Image(getClass().getResource("/Images/Entity/sp.png").toExternalForm());

            // 3. Ảnh SKIN MỚI của paddle
            newPaddleImage = new Image(getClass().getResource("/Images/PowerUp/paddlechange.png").toExternalForm());

            magneticPowerUpImage = new Image(getClass().getResource("/Images/PowerUp/powerupnamcham.png").toExternalForm());
            doubleScoreImage = new Image(getClass().getResource("/Images/PowerUp/xutang2.png").toExternalForm());
            slowDownImage = new Image(getClass().getResource("/Images/PowerUp/sloww.png").toExternalForm());
            bombPowerUpImage = new Image(getClass().getResource("/Images/PowerUp/bombepup.png").toExternalForm());
            enlargePowerUpImage = new Image(getClass().getResource("/Images/PowerUp/pupdaira.png").toExternalForm());

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
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) moveLeft = true;
            else if (code == KeyCode.RIGHT || code == KeyCode.D) moveRight = true;

            // Ấn SPACE hoặc ENTER để phóng bóng
            if ((code == KeyCode.SPACE || code == KeyCode.ENTER) && !ballLaunched) {
                launchBall();
            }
        });

        // giữu paddle ko nhả phím
        gamePane.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) moveLeft = false;
            else if (code == KeyCode.RIGHT || code == KeyCode.D) moveRight = false;
        });

        // --- Đảm bảo luôn có focus để nhận phím ---
        Platform.runLater(() -> gamePane.requestFocus());
    }


    private void loadBricksFromPane() {
        if (brickPane == null) return;
        Image damagedImg = null;
        try {
            var damagedUrl = getClass().getResource("/Images/Entity/brick_strong_damaged.png");
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

                    continue;
                }
                else if (fxId.contains("wall")) {
                    staticWalls.add(img);

                    continue;
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
        if (isGameOver) return;
        // bóng chx chạy thì bóng đi theo paddle
        if (!ballLaunched && !balls.isEmpty()) {
            Ball ball = balls.get(0);

            // vgtri bong đứng im
            double ballX = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
            double ballY = paddleRect.getLayoutY() - ball.getRadius() - 2;


            ball.setPosition(ballX, ballY);
            return; // chưa update vật lý khi chưa phóng
        }

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

                b.update();
            }

            handlePaddleCollision(b);
            handleBrickCollisions(b, now);
            handleObstacleCollision(b);
            handleWallCollision(b, now);



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
        for (MovingObstacle obs : obstacles) {
            if (ball.getShape().getBoundsInParent().intersects(obs.getShape().getBoundsInParent())) {
                double ballX = ball.getX();
                double ballY = ball.getY();
                double radius = ball.getRadius();

                double obsX = obs.getX();
                double obsY = obs.getY();
                double obsW = obs.getWidth();
                double obsH = obs.getHeight();
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
                    ball.getShape().setLayoutX(ball.getShape().getLayoutX());
                }
                return;
            }
        }
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
        balls.add(newBall);
        resetBallPosition();

        isGameOver = false;

        // dbao focus khi reset lại
        Platform.runLater(() -> gamePane.requestFocus());
    }


    // chỉnh bóng chạy
    protected void launchBall() {
        if (balls.isEmpty()) return;
        Ball ball = balls.get(0);
        ballLaunched = true;
        ball.setDirX(0.7);   // góc ban đầu hơi lệch
        ball.setDirY(-1);    // hướng lên
    }

    protected void resetBallPosition() {
        if (balls.isEmpty()) return;
        Ball ball = balls.get(0);
        ballLaunched = false;
        ball.setDirX(0);
        ball.setDirY(0);
        ball.setPosition(
                paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0,
                paddleRect.getLayoutY() + paddleRect.getHeight() - ball.getRadius() - 2
        );

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
            case "SLOW_DOWN": imageToSpawn = slowDownImage;
                break;
            case "BOMB": imageToSpawn = bombPowerUpImage;
                break;
            case "ENLARGE_PADDLE": imageToSpawn = enlargePowerUpImage;
                break;
        }
        if (imageToSpawn == null) {
            System.err.println("Không tìm thấy ảnh cho " + type);
            return;
        }

        double x = brick.getShape().getLayoutX() + brick.getShape().getFitWidth() / 2 - 15;
        double y = brick.getShape().getLayoutY() + brick.getShape().getFitHeight() / 2;

        PowerUp item = new PowerUp(imageToSpawn, x, y, type);
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
                        activateMagneticPaddle(2.0);
                        break;
                    case "DOUBLE_SCORE":
                        System.out.println("EFFECT: Score Doubled!");
                        addScore(this.score);
                        break;
                    case "SLOW_DOWN":
                        activateSlowDown(4);
                        break;
                    case "BOMB":
                        activateBomb();
                        break;
                    case "ENLARGE_PADDLE":
                        activateEnlargePaddle(5.0);
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
        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Paddle Restored.");
            paddleRect.setFill(originalPaddleFill);
        });
        timer.play();
    }
    protected void spawnBanana(Brick brick) {
        if (bananaImage == null) return;

        double x = brick.getShape().getLayoutX() + brick.getShape().getFitWidth() / 2 - 15;
        double y = brick.getShape().getLayoutY() + brick.getShape().getFitHeight() / 2;
        PowerUp banana = new PowerUp(bananaImage, x, y, "BANANA");

        powerUps.add(banana);
        gamePane.getChildren().add(banana.getShape());
    }
    protected void addScore(int pointsToAdd) {
        this.score += pointsToAdd;

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
    protected void activateSlowDown(double durationSeconds) {
        System.out.println("EFFECT: Ball Slow Down!");
        for (Ball ball : balls) {
            ball.multiplySpeed(0.5);
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
    protected void activateBomb() {
        System.out.println("EFFECT: BOMB ACTIVATED! ");
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick instanceof StrongBrick && !brick.isDestroyed()) {
                brick.destroy();
                gamePane.getChildren().remove(brick.getShape());
            }
        }
    }
    protected void activateEnlargePaddle(double durationSeconds) {
        if (isPaddleAltered) return;

        System.out.println("EFFECT: Paddle Enlarged!");
        isPaddleAltered = true;
        originalPaddleWidth = paddleRect.getWidth();
        paddleRect.setWidth(originalPaddleWidth * 3);

        PauseTransition timer = new PauseTransition(Duration.seconds(durationSeconds));
        timer.setOnFinished(event -> {
            System.out.println("EFFECT: Paddle Restored.");
            paddleRect.setWidth(originalPaddleWidth);
            isPaddleAltered = false;
        });
        timer.play();
    }
    private void handleWallCollision(Ball ball, long now) {

        for (ImageView wall : staticWalls) {
            Node ballNode = ball.getShape();

            if (ballNode.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                double ballX = ball.getX(), ballY = ball.getY(), radius = ball.getRadius();
                double brickX = wall.getLayoutX(), brickY = wall.getLayoutY();
                double brickW = wall.getFitWidth(), brickH = wall.getFitHeight();

                double overlapLeft = (ballX + radius) - brickX, overlapRight = (brickX + brickW) - (ballX - radius);
                double overlapTop = (ballY + radius) - brickY, overlapBottom = (brickY + brickH) - (ballY - radius);
                double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

                if (minOverlap == overlapLeft) ball.reflect(-1, 0);
                else if (minOverlap == overlapRight) ball.reflect(1, 0);
                else if (minOverlap == overlapTop) ball.reflect(0, -1);
                else ball.reflect(0, 1);

                break;
            }
        }
    }

    private void showGameEndScreen(String message) {
        Platform.runLater(() -> {
            double paneWidth = gamePane.getPrefWidth();
            double paneHeight = gamePane.getPrefHeight();

            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
            overlay.setPrefSize(paneWidth, paneHeight);

            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

            Label finalScoreLabel = new Label("Final Score: " + this.score);
            finalScoreLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: gold;");

            messageLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                messageLabel.setLayoutX(paneWidth / 2 - newBounds.getWidth() / 2);
                messageLabel.setLayoutY(paneHeight / 2 - 50);
            });

            finalScoreLabel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                finalScoreLabel.setLayoutX(paneWidth / 2 - newBounds.getWidth() / 2);
                finalScoreLabel.setLayoutY(paneHeight / 2 + 10);
            });

            overlay.getChildren().addAll(messageLabel, finalScoreLabel);
            gamePane.getChildren().add(overlay);

            // thêm ghi điểm
            try {
                HighScoresController highScoreCtrl = new HighScoresController();
                String playerName = "PLAYER"; // có thể sửa thành input từ người chơi
                highScoreCtrl.saveScore(playerName, this.score);
                System.out.println("Điểm đã lưu: " + this.score);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}