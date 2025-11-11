package GameManager.Menu;
public class Session {
    private static String currentUsername;
    public static void setUsername(String username) {
        currentUsername = username;
    }

    public static String getUsername() {
        return currentUsername;
    }
}
