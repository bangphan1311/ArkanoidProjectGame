package GameManager.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class GameOverController {

    @FXML private Button homeBtn;
    @FXML private Button replayBtn;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;

    private int level;      // level hiện tại
    private int score;
    private boolean isWin;

    // Map level -> class Controller, bạn cần import đầy đủ
    private static final Map<Integer, Class<?>> levelControllerMap = new HashMap<>();
    static {
        levelControllerMap.put(1, GameManager.Level.Level1Controller.class);
        levelControllerMap.put(2, GameManager.Level.Level2Controller.class);
        levelControllerMap.put(3, GameManager.Level.Level3Controller.class);
        levelControllerMap.put(4, GameManager.Level.Level4Controller.class);
        levelControllerMap.put(5, GameManager.Level.Level5Controller.class);
        levelControllerMap.put(6, GameManager.Level.Level6Controller.class);
    }

    public void setData(int level, int score, boolean isWin) {
        this.level = level;
        this.score = score;
        this.isWin = isWin;
    }

    @FXML
    public void initialize() {
        homeBtn.setOnAction(e -> switchScene("/RenderView/Menu/Menu.fxml", null));

        replayBtn.setOnAction(e -> switchScene("/RenderView/Level/Level" + level + ".fxml", levelControllerMap.get(level)));

        nextBtn.setOnAction(e -> {
            int nextLevel = level + 1;
            if (nextLevel <= 6) {
                switchScene("/RenderView/Level/Level" + nextLevel + ".fxml", levelControllerMap.get(nextLevel));
            }
        });

        previousBtn.setOnAction(e -> {
            int prevLevel = level - 1;
            if (prevLevel >= 1) {
                switchScene("/RenderView/Level/Level" + prevLevel + ".fxml", levelControllerMap.get(prevLevel));
            }
        });
    }

    private void switchScene(String fxmlPath, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (controllerClass != null) {
                Object controller = loader.getController();
                try {
                    controllerClass.getMethod("initLevel").invoke(controller);
                } catch (NoSuchMethodException ex) {
                    System.err.println("Controller " + controllerClass.getSimpleName() + " chưa có method initLevel()");
                }
            }

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Không load được FXML: " + fxmlPath);
        }
    }
}
