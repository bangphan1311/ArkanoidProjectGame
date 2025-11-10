import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent; // ✅ Sửa: Dùng Parent (chung nhất)
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

// ✅ THÊM CÁC IMPORT NÀY
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class Main extends Application {

    // ✅ 1. Biến để giữ file âm thanh
    private AudioClip clickSound;

    @Override
    public void start(Stage stage) throws Exception {

        // ✅ 2. Tải file âm thanh (click.wav) MỘT LẦN DUY NHẤT
        try {
            URL resource = getClass().getResource("/sounds/Click.wav"); // (Đảm bảo tên file đúng)
            if (resource != null) {
                clickSound = new AudioClip(resource.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file /sounds/click.wav");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Tải FXML (SignIn.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RenderView/Menu/SignIn.fxml"));
        Parent root = fxmlLoader.load(); // ✅ Sửa: Tải vào Parent
        Scene scene = new Scene(root);
        stage.setTitle("Sign In");
        stage.setScene(scene);

        // ✅ 4. THÊM BỘ LỌC SỰ KIỆN TOÀN CỤC VÀO STAGE
        // Code này sẽ "lắng nghe" mọi cú click chuột trong toàn bộ ứng dụng
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            // Kiểm tra xem thứ vừa được nhấn có phải là Button (hoặc nằm trong Button) không
            if (event.getTarget() instanceof Button || (event.getTarget() instanceof Node && ((Node)event.getTarget()).getParent() instanceof Button)) {
                // Nếu đúng, phát âm thanh
                playSound(clickSound);
            }
        });

        // 5. Hiển thị
        stage.show();

        // 6. Căn giữa (code của bạn)
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    // ✅ 7. THÊM HÀM PHÁT ÂM THANH
    private void playSound(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}