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
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.IOException;

public class InstructionsController {

    @FXML
    private ImageView storyImage; // hthi hình minh họa cho từng trang

    @FXML
    private Button nextButton, previousButton, menuButton;

    private int currentPage = 1;           // trag hiện tại
    private final int totalPages = 5;      // 5 trag

    @FXML
    public void initialize() {
        showPage(currentPage); // hthi trang đầu tiên

        // hiệu ứng hover sáng + rung nhẹ cho các nút
        addHoverEffect(menuButton);
        addHoverEffect(nextButton);
        addHoverEffect(previousButton);
    }

    /**
     * hthi trang theo chỉ số (kèm hiệu ứng lật + mờ dần)
     */
    private void showPage(int page) {
        String imagePath = "/Images/" + page + ".png";

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));

            // hiệu ứng
            ScaleTransition stHide = new ScaleTransition(Duration.millis(300), storyImage);
            stHide.setFromX(1);
            stHide.setToX(0); // Thu nhỏ ngang lại -> ẩn dần
            stHide.setOnFinished(e -> {
                // khi chạy xog hình cũ sag hình ms
                storyImage.setImage(img);

                // hiệu ứng mở trag ms
                ScaleTransition stShow = new ScaleTransition(Duration.millis(300), storyImage);
                stShow.setFromX(0);
                stShow.setToX(1);
                stShow.play();
            });

            stHide.play();

        } catch (Exception e) {
            System.err.println("Không thể tải ảnh: " + imagePath);
            e.printStackTrace();
        }

        updateButtons();
    }


    /**
     * nút Next - Previous tùy theo vị trí trang
     */
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

    /**
     * Xử lý khi nhấn nút NEXT
     */
    @FXML
    void handleNext(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
        }
    }

    /**
     * Xử lý khi nhấn nút PREVIOUS
     */
    @FXML
    void handlePrevious(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    }

    /**
     * xử lý khi ấn Menu
     */
    @FXML
    void handleMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiệu ứng khi di chuột vào các nút
     */
    private void addHoverEffect(Button button) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.WHITE);
        glow.setRadius(15);
        glow.setSpread(0.4);

        //di chuột vào
        button.setOnMouseEntered(e -> {
            button.setEffect(glow); // phát sáng

            // Phóng to nhẹ
            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();

            //rung nhẹ
            TranslateTransition tt = new TranslateTransition(Duration.millis(60), button);
            tt.setFromX(0);
            tt.setByX(5);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.play();
        });

        //rời chuột
        button.setOnMouseExited(e -> {
            button.setEffect(null);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
