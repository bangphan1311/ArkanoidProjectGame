package GameManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class MapController {

    @FXML
    void handleLevel1(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Game.fxml", "Level 1");
    }

    @FXML
    void handleLevel2(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Level2.fxml", "Level 2");
    }

    @FXML
    void handleLevel3(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Level3.fxml", "Level 3");
    }

    @FXML
    void handleLevel4(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Level4.fxml", "Level 4");
    }

    @FXML
    void handleLevel5(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Level5.fxml", "Level 5");
    }

    @FXML
    void handleLevel6(ActionEvent event) throws IOException {
        switchScene(event, "/RenderView/Level6.fxml", "Level 6");
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
