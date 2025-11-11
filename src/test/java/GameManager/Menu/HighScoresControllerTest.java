package GameManager.Menu;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class HighScoresControllerTest {
    private HighScoresController controller;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        controller = new HighScoresController();

        // tạo file để test
        tempFile = Files.createTempFile("testhighscores", ".txt");
        tempFile.toFile().deleteOnExit();

        try {
            var field = HighScoresController.class.getDeclaredField("highscoreFile");
            field.setAccessible(true);
            field.set(controller, tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    // lưu điểm tên
    @Test
    void test1_SaveSingleScore() {
        controller.saveScore("A", 500);
        assertEquals(500, controller.getHighScore("A"));
    }

    // điểm thấp hơn -> 0 ghi đè
    @Test
    void test2_UpdateLowerScoreDoesNotOverride() {
        controller.saveScore("B", 700);
        controller.saveScore("B", 600);
        assertEquals(700, controller.getHighScore("B"));
    }

    // điểm cao hơn -> ghi đè
    @Test
    void test3_UpdateHigherScoreOverrides() {
        controller.saveScore("C", 400);
        controller.saveScore("C", 800); // cao hơn
        assertEquals(800, controller.getHighScore("C"));
    }

    // lưu điểm cho user
    @Test
    void test4_MultipleUsers() {
        controller.saveScore("A", 500);
        controller.saveScore("B", 700);
        controller.saveScore("C", 600);

        assertEquals(500, controller.getHighScore("A"));
        assertEquals(700, controller.getHighScore("B"));
        assertEquals(600, controller.getHighScore("C"));
    }

    // user 0 pbt hoa / thg
    @Test
    void test5_CaseInsensitiveUsernames() {
        controller.saveScore("A", 300);
        controller.saveScore("a", 500);
        assertEquals(500, controller.getHighScore("A"));
        assertEquals(500, controller.getHighScore("a"));
    }

    // ktra dữ liệu đ luu
    @Test
    void test6_FilePersistence() throws IOException {
        controller.saveScore("A", 400);
        controller.saveScore("B", 900);

        var lines = Files.readAllLines(tempFile);
        assertTrue(lines.stream().anyMatch(l -> l.equalsIgnoreCase("a;400")));
        assertTrue(lines.stream().anyMatch(l -> l.equalsIgnoreCase("b;900")));
    }

    // chưa lưu điểm -> =0
    @Test
    void test7_ZeroScoreForUnknownUser() {
        assertEquals(0, controller.getHighScore("Unknown"));
    }

    // 1 user nh điểm và giữu điểm cao nhất
    @Test
    void test8_MultipleScoresSameUser() {
        controller.saveScore("Domailien", 1900);
        controller.saveScore("Domailien", 1100);
        assertEquals(1900, controller.getHighScore("domailien"));
    }

    // ktra ghi nh điểm, giữu điểm cao nhất
    @Test
    void test9_SequentialScores() {
        controller.saveScore("Ahoho", 1000);
        controller.saveScore("Ahoho", 1100);
        controller.saveScore("Ahoho", 2700);
        assertEquals(2700, controller.getHighScore("ahoho"));
    }

    // lưu điểm cho nh ng dùng và hiện điểm cụ thể
    @Test
    void test10_ManyUsersTopCheck() {
        for (int i = 1; i <= 15; i++) {
            controller.saveScore("User" + i, i * 100);
        }
        assertEquals(1500, controller.getHighScore("User15"));
        assertEquals(100, controller.getHighScore("User1"));
        assertEquals(600, controller.getHighScore("User6"));
    }
}
