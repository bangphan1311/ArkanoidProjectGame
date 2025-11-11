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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import GameManager.Level.GameOverController;
import javafx.scene.media.AudioClip;
import GameManager.Menu.MapController;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javafx.animation.ScaleTransition;
import javafx.animation.Animation;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import GameManager.SoundManager;

public abstract class BaseGameController {

    @FXML
    protected Pane gamePane;
    @FXML
    protected Pane brickPane;
    @FXML
    protected Rectangle paddleRect;
    @FXML
    protected Circle ballCircle;
    @FXML
    protected ImageView obstacle1;
    @FXML
    protected Label scoreLabel;
    @FXML
    protected Button pauseButton;
    @FXML
    protected ImageView pauseIcon;
    @FXML
    protected Pane pauseOverlay;

    protected boolean isPaused = false;
    protected Image pauseIconImage;
    protected Image playIconImage;

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

    protected AudioClip hitSound;
    protected AudioClip breakSound;
    protected AudioClip powerUpSound;
    protected AudioClip loseBallSound;
    protected AudioClip bananaSound;

    private ExecutorService audioExecutor = Executors.newCachedThreadPool();

    protected final List<ImageView> staticWalls = new ArrayList<>();

    protected Image paddleChangeImage;
    protected Image newPaddleImage;
    private Paint originalPaddleFill;
    protected Image doubleScoreImage;

    // mạng
    protected int lives = 3;
    protected final List<ImageView> heartIcons = new ArrayList<>();
    protected Image heartImage;


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

        //reset lại mạng mỗi khi qua level
        lives = 3;

        // Xóa tim cũ
        for (ImageView heart : heartIcons) {
            gamePane.getChildren().remove(heart);
        }
        heartIcons.clear();

