import controller.paddleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final double SCENE_WIDTH = 600;
    private static final double SCENE_HEIGHT = 690;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hello.fxml"));
        paddleController paddleController = new paddleController();
        loader.setController(paddleController);

        Parent root = loader.load();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Arkanoid Game");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
