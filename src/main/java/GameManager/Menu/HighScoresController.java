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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HighScoresController {

    @FXML private TableView<ScoreEntry> scoreTable;
    @FXML private TableColumn<ScoreEntry, String> nameColumn;
    @FXML private TableColumn<ScoreEntry, Integer> scoreColumn;
    @FXML private Button backButton;

    private final Path highscoreFile = Path.of("src/main/data/highscores.txt");

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("score"));

        // Load tất cả điểm
        List<ScoreEntry> allScores = loadHighScores();

        // sx
        allScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // ghan 10
        List<ScoreEntry> top10 = allScores.size() > 10 ? allScores.subList(0, 10) : allScores;

        ObservableList<ScoreEntry> data = FXCollections.observableArrayList(top10);
        scoreTable.setItems(data);

        // Lấy username hiện tại
        String currentUser = Session.getUsername();

        // Highlight top 3 + user hiện tại
        scoreTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ScoreEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }

                int index = getIndex();
                if (index == 0) {
                    setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"); // Top 1
                } else if (index == 1) {
                    setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"); // Top 2
                } else if (index == 2) {
                    setStyle("-fx-background-color: #5dade2; -fx-text-fill: white;"); // Top 3
                } else if (currentUser != null && item.getName().equals(currentUser)) {
                    setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #f1c40f); -fx-text-fill: white;");
                } else {
                    setStyle("");
                }
            }
        });

        scoreTable.setPlaceholder(new Label("Chưa có điểm nào được ghi nhận!"));
        addHoverEffect(backButton);
    }

    @FXML
    void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/Menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Menu");
        stage.show();
    }

    private void addHoverEffect(Button button) {
        String normalStyle = "-fx-background-color: linear-gradient(to bottom, #74b9ff, #0984e3);"
                + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-pref-width: 160; -fx-pref-height: 45;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);";

        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #a0cfff, #3b7de3);"
                + "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
                + "-fx-background-radius: 25; -fx-pref-width: 160; -fx-pref-height: 45;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(255,255,180,0.9), 15, 0.5, 0, 0);";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            button.setScaleX(1.08);
            button.setScaleY(1.08);
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
            button.setStyle(normalStyle);
        });
    }

    private List<ScoreEntry> loadHighScores() {
        List<ScoreEntry> list = new ArrayList<>();
        if (!Files.exists(highscoreFile)) return list;
        try (BufferedReader br = Files.newBufferedReader(highscoreFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    list.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // lưu
    public void saveScore(String name, int newScore) {
        try {
            Files.createDirectories(highscoreFile.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(highscoreFile,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                bw.write(name + ";" + newScore);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
