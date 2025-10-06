package org.example.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private static final double SCENE_WIDTH = 600;
    private static final double SCENE_HEIGHT = 690;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        Ball ball = new Ball(10, SCENE_WIDTH, SCENE_HEIGHT);
        root.getChildren().add(ball.getShape());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ball.update();
            }
        };
        timer.start();
        stage.setTitle("Arkanoid Ball Test");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
