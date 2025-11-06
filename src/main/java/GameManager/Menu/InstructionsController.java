package GameManager.Menu;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.IOException;

public class InstructionsController {

    @FXML
    private ImageView storyImage;

    @FXML
    private Button nextButton, previousButton, menuButton;

    private int currentPage = 1;
    private final int totalPages = 5;

    @FXML
    public void initialize() {
        showPage(currentPage);

        // Thêm hiệu ứng hover sáng + rung nhẹ
        addHoverEffect(menuButton);
        addHoverEffect(nextButton);
        addHoverEffect(previousButton);
    }

    private void showPage(int page) {
        String imagePath = "/Images/" + page + ".png";
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            storyImage.setImage(img);

            FadeTransition ft = new FadeTransition(Duration.millis(600), storyImage);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh: " + imagePath);
            e.printStackTrace();
        }

        updateButtons();
    }

    private void updateButtons() {
        if (currentPage == 1) {
            previousButton.setVisible(false);
            nextButton.setVisible(true);
        } else if (currentPage == totalPages) {
            nextButton.setVisible(false);
            previousButton.setVisible(true);
        } else {
            previousButton.setVisible(true);
            nextButton.setVisible(true);
        }
    }

    @FXML
    void handleNext(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
        }
    }

    @FXML
    void handlePrevious(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    }

    @FXML
    void handleMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // hiệu ứng khi di chuột đến nút
    private void addHoverEffect(Button button) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.WHITE);
        glow.setRadius(15);
        glow.setSpread(0.4);

        // Khi di chuột vào
        button.setOnMouseEntered(e -> {
            // Hiệu ứng sáng
            button.setEffect(glow);

            // Phóng to nhẹ
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();

            // thêm rung
            TranslateTransition tt = new TranslateTransition(Duration.millis(60), button);
            tt.setFromX(0);
            tt.setByX(5);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.play();
        });

        // Khi rời chuột
        button.setOnMouseExited(e -> {
            button.setEffect(null);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
