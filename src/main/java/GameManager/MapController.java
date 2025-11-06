package GameManager;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.util.Duration;

public class MapController {

    // ===== Các sự kiện click từng level =====
    @FXML
    private void handleLevel1() {
        System.out.println("Level 1 clicked!");
    }

    @FXML
    private void handleLevel2() {
        System.out.println("Level 2 clicked!");
    }

    @FXML
    private void handleLevel3() {
        System.out.println("Level 3 clicked!");
    }

    @FXML
    private void handleLevel4() {
        System.out.println("Level 4 clicked!");
    }

    @FXML
    private void handleLevel5() {
        System.out.println("Level 5 clicked!");
    }

    @FXML
    private void handleLevel6() {
        System.out.println("Level 6 clicked!");
    }

    // ===== Hiệu ứng hover =====
    @FXML
    private void onHover(javafx.scene.input.MouseEvent event) {
        Node node = (Node) event.getSource();

        // Hiệu ứng phóng to
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.play();

        // Hiệu ứng rung nhẹ
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), node);
        shake.setByX(5);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.play();

        // Hiệu ứng sáng
        node.setEffect(new Glow(0.6));
    }

    @FXML
    private void onExit(javafx.scene.input.MouseEvent event) {
        Node node = (Node) event.getSource();

        // Trả về kích thước ban đầu
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();

        // Tắt hiệu ứng sáng
        node.setEffect(null);
    }
}
