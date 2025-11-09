package GameManager.Menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class SignInController {

    @FXML private ComboBox<String> usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label errorLabel;

    private final String USERS_FILE = "src/data/users.txt";
    private final String REMEMBER_FILE = "src/data/remember.txt";

    private Map<String, String> rememberedAccounts = new HashMap<>();

    @FXML
    public void initialize() {
        // load danh sách tài khoản đã lưu
        loadRememberedAccounts();

        // khi chọn user, điền password
        usernameField.setOnAction(e -> {
            String selectedUser = usernameField.getValue();
            if (rememberedAccounts.containsKey(selectedUser)) {
                passwordField.setText(rememberedAccounts.get(selectedUser));
            }
        });
    }

    private void loadRememberedAccounts() {
        File file = new File(REMEMBER_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    rememberedAccounts.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> usernames = FXCollections.observableArrayList(rememberedAccounts.keySet());
        usernameField.setItems(usernames);
    }

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

    @FXML
    private void handleSignIn(ActionEvent event) {
        String username = usernameField.getEditor().getText().trim();
        String password = showPasswordCheck.isSelected()
                ? visiblePasswordField.getText().trim()
                : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setVisible(true);
            return;
        }

        boolean isValid = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equals(username) && parts[1].trim().equals(password)) {
                    isValid = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isValid) {
            Session.setUsername(username);

            if (rememberMeCheck.isSelected()) {
                rememberedAccounts.put(username, password);
                saveRememberedAccounts();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Menu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Menu");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
        }
    }

    private void saveRememberedAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REMEMBER_FILE))) {
            for (Map.Entry<String, String> entry : rememberedAccounts.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Sorry. Chức năng này hiện chưa được thực hiện.");
        alert.showAndWait();
    }
}