        // hthi lại 3 mạng mới khi qua level khác
        Platform.runLater(this::setupHearts);
        if (pauseButton != null) {
            pauseButton.setOnAction(e -> togglePause());
        }

    }

    @FXML
    public void initialize() {
        System.out.println("--- KIỂM TRA NÚT PAUSE ---");
        System.out.println("pauseButton (Nút bấm): " + pauseButton);
        System.out.println("pauseIcon (Icon vuông/tam giác): " + pauseIcon);
        System.out.println("pauseOverlay (Màn hình mờ): " + pauseOverlay);
        if (gamePane != null) {
            if (gamePane.getPrefWidth() > 0) sceneWidth = gamePane.getPrefWidth();
            if (gamePane.getPrefHeight() > 0) sceneHeight = gamePane.getPrefHeight();
        }
        if (ballCircle != null) {
            Ball mainBall = new Ball(ballCircle, sceneWidth, sceneHeight);
            mainBall.setPosition(ballCircle.getCenterX(), ballCircle.getCenterY());
            balls.add(mainBall);
        }

        ///  load các ảnh power up
        try {
            bananaImage = new Image(getClass().getResource("/Images/Entity/banana.png").toExternalForm());
            speedImage = new Image(getClass().getResource("/Images/PowerUp/PSpeed.png").toExternalForm());
            paddleChangeImage = new Image(getClass().getResource("/Images/Entity/sp.png").toExternalForm());
            newPaddleImage = new Image(getClass().getResource("/Images/PowerUp/padchange.png").toExternalForm());
            magneticPowerUpImage = new Image(getClass().getResource("/Images/PowerUp/powerupnamcham.png").toExternalForm());
            doubleScoreImage = new Image(getClass().getResource("/Images/PowerUp/xutang2.png").toExternalForm());
            slowDownImage = new Image(getClass().getResource("/Images/PowerUp/sloww.png").toExternalForm());
            bombPowerUpImage = new Image(getClass().getResource("/Images/PowerUp/bombepup.png").toExternalForm());
            enlargePowerUpImage = new Image(getClass().getResource("/Images/PowerUp/pupdaira.png").toExternalForm());
            pauseIconImage = new Image(getClass().getResource("/Images/Page/thu1.png").toExternalForm());
            playIconImage = new Image(getClass().getResource("/Images/Page/thu2.png").toExternalForm());
            if (pauseIcon != null) {
                pauseIcon.setImage(pauseIconImage); // Đặt ảnh mặc định
            }

        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh power-up: " + e.getMessage());
        }
        loadSounds();

        // mạng
        try {
            heartImage = new Image(getClass().getResource("/Images/Entity/tym.png").toExternalForm());
            setupHearts(); // htih 3 mạng
        } catch (Exception e) {
            System.err.println("Không tải được ảnh : " + e.getMessage());
        }

        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + this.score);
        }
        if (pauseOverlay != null) {
            pauseOverlay.setMouseTransparent(true);
        }


        resetPositions();
        setupControls();
        loadBricksFromPane();
    }

    private void setupControls() {
        if (gamePane == null) return;
        ///  xử lý các phím
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (isPaused) return;
            if (code == KeyCode.LEFT || code == KeyCode.A) moveLeft = true;
            else if (code == KeyCode.RIGHT || code == KeyCode.D) moveRight = true;

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

        // dbao focus luôn nhân phím
        Platform.runLater(() -> gamePane.requestFocus());
    }


    private void loadBricksFromPane() {
        if (brickPane == null) return;
        ///  xử lý strong brick riêng
        Image damagedImg = null;
        try {
            var damagedUrl = getClass().getResource("/Images/Entity/brick_strong_damaged.png");
            if (damagedUrl != null) damagedImg = new Image(damagedUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Không load được ảnh brick damaged: " + e.getMessage());
        }
        ///  duyệt các iamge view và phân biệt nó qua id

        for (var node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof ImageView img) {
                String fxId = img.getId() != null ? img.getId().toLowerCase() : "";
                Brick brick;
                if (fxId.contains("obstacle")) {
                    MovingObstacle obs = new MovingObstacle(img, sceneWidth, obstacleSpeed);
                    obs.setBounds(0, sceneWidth);
                    obstacles.add(obs);

                    continue;
                } else if (fxId.contains("wall")) {
                    staticWalls.add(img);

                    continue;
                }

                if (fxId.contains("strong")) {
                    brick = new StrongBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage(), damagedImg);
                } else {
                    NormalBrick nb = new NormalBrick(img.getLayoutX(), img.getLayoutY(), img.getFitWidth(), img.getFitHeight(), img.getImage());
                    if (fxId.contains("doubleball")) nb.setType("doubleBall");
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
        ///  60 FPS
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


    // paddle vs bóng khi bắt đầu
    private void update(long now) {
        if (isGameOver || isPaused) return;

        // di chuyển paddle trc
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

        // bóng chx chạy - di chuyển theo paddle
        if (!ballLaunched && !balls.isEmpty()) {
            Ball ball = balls.get(0);

            double ballX = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
            double ballY = paddleRect.getLayoutY() - ball.getRadius() - 2;

            ball.setPosition(ballX, ballY);
            return; // chưa update vật lý khi chưa phóng
        }

        // update vật lý bthg
        for (MovingObstacle obs : obstacles) {
            obs.update();
        }

        List<Ball> toRemove = new ArrayList<>();
        ///  xử lý va chạm tất cả các bóng khi đc tạo ra từ power up nhân đôi bóng
        for (Ball b : new ArrayList<>(balls)) {
            double nextX = b.getShape().getCenterX() + b.getDirX() * b.getSpeed();
            double nextY = b.getShape().getCenterY() + b.getDirY() * b.getSpeed();
            if (nextX - b.getRadius() <= 0 || nextX + b.getRadius() >= sceneWidth || nextY - b.getRadius() <= 0) {
                playSound(hitSound);
            }
            if (b.isCaught()) {
                double newBallX = paddleRect.getLayoutX() + b.getCatchOffset();
                b.setPosition(newBallX, paddleRect.getLayoutY() - b.getRadius() - 1);
            } else {
                b.update();
            }

            ///  quản lý mọi va chạm bóng
            handlePaddleCollision(b);
            handleBrickCollisions(b, now);
            handleObstacleCollision(b);
            handleWallCollision(b, now);


            if (b.getY() - b.getRadius() > this.sceneHeight) {
                toRemove.add(b);
                ///  đi ra khỏi màn hình
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
            playSound(hitSound);

            ///  xử lý riêng tính năng bóng dính cho powerup
            if (isPaddleMagnetic && !ball.isCaught()) {
                System.out.println("Bóng đã dính!");
                ball.setCaught(true, paddleRect.getLayoutX());
                ball.setDirX(0); // chặn mọi chuyển dộngd vật lý
                ball.setDirY(0);
                double newBallX = paddleRect.getLayoutX() + ball.getCatchOffset(); //vị trí ngang mới
                ball.setPosition(newBallX, paddleRect.getLayoutY() - ball.getRadius() - 1);// nằm trên paddle

                PauseTransition timer = new PauseTransition(Duration.seconds(2)); // vất vào luồng xử lý riêng
                // sau 2s giữ bóng, thả bóng
                timer.setOnFinished(e -> {
                    ball.setCaught(false, 0);// thả
                    ///  xử lý lại vị trí bóng khi nó thả
                    double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
                    double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0); // chuẩn hóa
                    hitOffset = Math.max(-1, Math.min(1, hitOffset));
                    ball.setDirX(hitOffset);
                    ball.setDirY(-1);
                });
                timer.play();
            } /// nêú kh dinhs thì set vị trí, vận tốc bình thường
            else if (!ball.isCaught()) {
                ball.reverseY();
                double paddleY = paddleRect.getLayoutY();
                double newY = paddleY - ball.getRadius() - 1;
                ball.setPosition(ball.getX(), newY);
                double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
                double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0);
                hitOffset = Math.max(-1, Math.min(1, hitOffset));
                ball.setDirX(hitOffset);
                ball.addSmallRandomAngle();// di chuyển random tránh lặp lại quỹ đạo
            }
        }
    }

    private void handleBrickCollisions(Ball ball, long now) {
        if (now - ball.getLastCollisionTime() < COLLISION_COOLDOWN_NANOS) return; // tránh bị kẹt
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            Node ballNode = ball.getShape();
            if (ballNode.getBoundsInParent().intersects(r.getBoundsInParent())) { // va chạm khi vùng bao chạm nhau
                double ballX = ball.getX(), ballY = ball.getY(), radius = ball.getRadius();
                double brickX = r.getLayoutX(), brickY = r.getLayoutY();
                double brickW, brickH;
                if (r instanceof ImageView iv) {
                    brickW = iv.getFitWidth();
                    brickH = iv.getFitHeight();
                } else {
                    brickW = r.getBoundsInParent().getWidth();
                    brickH = r.getBoundsInParent().getHeight();
                }
                ///  tính mức độ chồng lấn để phản xạ, tìm min
                double overlapLeft = (ballX + radius) - brickX, overlapRight = (brickX + brickW) - (ballX - radius);
                double overlapTop = (ballY + radius) - brickY, overlapBottom = (brickY + brickH) - (ballY - radius);
                double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
                // tìm hướng phản xạ
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
                playSound(hitSound);

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
                ///  đưa bóng ra ngoài biên vật thể tránh chồng nhau
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
    ///  xử lý nhân đôi bóng
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
            playSound(breakSound);
            gamePane.getChildren().remove(brick.getShape());

            if (brick instanceof StrongBrick || brick instanceof NormalBrick) {
                spawnBanana(brick);
            }

            if ("doubleBall".equals(brick.getType())) {
                spawnDoubleBall();
            }
        } else {
            playSound(hitSound);
        }
    }

    private void checkLevelComplete() {
        if (isGameOver) return;
        boolean allDestroyed = bricks.stream().allMatch(Brick::isDestroyed);
        if (allDestroyed) {
            isGameOver = true;
            gameLoop.stop();
            stopGameMusic();
            showGameEndScreen(true); // true = WIN, sẽ load GameOver.fxml
        }
    }

    //giảm mạng khi rơi bóng
    private void checkGameOver() {
        if (isGameOver) return;
        if (balls.isEmpty()) {
            loseLife(); // trừ mạng
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
            case "SLOW_DOWN":
                imageToSpawn = slowDownImage;
                break;
            case "BOMB":
                imageToSpawn = bombPowerUpImage;
                break;
            case "ENLARGE_PADDLE":
                imageToSpawn = enlargePowerUpImage;
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

    protected void updatePowerUps() {
        for (PowerUp item : new ArrayList<>(powerUps)) {
            item.update();

            if (item.getShape().getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {
                playSound(powerUpSound);
                item.setCollected();
                gamePane.getChildren().remove(item.getShape());
                switch (item.getType()) {
                    case "BANANA":
                        addScore(100);
                        System.out.println("Hứng được chuối! Điểm: " + score);
                        playSound(bananaSound);
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

    // mạng
    private void setupHearts() {
        System.out.println(" setupHearts() gọi, gamePane = " + gamePane);
        if (gamePane == null) return;

        double startX = 0;
        double startY = 0;
        double spacing = 45; // khoảng cách

        for (int i = 0; i < lives; i++) {
            ImageView heart = new ImageView(heartImage);
            heart.setFitWidth(40);
            heart.setFitHeight(40);
            heart.setLayoutX(startX + i * spacing);
            heart.setLayoutY(startY);
            heartIcons.add(heart);
            gamePane.getChildren().add(heart);
            addHeartBeatEffect(heart); // thêm hiệu ứng nhịp tim
        }
    }

    // mạng - mất mạng thif mất 1 tym và reset bóng lên paddle
    protected void loseLife() {
        lives--;
        System.out.println("Mất mạng! Còn lại: " + lives);

        if (lives >= 0 && lives < heartIcons.size()) {
            heartIcons.get(lives).setVisible(false);
        }

        if (lives > 0) {
            // reset bóng
            for (Ball b : balls) gamePane.getChildren().remove(b.getShape());
            balls.clear();

            Circle newBallCircle = new Circle(10);
            newBallCircle.setFill(ballCircle.getFill());
            gamePane.getChildren().add(newBallCircle);

            Ball newBall = new Ball(newBallCircle, sceneWidth, sceneHeight);
            balls.add(newBall);

            resetBallPosition();
            ballLaunched = false;
        } else {
            isGameOver = true;
            playSound(loseBallSound);
            gameLoop.stop();
            stopGameMusic();
            showGameEndScreen(false); // false = LOSE, load GameOver.fxml
        }
    }

    // hieu ung mang
    private void addHeartBeatEffect(ImageView heart) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.5), heart);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setCycleCount(Animation.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }

    // Thêm phương thức pauseGame và resumeGame
    protected void pauseGame() {
        if (gameLoop != null) { // gameLoop là AnimationTimer
            gameLoop.stop();
        }
    }

    protected void resumeGame() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void hidePauseMenu() {
        if (pauseOverlay != null) {
            gamePane.getChildren().remove(pauseOverlay);
            pauseOverlay = null;

            // resume game loop
            if (gameLoop != null) gameLoop.start();
        }
    }

    private void stopGameMusic() {
        // Chúng ta gọi biến "static" của MapController
        if (MapController.gameMusicPlayer != null) {
            MapController.gameMusicPlayer.stop();
        }
    }

    public void switchLevel(int direction) {
        int currentLevel = Integer.parseInt(getClass().getSimpleName().replace("Level", "").replace("Controller", ""));
        int newLevel = currentLevel + direction;
        String newLevelFxml = "/RenderView/Level/Level" + newLevel + ".fxml";

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(newLevelFxml));
            javafx.scene.Scene newScene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) gamePane.getScene().getWindow();
            stage.setScene(newScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GAMEOVER
    private void showGameEndScreen(boolean isWin) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Level/GameOver.fxml"));
                Parent root = loader.load();

                GameOverController controller = loader.getController();
                controller.setData(getCurrentLevelNumber(), this.score, isWin);

                Stage stage = (Stage) gamePane.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Không load được GameOver.fxml");
            }
        });
    }

    protected int getCurrentLevelNumber() {
        return 1; // Mặc định ở 1 level controller
    }

    // phương thức lấy level hiện tại
    private int getCurrentLevel() {
        try {
            String clsName = this.getClass().getSimpleName(); // ví dụ Level1Controller
            return Integer.parseInt(clsName.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return 1; // default level
        }
    }

    public void setupLevelController() {
        // reset lại mọi thứ giống khi khởi tạo level
        resetPositions();
        setupHearts();
        loadBricksFromPane();
        startGameLoop();
    }

    public abstract class BaseLevelController {
        protected int level;

        public abstract void initLevel(); // reset
    }

    public void initLevel() {
        setupLevelController();
    }

    public Pane getGamePane() {
        return gamePane;
    }

    public int getHighScoreForLevel(int level) {
        Path file = Path.of("src/main/data/highscores.txt");
        int maxScore = 0;
        if (!Files.exists(file)) return 0;

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    int score = Integer.parseInt(parts[0]);
                    int lvl = Integer.parseInt(parts[1]);
                    if (lvl == level && score > maxScore) {
                        maxScore = score;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxScore;
    }

    private void loadSounds() {
        hitSound = loadSound("/sounds/wavbrickpaddle.wav");
        breakSound = loadSound("/sounds/wavpaddleball.wav");
        powerUpSound = loadSound("/sounds/powerUPP2.wav");
        loseBallSound = loadSound("/sounds/gameover.mp3");
        bananaSound = loadSound("/sounds/Anchuoi.wav");
    }

    private AudioClip loadSound(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                return new AudioClip(url.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file âm thanh: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải âm thanh: " + path + " - " + e.getMessage());
            return null;
        }
    }

    private void playSound(AudioClip clip) {
        if (clip != null && !SoundManager.isSoundMuted) {
            audioExecutor.submit(() -> clip.play());
        }
    }

    @FXML
    private void handlePauseButton() {
        togglePause();
    }

    private void togglePause() {
        if (isGameOver) return;

        isPaused = !isPaused; // Lật trạng thái

        if (isPaused) {
            // DỪNG GAME
            gameLoop.stop();
            // Hiện màn hình mờ "PAUSED"
            if (pauseOverlay != null) {
                pauseOverlay.setVisible(true);
                pauseOverlay.setMouseTransparent(false);
            }
            // Đổi icon nút sang hình Tam giác (Play)
            if (pauseIcon != null) {
                pauseIcon.setImage(playIconImage);
            }
            if (pauseButton != null) {
                pauseButton.toFront();
            }
        } else {

            gameLoop.start();
            if (pauseOverlay != null) {
                pauseOverlay.setVisible(false);
                pauseOverlay.setMouseTransparent(true);
            }

            if (pauseIcon != null) {
                pauseIcon.setImage(pauseIconImage);
            }

            gamePane.requestFocus();
        }
    }

    // để test
    public void updateAllPowerUps() {
        updatePowerUps();
    }

    public boolean isPaddleMagneticActive() {
        return isPaddleMagnetic;
    }

    public List<Ball> getBalls() {
        return balls;
    }
}