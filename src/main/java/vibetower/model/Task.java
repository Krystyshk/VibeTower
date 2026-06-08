package vibetower.model;

import java.io.Serializable;

public class Task implements Serializable {

    private final String id;
    private final String title;
    private final String description;
    private final int requiredLevel;
    private final int energyCost;
    private final int rewardXp;
    private final int rewardSilver;
    private final int rewardGold;
    private final Item rewardItem;

    public Task(String id, String title, String description,
                int requiredLevel, int energyCost,
                int rewardXp, int rewardSilver, int rewardGold) {
        this(id, title, description, requiredLevel, energyCost, rewardXp, rewardSilver, rewardGold, null);
    }

    public Task(String id, String title, String description,
                int requiredLevel, int energyCost,
                int rewardXp, int rewardSilver, int rewardGold,
                Item rewardItem) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredLevel = requiredLevel;
        this.energyCost = energyCost;
        this.rewardXp = rewardXp;
        this.rewardSilver = rewardSilver;
        this.rewardGold = rewardGold;
        this.rewardItem = rewardItem;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getEnergyCost() { return energyCost; }
    public int getRewardXp() { return rewardXp; }
    public int getRewardSilver() { return rewardSilver; }
    public int getRewardGold() { return rewardGold; }
    public Item getRewardItem() { return rewardItem; }
}