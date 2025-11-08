import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RenderView/Menu/SignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign In");
        stage.setScene(scene);
        stage.show();

        // căn giữa
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
