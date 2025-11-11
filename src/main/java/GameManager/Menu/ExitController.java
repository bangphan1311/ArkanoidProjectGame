package GameManager.Menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ExitController {

    @FXML
    private Button byeButton, keepPlayingButton;

    private Stage stage;
    private Scene scene;

    @FXML
    public void initialize() {
        addHoverEffect(byeButton);
        addHoverEffect(keepPlayingButton);
    }

    // hiệu ứng
    private void addHoverEffect(Button button) {
        DropShadow glow = new DropShadow();  // ság
        glow.setColor(Color.WHITE);
        glow.setRadius(15);
        glow.setSpread(0.4);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);

            // phosg to 1,08
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();

            // rung
            TranslateTransition tt = new TranslateTransition(Duration.millis(60), button);
            tt.setFromX(0);
            tt.setByX(5);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.play();
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    // bye
    @FXML
    void onByeClicked(ActionEvent event) {
        System.out.println("Exit Game!");
        System.exit(0);
    }

    // keepplaying
    @FXML
    void onKeepPlayingClicked(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}