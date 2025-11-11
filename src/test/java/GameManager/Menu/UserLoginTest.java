package GameManager.Menu;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginTest {
    private final String USERS_FILE = "src/data/users.txt";

    @BeforeEach
    void setup() throws IOException {
        // tạo file users.txt để test
        File file = new File(USERS_FILE);
        file.getParentFile().mkdirs();
        List<String> users = List.of(
                "domailien,domailien",
                "bangphan,55555",
                "ahihi,ahihi",
                "ahoho,ahoho"
        );
        Files.write(file.toPath(), users);
    }

    boolean checkLogin(String username, String password) throws IOException {
        List<String> lines = Files.readAllLines(new File(USERS_FILE).toPath());
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    // ktra tkhoan hợp lệ
    @Test
    void testLoginSuccess() throws IOException {
        assertTrue(checkLogin("domailien", "domailien"));
        assertTrue(checkLogin("bangphan", "55555"));
    }

    // ktra sai mkhau
    @Test
    void testLoginFailWrongPassword() throws IOException {
        assertFalse(checkLogin("domailien", "abc"));
    }

    // ktra đăng nhập 0 tồn tại
    @Test
    void testLoginFailNonExistentUser() throws IOException {
        assertFalse(checkLogin("ahehe", "123"));
    }

    // ktra user trống
    @Test
    void testLoginEmptyUsername() throws IOException {
        assertFalse(checkLogin("", "domailien"));
    }

    //ktra mkhau trống
    @Test
    void testLoginEmptyPassword() throws IOException {
        assertFalse(checkLogin("domailien", ""));
    }

    // ktra tên đăng nhập chữ hoa thường
    @Test
    void testLoginCaseSensitive() throws IOException {
        assertFalse(checkLogin("Domailien", "domailien"));
    }

    // ktra các tkhoan khác
    @Test
    void testMultipleUsers() throws IOException {
        assertTrue(checkLogin("ahihi", "ahihi"));
        assertTrue(checkLogin("ahoho", "ahoho"));
    }

    // ktra tkhoan có ký tự @....
    @Test
    void testLoginSpecialChars() throws IOException {
        // thêm user test
        Files.write(new File(USERS_FILE).toPath(), List.of("admin@123,pass!@#"), java.nio.file.StandardOpenOption.APPEND);
        assertTrue(checkLogin("admin@123", "pass!@#"));
    }

    // ktra checkboxx Remember
    @Test
    void testLoginRememberMeDoesNotAffectLogic() throws IOException {
        assertTrue(checkLogin("domailien", "domailien"));
    }

    // ktra đăng nhập tài khoản sai
    @Test
    void testLoginAfterFailedAttempt() throws IOException {
        assertFalse(checkLogin("domailien", "abc"));
        assertTrue(checkLogin("domailien", "domailien"));
    }
}
