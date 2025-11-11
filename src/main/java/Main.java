import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import java.net.URL;
import GameManager.SoundManager;

public class Main extends Application {
    private AudioClip clickSound;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            URL resource = getClass().getResource("/sounds/Click.wav");
            if (resource != null) {
                clickSound = new AudioClip(resource.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file /sounds/click.wav");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RenderView/Menu/SignIn.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Sign In");
        stage.setScene(scene);

        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            if (event.getTarget() instanceof Button || (event.getTarget() instanceof Node && ((Node)event.getTarget()).getParent() instanceof Button)) {

                playSound(clickSound);
            }
            if (!SoundManager.isSoundMuted) {
                if (event.getTarget() instanceof Button || (event.getTarget() instanceof Node && ((Node)event.getTarget()).getParent() instanceof Button)) {
                    playSound(clickSound);
                }
            }
        });

        // Hiển thị
        stage.show();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }


    private void playSound(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}