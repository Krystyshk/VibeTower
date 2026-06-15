package vibetower.model;

import java.time.LocalDate;
import java.util.UUID;

public class PlayerProfile {

    private String id;
    private String name;
    private String gender;

    private String skinColor;
    private String hairStyle;
    private String hairColor;
    private String eyeColor;
    private String outfit;

    private int level;
    private int xp;
    private int silver;
    private int gold;
    private int energy;

    private LocalDate createdAt;

    public PlayerProfile(String name, String gender, String skinColor, String hairStyle,
                         String hairColor, String eyeColor, String outfit) {
        this.id = generateId();
        this.name = name;
        this.gender = gender;

        this.skinColor = skinColor;
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.outfit = outfit;

        this.level = 1;
        this.xp = 0;
        this.silver = 500;
        this.gold = 25;
        this.energy = 100;

        this.createdAt = LocalDate.now();
    }

    private String generateId() {
        String shortId = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "VT-" + shortId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public String getHairColor() {
        return hairColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public String getOutfit() {
        return outfit;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getSilver() {
        return silver;
    }

    public int getGold() {
        return gold;
    }

    public int getEnergy() {
        return energy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
