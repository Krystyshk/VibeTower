import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    private String description;
    private int minLevel;
    private int xpReward;
    private int silverReward;
    private int goldReward;

    public Task(String name, String description, int minLevel, int xpReward, int silverReward, int goldReward) {
        this.name = name;
        this.description = description;
        this.minLevel = minLevel;
        this.xpReward = xpReward;
        this.silverReward = silverReward;
        this.goldReward = goldReward;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getSilverReward() {
        return silverReward;
    }

    public int getGoldReward() {
        return goldReward;
    }
}