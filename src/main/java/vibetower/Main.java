package vibetower;

import vibetower.model.GameState;
import vibetower.model.SaveManager;
import vibetower.ui.StartFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState gameState = SaveManager.loadGame();
            StartFrame startFrame = new StartFrame(gameState);
            startFrame.setVisible(true);
        });
    }
}
