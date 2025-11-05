package GameManager;

import Entity.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameController {
    @FXML private Pane gamePane;
    @FXML private Pane brickPane;
    @FXML private Rectangle paddleRect;
    @FXML private Circle ballCircle;
    @FXML private ImageView obstacle1;

    // ========================
    // Shared state for v1 (advanced)
    // ========================
    private final List<Ball> balls = new ArrayList<>();         // dynamic balls (v1)
    private final List<Brick> bricks = new ArrayList<>();      // bricks parsed from ImageView (v1)
    private boolean moveLeft = false;
    private boolean moveRight = false;

    private AnimationTimer gameLoop;                             // main loop for v1
    private boolean isLevelLoading = false;
    private boolean isGameOver = false;

    private long lastCollisionTime = 0;
    private static final long COLLISION_COOLDOWN_NANOS = 40_000_000L; // 40ms cooldown

    private double sceneWidth = 800;
    private double sceneHeight = 600;
    private final double paddleSpeed = 8.0;

    private MovingObstacle movingObstacle = null;
    private double obstacleSpeed = 3.5;

    private String nextLevelFile = null;
    private String backgroundImagePath = "/Images/level1.png";
    private String paddleImagePath = "/Images/paddle.png";

    // ========================
    // State for v2 (simple)
    // ========================
    private Paddle paddle_v2;
    private Ball ball_v2;
    private final List<Brick> bricks_v2 = new ArrayList<>();   // bricks parsed from Rectangle (v2)
    private boolean moveLeft_v2 = false;
    private boolean moveRight_v2 = false;
    private AnimationTimer gameLoop_v2;                        // separate loop for v2

    // ========================
    // Configuration helpers
    // ========================
    public void configureGraphics(String bgPath, String paddlePath) {
        this.backgroundImagePath = bgPath;
        this.paddleImagePath = paddlePath;
    }

    public void setNextLevel(String nextLevelFile) {
        this.nextLevelFile = nextLevelFile;
    }

    // ========================
    // === INITIALIZE (v1) ===
    // ========================
    @FXML
    public void initialize() {
        // apply background if set (if initialize called after configureGraphics it will use that)
        if (backgroundImagePath != null) {
            try {
                var url = getClass().getResource(backgroundImagePath);
                if (url != null && gamePane != null) {
                    gamePane.setStyle(String.format("""
                            -fx-background-image: url('%s');
                            -fx-background-repeat: no-repeat;
                            -fx-background-position: center;
                            -fx-background-size: cover;
                            """, url.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi tải background: " + e.getMessage());
            }
        }

        // paddle image
        try {
            var pUrl = getClass().getResource(paddleImagePath);
            if (pUrl != null && paddleRect != null) {
                Image paddleImg = new Image(pUrl.toExternalForm());
                paddleRect.setFill(new ImagePattern(paddleImg));
                paddleRect.setStroke(null);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh paddle: " + e.getMessage());
        }

        if (gamePane != null) {
            if (gamePane.getPrefWidth() > 0) sceneWidth = gamePane.getPrefWidth();
            if (gamePane.getPrefHeight() > 0) sceneHeight = gamePane.getPrefHeight();
        }

        // create main ball object (v1)
        if (ballCircle != null) {
            Ball mainBall = new Ball(ballCircle.getRadius(), sceneWidth, sceneHeight);
            mainBall.setPosition(ballCircle.getCenterX(), ballCircle.getCenterY());
            balls.add(mainBall);
        } else {
            Ball mainBall = new Ball(12, sceneWidth, sceneHeight);
            mainBall.setPosition(sceneWidth / 2.0, sceneHeight / 2.0);
            balls.add(mainBall);
        }

        // initial positions
        resetPositions();

        // controls and bricks
        setupControls();
        loadBricksFromPane(); // loads ImageView bricks (v1)

        // initialize obstacle and start loop after layout pass
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

    // ========================
    // === SETUP CONTROLS ===
    // ========================
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

    // ========================
    // === LOAD BRICKS (v1) from ImageView nodes ===
    // ========================
    private void loadBricksFromPane() {
        if (brickPane == null) return;

        Image damagedImg = null;
        try {
            var damagedUrl = getClass().getResource("/Images/brick_strong_damaged.png");
            if (damagedUrl != null) damagedImg = new Image(damagedUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Không load được ảnh brick damaged: " + e.getMessage());
        }

        for (var node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof ImageView img) {
                String fxId = img.getId() != null ? img.getId().toLowerCase() : "";
                Brick brick;
                if (fxId.contains("obstacle")) continue; // skip if named obstacle

                if (fxId.contains("strong")) {
                    brick = new StrongBrick(
                            img.getLayoutX(), img.getLayoutY(),
                            img.getFitWidth(), img.getFitHeight(),
                            img.getImage(), damagedImg
                    );
                } else {
                    NormalBrick nb = new NormalBrick(
                            img.getLayoutX(), img.getLayoutY(),
                            img.getFitWidth(), img.getFitHeight(),
                            img.getImage()
                    );
                    // preserve "doubleBall" semantics if id contains doubleball
                    if (fxId.contains("doubleball")) nb.setType("doubleBall");
                    brick = nb;
                }

                bricks.add(brick);
                // Brick's shape expected to be a Node/ImageView/Rectangle - we add it to gamePane for rendering
                gamePane.getChildren().add(brick.getShape());
                img.setVisible(false);
            }
        }

        // Additionally, support rectangles (in case both types present in same FXML).
        // Convert any Rectangle nodes (from old FXML) to NormalBrick.
        for (var node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof Rectangle rect) {
                // convert Rectangle -> NormalBrick using its layout and size; we don't have an image so pass null
                NormalBrick n = new NormalBrick(rect.getLayoutX(), rect.getLayoutY(),
                        rect.getWidth(), rect.getHeight(), null);
                n.setType("normal");
                bricks.add(n);
                gamePane.getChildren().add(n.getShape());
                rect.setVisible(false);
            }
        }
    }

    // ========================
    // === START / STOP (v1) ===
    // ========================
    public void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
            }
        };
        gameLoop.start();
    }

    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // ========================
    // === UPDATE (v1) ===
    // ========================
    private void update(long now) {
        // move obstacle
        if (movingObstacle != null) {
            movingObstacle.update();
        }

        // paddle movement (v1)
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

        // update all balls (v1)
        List<Ball> toRemove = new ArrayList<>();
        for (Ball b : new ArrayList<>(balls)) {
            b.update();

            // update visible main ball position
            if (!balls.isEmpty() && b == balls.get(0) && ballCircle != null) {
                ballCircle.setCenterX(b.getShape().getCenterX());
                ballCircle.setCenterY(b.getShape().getCenterY());
            } else {
                // extra balls must be shown on screen
                if (!gamePane.getChildren().contains(b.getShape())) {
                    gamePane.getChildren().add(b.getShape());
                }
            }

            handlePaddleCollision(b);
            handleBrickCollisions(b, now);

            if (movingObstacle != null) {
                handleObstacleCollision(b);
            }

            // remove ball if it falls below screen
            if (b.getY() - b.getRadius() > gamePane.getHeight()) {
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

    // ========================
    // === PADDLE COLLISION (v1) ===
    // ========================
    private void handlePaddleCollision(Ball ball) {
        Node ballNode = (balls.size() > 0 && ball == balls.get(0)) ? ballCircle : ball.getShape();

        if (ball.getDirY() > 0 && ballNode.getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {
            ball.reverseY();

            double paddleY = paddleRect.getLayoutY();
            double newY = paddleY - ball.getRadius() - 1;
            ball.setPosition(ball.getX(), newY);

            if (ballNode == ballCircle) {
                ballCircle.setCenterY(newY);
            } else {
                ball.getShape().setLayoutY(newY);
            }

            double paddleCenter = paddleRect.getLayoutX() + paddleRect.getWidth() / 2.0;
            double hitOffset = (ball.getX() - paddleCenter) / (paddleRect.getWidth() / 2.0);
            hitOffset = Math.max(-1, Math.min(1, hitOffset));
            ball.setDirX(hitOffset);
            ball.addSmallRandomAngle();
        }
    }

    // ========================
    // === BRICK COLLISIONS (v1) ===
    // ========================
    private void handleBrickCollisions(Ball ball, long now) {
        if (now - lastCollisionTime < COLLISION_COOLDOWN_NANOS) return;

        // iterate both bricks lists (ImageView-derived and Rectangle-derived)
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            Node ballNode = (balls.size() > 0 && ball == balls.get(0)) ? ballCircle : ball.getShape();
            if (ballNode.getBoundsInParent().intersects(r.getBoundsInParent())) {
                // compute overlap and reflect
                double ballX = ball.getX(), ballY = ball.getY(), radius = ball.getRadius();
                double brickX = r.getLayoutX(), brickY = r.getLayoutY();
                double brickW, brickH;
                if (r instanceof ImageView iv) {
                    brickW = iv.getFitWidth();
                    brickH = iv.getFitHeight();
                } else if (r instanceof Rectangle rect) {
                    brickW = rect.getWidth();
                    brickH = rect.getHeight();
                } else {
                    brickW = r.getBoundsInParent().getWidth();
                    brickH = r.getBoundsInParent().getHeight();
                }

                double overlapLeft = (ballX + radius) - brickX;
                double overlapRight = (brickX + brickW) - (ballX - radius);
                double overlapTop = (ballY + radius) - brickY;
                double overlapBottom = (brickY + brickH) - (ballY - radius);

                double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

                if (minOverlap == overlapLeft) ball.reflect(-1, 0);
                else if (minOverlap == overlapRight) ball.reflect(1, 0);
                else if (minOverlap == overlapTop) ball.reflect(0, -1);
                else ball.reflect(0, 1);

                lastCollisionTime = now;
                brick.takeHit();
                if ("doubleBall".equals(brick.getType())) {
                    spawnDoubleBall();
                }

                break;
            }
        }

        for (Brick brick : new ArrayList<>(bricks_v2)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            Node ballNode = (balls.size() > 0 && ball == balls.get(0)) ? ballCircle : ball.getShape();
            if (ballNode.getBoundsInParent().intersects(r.getBoundsInParent())) {
                brick.destroy();
                ball.reverseY();
                break;
            }
        }
    }

    // ========================
    // === OBSTACLE COLLISION (v1) ===
    // ========================
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

    // ========================
    // === LEVEL & GAMEOVER (v1) ===
    // ========================
    private void checkLevelComplete() {
        if (isLevelLoading) return;
        boolean allDestroyed = bricks.stream().allMatch(Brick::isDestroyed) && bricks_v2.stream().allMatch(Brick::isDestroyed);
        if (allDestroyed) {
            isLevelLoading = true;
            if (gameLoop != null) gameLoop.stop();
            showNextLevelScreen();
        }
    }

    private void checkGameOver() {
        if (isGameOver || isLevelLoading) return;
        if (balls.isEmpty()) {
            isGameOver = true;
            if (nextLevelFile != null) {
                // Khi bóng rơi -> qua level tiếp theo (nếu có)
                showNextLevelScreen();
            } else {
                // Nếu không có level tiếp theo thì kết thúc game
                System.out.println("--- GAME OVER ---");
                Platform.exit();
            }
        }
    }


    private void showNextLevelScreen() {
        Platform.runLater(() -> {
            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
            overlay.setPrefSize(gamePane.getWidth(), gamePane.getHeight());

            Button nextButton = new Button(nextLevelFile == null ? "Exit Game" : "Next Level");
            nextButton.setLayoutX(gamePane.getWidth() / 2 - 60);
            nextButton.setLayoutY(gamePane.getHeight() / 2 - 25);
            nextButton.setPrefWidth(120);
            nextButton.setStyle("-fx-font-size: 16px; -fx-background-color: gold; -fx-font-weight: bold;");

            nextButton.setOnAction(e -> {
                if (nextLevelFile == null) {
                    Platform.exit();
                    return;
                }

                try {
                    // ✅ Load file FXML level 2
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/" + nextLevelFile));
                    Pane nextRoot = loader.load();
                    GameController nextController = loader.getController();

                    if (nextController != null) {
                        // ✅ cấu hình hình nền và paddle cho level 2
                        nextController.configureGraphics(
                                "/Images/level2.png",
                                "/Images/paddle.png"
                        );
                        nextController.setNextLevel(null); // level 2 là level cuối
                    }

                    // ✅ Cách load mới: tạo Scene mới để FXML của Level 2 được initialize()
                    Scene newScene = new Scene(nextRoot);
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(newScene);
                    stage.show();

                    // ✅ Sau khi scene đã set -> khởi tạo game level 2
                    Platform.runLater(() -> {
                        if (nextController != null) {
                            nextController.applyGraphics();
                            nextController.resetPositions();
                            nextController.startGameLoop();
                        } else {
                            System.err.println("⚠️ nextController is null!");
                        }
                        nextRoot.requestFocus();
                        if (nextRoot.lookup("#gamePane") instanceof Pane gp) {
                            gp.requestFocus();
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            overlay.getChildren().add(nextButton);
            gamePane.getChildren().add(overlay);
        });
    }



    private void spawnDoubleBall() {
        if (balls.size() >= 2) return;
        Ball base = balls.get(0);
        Ball extraBall = new Ball(base.getRadius(), sceneWidth, sceneHeight);
        extraBall.setPosition(base.getX(), base.getY());
        extraBall.setDirX(-base.getDirX());
        extraBall.setDirY(base.getDirY());

        balls.add(extraBall);
        gamePane.getChildren().add(extraBall.getShape());
    }

    // ========================
    // === RESET POSITIONS (v1) ===
    // ========================
    public void resetPositions() {
        // reset paddle
        if (paddleRect != null) {
            paddleRect.setLayoutX(sceneWidth / 2.0 - paddleRect.getWidth() / 2.0);
            paddleRect.setLayoutY(sceneHeight - 40);
        }

        // reset visible ball
        if (ballCircle != null) {
            ballCircle.setCenterX(sceneWidth / 2.0);
            ballCircle.setCenterY(sceneHeight / 2.0);
        }

        // reset physics balls
        balls.clear();
        Ball newBall = new Ball(ballCircle != null ? ballCircle.getRadius() : 12, sceneWidth, sceneHeight);
        newBall.setPosition(sceneWidth / 2.0, sceneHeight / 2.0);
        balls.add(newBall);

        isGameOver = false;
    }

    // ========================
    // === APPLY GRAPHICS UTILS (v1) ===
    // ========================
    public void applyGraphics() {
        try {
            java.net.URL bgUrl = getClass().getResource(backgroundImagePath);
            java.net.URL paddleUrl = getClass().getResource(paddleImagePath);

            if (gamePane == null) {
                System.err.println("applyGraphics: gamePane is null (FXML injection failed?)");
                return;
            }

            if (bgUrl != null) {
                String bgCss = String.format("""
                        -fx-background-image: url('%s');
                        -fx-background-repeat: no-repeat;
                        -fx-background-position: center;
                        -fx-background-size: cover;
                        """, bgUrl.toExternalForm());
                gamePane.setStyle(bgCss);
            } else {
                System.err.println("applyGraphics: background resource NOT FOUND: " + backgroundImagePath);
            }

            if (paddleRect == null) {
                System.err.println("applyGraphics: paddleRect is null (FXML injection failed?)");
            } else if (paddleUrl != null) {
                Image paddleImg = new Image(paddleUrl.toExternalForm());
                paddleRect.setFill(new ImagePattern(paddleImg));
            } else {
                System.err.println("applyGraphics: paddle resource NOT FOUND: " + paddleImagePath);
            }
        } catch (Exception e) {
            System.err.println(" applyGraphics Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================
    // === BELOW: v2 (simple) ===
    // ========================

    /**
     * initialize_v2 - phiên bản đơn giản hơn (dùng Rectangle bricks)
     * Gọi từ FXML nếu bạn muốn chạy phiên bản này thay cho initialize()
     */
    @FXML
    public void initialize_v2() {
        if (gamePane != null) {
            if (gamePane.getPrefWidth() > 0) sceneWidth = gamePane.getPrefWidth();
            if (gamePane.getPrefHeight() > 0) sceneHeight = gamePane.getPrefHeight();
        }

        // create paddle and ball objects for v2
        paddle_v2 = new Paddle(
                paddleRect.getLayoutX(),
                paddleRect.getLayoutY(),
                paddleRect.getWidth(),
                paddleRect.getHeight(),
                8,
                sceneWidth
        );

        ball_v2 = new Ball(
                ballCircle.getRadius(),
                sceneWidth,
                sceneHeight
        );
        ball_v2.setPosition(ballCircle.getCenterX(), ballCircle.getCenterY());
        ball_v2.setDirection(0, -1); // initial up

        setupControls_v2();
        loadBricksFromPane_v2();
        startGameLoop_v2();
        if (gamePane != null) gamePane.requestFocus();
    }

    private void setupControls_v2() {
        if (gamePane == null) return;
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft_v2 = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight_v2 = true;
        });
        gamePane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) moveLeft_v2 = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) moveRight_v2 = false;
        });
    }

    private void loadBricksFromPane_v2() {
        if (brickPane == null) return;

        for (Node node : new ArrayList<>(brickPane.getChildren())) {
            if (node instanceof Rectangle r) {
                // Brick is abstract -> instantiate NormalBrick
                NormalBrick brick = new NormalBrick(
                        r.getLayoutX(),
                        r.getLayoutY(),
                        r.getWidth(),
                        r.getHeight(),
                        null // no image available for Rectangle bricks
                );
                bricks_v2.add(brick);
                gamePane.getChildren().add(brick.getShape());
                r.setVisible(false);
            }
        }
    }

    private void startGameLoop_v2() {
        if (gameLoop_v2 != null) gameLoop_v2.stop();
        gameLoop_v2 = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update_v2();
            }
        };
        gameLoop_v2.start();
    }

    private void update_v2() {
        double w = gamePane.getWidth();
        double h = gamePane.getHeight();

        if (moveLeft_v2) paddle_v2.moveLeft();
        if (moveRight_v2) paddle_v2.moveRight();

        // reflect paddleRect with Paddle object
        if (paddle_v2.getShape() != null) {
            paddleRect.setLayoutX(paddle_v2.getShape().getX());
            paddleRect.setLayoutY(paddle_v2.getShape().getY());
        }

        ball_v2.update();
        if (ball_v2.getShape() != null) {
            ballCircle.setCenterX(ball_v2.getShape().getCenterX());
            ballCircle.setCenterY(ball_v2.getShape().getCenterY());
        }

        // collision paddle v2
        if (ball_v2.getDirY() > 0 && ballCircle.getBoundsInParent().intersects(paddleRect.getBoundsInParent())) {
            ball_v2.reverseY();
            double newY = paddle_v2.getY() - ball_v2.getRadius() - 1;
            ball_v2.setPosition(ball_v2.getX(), newY);
            ballCircle.setCenterY(newY);

            double paddleCenter = paddle_v2.getX() + paddle_v2.getWidth() / 2;
            double hitPosition = (ball_v2.getX() - paddleCenter) / (paddle_v2.getWidth() / 2);
            hitPosition = Math.max(-1, Math.min(1, hitPosition));
            ball_v2.setDirX(hitPosition);
        }

        // collision bricks v2
        for (Brick brick : new ArrayList<>(bricks_v2)) {
            if (brick.isDestroyed()) continue;
            Node r = brick.getShape();
            if (ball_v2.getShape().getBoundsInParent().intersects(r.getBoundsInParent())) {
                brick.destroy();
                ball_v2.reverseY();
                break;
            }
        }

        // if ball falls
        if (ball_v2.getY() - ball_v2.getRadius() > h) {
            System.out.println("[v2] Ball fell out -> game over (v2).");
            if (gameLoop_v2 != null) gameLoop_v2.stop();
        }
    }

    // Utility: stop v2 loop
    public void stopGameLoop_v2() {
        if (gameLoop_v2 != null) gameLoop_v2.stop();
    }
}
