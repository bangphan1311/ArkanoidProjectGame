package GameManager.Menu;  // ⚠️ Phải trùng với nơi bạn import trong GameOverController

public class Session {
    private static String currentUsername;

    public static void setUsername(String username) {
        currentUsername = username;
    }

    public static String getUsername() {
        return currentUsername;
    }
}
