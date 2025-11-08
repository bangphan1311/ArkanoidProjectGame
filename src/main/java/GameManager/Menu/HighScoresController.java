package GameManager.Menu;

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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class HighScoresController {

    @FXML
    private TableView<ScoreEntry> scoreTable;

    @FXML
    private TableColumn<ScoreEntry, String> nameColumn;

    @FXML
    private TableColumn<ScoreEntry, Integer> scoreColumn;

    @FXML
    private Button backButton;

    private final Path highscoreFile = Path.of("src/main/data/highscores.txt");

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("PLAYER NAME"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("SCORE"));

        // Load dữ liệu từ file
        List<ScoreEntry> scores = loadHighScores();
        scores.sort((a, b) -> b.getScore() - a.getScore()); // giảm dần
        if (scores.size() > 10) scores = scores.subList(0, 10); // Top 10

        ObservableList<ScoreEntry> data = FXCollections.observableArrayList(scores);
        scoreTable.setItems(data);

        scoreTable.setPlaceholder(new javafx.scene.control.Label("Chưa có điểm nào được ghi nhận!"));

        addHoverEffect(backButton);  // hiệu ứng nút
    }

    // back to menu
    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Menu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    // hiệu ứng
    private void addHoverEffect(Button button) {
        // style gốc của nút (giống trong FXML)
        String normalStyle = "-fx-background-color: linear-gradient(to bottom, #74b9ff, #0984e3);"
                + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-pref-width: 160; -fx-pref-height: 45;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);";

        // style khi hover
        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #a0cfff, #3b7de3);"
                + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-pref-width: 160; -fx-pref-height: 45;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(255,255,180,0.9), 15, 0.5, 0, 0);";

        // style bdau
        button.setStyle(normalStyle);

        //di chuột vào nút
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle); // chuyển sang style hover
            button.setScaleX(1.08);
            button.setScaleY(1.08);

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

        // Khi rời chuột khỏi nút
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setTranslateX(0);
            button.setStyle(normalStyle); // trở lại style gốc (không xóa trắng)
        });
    }

    // đọc file highscores.txt
    private List<ScoreEntry> loadHighScores() {
        List<ScoreEntry> list = new ArrayList<>();
        if (!Files.exists(highscoreFile)) return list;

        try (BufferedReader br = Files.newBufferedReader(highscoreFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    list.add(new ScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ghi điểm mới vào file
    public void saveScore(String name, int score) {
        try {
            Files.createDirectories(highscoreFile.getParent()); // tạo thư mục nếu chưa có
            try (BufferedWriter bw = Files.newBufferedWriter(highscoreFile,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                bw.write(name + ";" + score);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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