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
        // music
        musicToggle.setOnAction(e -> {
            if (musicToggle.isSelected()) {
                musicToggle.setText("ON");
                musicToggle.setStyle(
                        "-fx-background-color: #4CAF50;" +  // xah
                                "-fx-text-fill: #0b3d02;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 25;" +
                                "-fx-pref-width: 120;" +
                                "-fx-pref-height: 40;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                );
            } else {
                musicToggle.setText("OFF");
                musicToggle.setStyle(
                        "-fx-background-color: #F44336;" +  // nền đỏ
                                "-fx-text-fill: white;" +           // chữ trắng
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 25;" +
                                "-fx-pref-width: 120;" +
                                "-fx-pref-height: 40;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                );
            }
        });

        // sound
        soundToggle.setOnAction(e -> {
                    if (soundToggle.isSelected()) {
                        soundToggle.setText("ON");
                        soundToggle.setStyle(
                                "-fx-background-color: #4CAF50;" +
                                        "-fx-text-fill: #0b3d02;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-background-radius: 25;" +
                                        "-fx-pref-width: 120;" +
                                        "-fx-pref-height: 40;" +
                                        "-fx-cursor: hand;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                        );
                    } else {
                        soundToggle.setText("OFF");
                        soundToggle.setStyle(
                                "-fx-background-color: #F44336;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 16px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-background-radius: 25;" +
                                        "-fx-pref-width: 120;" +
                                        "-fx-pref-height: 40;" +
                                        "-fx-cursor: hand;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);"
                        );
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
