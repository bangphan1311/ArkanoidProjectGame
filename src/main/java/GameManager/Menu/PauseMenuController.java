package GameManager.Menu;

import GameManager.BaseGameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PauseMenuController {

    @FXML private Button resumeBtn;
    @FXML private Button restartBtn;
    @FXML private Button homeBtn;
    @FXML private Button nextBtn;
    @FXML private Button prevBtn;

    private BaseGameController gameController;

    //  BaseGameController truyền tham chiếu qua
    public void setBaseGameController(BaseGameController controller) {
        this.gameController = controller;
        setupButtons();
    }

    // xử lý các hành động cho nút bấm
    private void setupButtons() {
        // Resume
        resumeBtn.setOnAction(e -> {
            if (gameController != null) {
                gameController.hidePauseMenu(); // Ẩn menu tạm dừng
            }
        });

        // chs lại
        restartBtn.setOnAction(e -> {
            if (gameController != null) {
                gameController.setupLevelController(); // Chạy lại level
                gameController.hidePauseMenu();
            }
        });

        // home
        homeBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RenderView/Menu/MainMenu.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) gameController.getGamePane().getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // next
        nextBtn.setOnAction(e -> {
            if (gameController != null) {
                gameController.switchLevel(1);
            }
        });

        // previous
        prevBtn.setOnAction(e -> {
            if (gameController != null) {
                gameController.switchLevel(-1);
            }
        });
    }
}
