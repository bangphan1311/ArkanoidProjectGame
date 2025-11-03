package GameManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.IOException;


public class HighScoresController {

    @FXML
    private TableView<ScoreEntry> scoreTable;

    @FXML
    private TableColumn<ScoreEntry, String> nameColumn;

    @FXML
    private TableColumn<ScoreEntry, Integer> scoreColumn;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        // 0 có dữ liệu , bảng rỗng
        ObservableList<ScoreEntry> data = FXCollections.observableArrayList();

        scoreTable.setItems(data);
        scoreTable.setPlaceholder(new javafx.scene.control.Label("Chưa có điểm nào được ghi nhận!"));

        addHoverEffect(backButton);  // hiệu ứg
    }

    // back to menu
    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    // hiệu ứng
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            // sáng lên
            button.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,180,0.9), 15, 0.5, 0, 0);");

            // phóng to
            button.setScaleX(1.1);
            button.setScaleY(1.1);

            // rug
            Timeline shake = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(button.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(50), new KeyValue(button.translateXProperty(), -4)),
                    new KeyFrame(Duration.millis(100), new KeyValue(button.translateXProperty(), 4)),
                    new KeyFrame(Duration.millis(150), new KeyValue(button.translateXProperty(), -4)),
                    new KeyFrame(Duration.millis(200), new KeyValue(button.translateXProperty(), 4)),
                    new KeyFrame(Duration.millis(250), new KeyValue(button.translateXProperty(), 0))
            );
            shake.play();
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setTranslateX(0);
            button.setStyle(""); // trở về style gốc
        });
    }

    // lớp phụ để lưu dữ liệu điểm
    public static class ScoreEntry {
        private final String name;
        private final int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
    }
}