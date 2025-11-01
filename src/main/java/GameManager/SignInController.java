package GameManager;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;

public class SignInController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Button signInButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Hiệu ứng
        signInButton.setOnMouseEntered(e -> {
            signInButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #b3e5ff, #7ec8ff);" +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;");
            signInButton.setEffect(new Glow(0.5));

            ScaleTransition st = new ScaleTransition(Duration.millis(150), signInButton);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
        });

        signInButton.setOnMouseExited(e -> {
            signInButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #a2d9ff, #69b9ff);" +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;");
            signInButton.setEffect(null);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), signInButton);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

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
