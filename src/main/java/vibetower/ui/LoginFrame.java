package vibetower.ui;

import vibetower.model.GameState;
import vibetower.model.HomeFrame;
import vibetower.model.SaveManager;

public class LoginFrame extends StartFrame {

    public LoginFrame(GameState gameState) {
        super(gameState);
    }

    private void openMainGame() {
        GameState gameState = SaveManager.loadGame();
        HomeFrame homeFrame = new HomeFrame(gameState);
        homeFrame.setVisible(true);
        dispose();
    }
}