package vibetower.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveManager {

    private static final String FILE_NAME = "vibetower_save.dat";

    public static void saveGame(GameState gameState) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            out.writeObject(gameState);
            out.close();
            System.out.println("Прогрес збережено");
        } catch (Exception e) {
            System.out.println("Помилка збереження: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static GameState loadGame() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("Файл збереження не знайдено. Нова гра.");
            return new GameState();
        }
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME));
            GameState gameState = (GameState) in.readObject();
            in.close();
            return gameState;
        } catch (Exception e) {
            System.out.println("Збереження застаріле або пошкоджене, створюємо нову гру: " + e.getMessage());
            file.delete();
            return new GameState();
        }
    }
}
