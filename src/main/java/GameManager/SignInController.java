package GameManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SignInController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;

    // chuyển đổi giữa password ẩn/hiện
    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        if (showPasswordCheck.isSelected()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    // xử lý đăng nhập
    @FXML
    private void handleSignIn(ActionEvent event) {
        String username = usernameField.getText();
        String password = showPasswordCheck.isSelected()
                ? visiblePasswordField.getText()
                : passwordField.getText();

        if (username.equals("admin") && password.equals("123")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Menu");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
        }
    }

    //  Sign Up
    @FXML
    private void handleGoToSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
