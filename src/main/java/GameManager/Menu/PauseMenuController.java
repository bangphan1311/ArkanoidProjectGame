package GameManager.Menu;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;

public class PauseMenuController {

    @FXML private AnchorPane pauseRoot;
    @FXML private Button homeBtn, resumeBtn, restartBtn, nextBtn, prevBtn;

    private Runnable resumeCallback;
    private Runnable restartCallback;
    private Runnable homeCallback;
    private Runnable nextCallback;
    private Runnable prevCallback;

    public void setCallbacks(Runnable resume, Runnable restart, Runnable home, Runnable next, Runnable prev) {
        this.resumeCallback = resume;
        this.restartCallback = restart;
        this.homeCallback = home;
        this.nextCallback = next;
        this.prevCallback = prev;

        // Gán hành vi cho nút
        resumeBtn.setOnAction(e -> { if (resumeCallback != null) resumeCallback.run(); });
        restartBtn.setOnAction(e -> { if (restartCallback != null) restartCallback.run(); });
        homeBtn.setOnAction(e -> { if (homeCallback != null) homeCallback.run(); });
        nextBtn.setOnAction(e -> { if (nextCallback != null) nextCallback.run(); });
        prevBtn.setOnAction(e -> { if (prevCallback != null) prevCallback.run(); });
    }

    public AnchorPane getRootPane() {
        return pauseRoot;
    }
}