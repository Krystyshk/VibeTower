package vibetower.model;
import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "vibetower_save.dat";

    public static void saveGame(GameState gameState) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(gameState);
        } catch (IOException e) {
            System.out.println("Помилка збереження гри: " + e.getMessage());
        }
    }

    public static GameState loadGame() {
        File file = new File(SAVE_FILE);

        if (!file.exists()) {
            return new GameState();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new GameState();
        }
    }
}