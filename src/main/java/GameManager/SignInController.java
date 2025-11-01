package GameManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SignInController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignIn(javafx.event.ActionEvent event) {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.equals("admin") && pass.equals("123")) {
            try {
                URL url = getClass().getResource("/RenderView/Menu.fxml");
                FXMLLoader loader = new FXMLLoader(url);
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Menu");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading Menu.fxml");
            }
        } else {
            errorLabel.setText("Invalid username or password!");
        }
    }

    @FXML
    private void handleGoToSignup(javafx.event.ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/SignUp.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Cannot load SignUp.fxml!");
        }
    }
}
