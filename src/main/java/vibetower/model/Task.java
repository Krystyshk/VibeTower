package vibetower.model;

import java.io.Serializable;

public class Task implements Serializable {

    private String name;
    private String description;
    private int silverReward;
    private int experienceReward;

    public Task(String name, String description, int silverReward, int experienceReward) {
        this.name = name;
        this.description = description;
        this.silverReward = silverReward;
        this.experienceReward = experienceReward;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSilverReward() {
        return silverReward;
    }

    public int getExperienceReward() {
        return experienceReward;
    }
}
