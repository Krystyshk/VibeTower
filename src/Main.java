import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState gameState = SaveManager.loadGame();
            HomeFrame homeFrame = new HomeFrame(gameState);
            homeFrame.setVisible(true);
        });
    }
}