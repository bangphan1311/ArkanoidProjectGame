package GameManager;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class SettingsController {

    @FXML
    private ToggleButton musicToggle;

    @FXML
    private ToggleButton soundToggle;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // nhạc
        musicToggle.setOnAction(e -> {
            if (musicToggle.isSelected()) {
                musicToggle.setText("ON");
                musicToggle.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            } else {
                musicToggle.setText("OFF");
                musicToggle.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }
        });

        // âm thanh
        soundToggle.setOnAction(e -> {
            if (soundToggle.isSelected()) {
                soundToggle.setText("ON");
                soundToggle.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            } else {
                soundToggle.setText("OFF");
                soundToggle.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }
        });
    }

    // back to menu
    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }
}
