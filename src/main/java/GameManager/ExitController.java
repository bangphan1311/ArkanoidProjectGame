package GameManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ExitController {

    private Stage stage;
    private Scene scene;

    // thoát game
    @FXML
    void onByeClicked(ActionEvent event) {
        System.out.println("Exit Game!");
        System.exit(0);
    }

    //  Keep Playing
    @FXML
    void onKeepPlayingClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/RenderView/Menu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
