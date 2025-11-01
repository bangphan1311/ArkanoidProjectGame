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

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Hiệu ứng
        signUpButton.setOnMouseEntered(e -> {
            signUpButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #b3e5ff, #7ec8ff);" +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;");
            signUpButton.setEffect(new Glow(0.5));

            ScaleTransition st = new ScaleTransition(Duration.millis(150), signUpButton);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
        });

        signUpButton.setOnMouseExited(e -> {
            signUpButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #a2d9ff, #69b9ff);" +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand;");
            signUpButton.setEffect(null);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), signUpButton);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    @FXML
    private void handleSignUp(javafx.event.ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill in all fields!");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        // tbao thanh cong
        messageLabel.setText("Account created successfully!");
        messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
    }

    @FXML
    private void handleGoToSignIn(javafx.event.ActionEvent event) {
        try {
            URL url = getClass().getResource("/RenderView/SignIn.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Cannot load SignIn.fxml!");
        }
    }
}
