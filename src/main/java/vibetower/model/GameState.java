package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private int silver;
    private int gold;
    private int level;
    private int experience;
    private int energy;

    private long cafeCooldownEndTime;

    private ArrayList<Item> inventory;
    private ArrayList<Item> placedItems;

    private Map<String, Item> equippedItems;

    private String characterName;
    private String gender;
    private String skinColor;
    private String hairStyle;
    private String hairColor;
    private String eyeColor;

    private boolean janitorWorkDone = false;
    private boolean npcQuestDone = false;
    private boolean cottonCandyDone = false;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 5;
        experience = 0;
        energy = 100;

        cafeCooldownEndTime = 0;

        inventory = new ArrayList<>();
        placedItems = new ArrayList<>();
        equippedItems = new HashMap<>();

        characterName = "Кристина";
        gender = "Жіночий";
        skinColor = "Світла";
        hairStyle = "Пучок";
        hairColor = "Каштановий";
        eyeColor = "Карі";
    }

    public int getSilver() {
        return silver;
    }

    public int getGold() {
        return gold;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getXp() {
        return experience;
    }

    public int getEnergy() {
        return energy;
    }

    public ArrayList<Item> getInventory() {
        if (inventory == null) {
            inventory = new ArrayList<>();
        }

        return inventory;
    }

    public ArrayList<Item> getPlacedItems() {
        if (placedItems == null) {
            placedItems = new ArrayList<>();
        }

        return placedItems;
    }

    public Map<String, Item> getEquippedItems() {
        if (equippedItems == null) {
            equippedItems = new HashMap<>();
        }

        return equippedItems;
    }

    public long getCafeCooldownEndTime() {
        return cafeCooldownEndTime;
    }

    public void setCafeCooldownEndTime(long cafeCooldownEndTime) {
        this.cafeCooldownEndTime = cafeCooldownEndTime;
    }

    public boolean buyItem(Item item) {
        if (item == null) {
            return false;
        }

        if (silver >= item.getPrice()) {
            silver -= item.getPrice();
            getInventory().add(item);
            addExperience(10);
            return true;
        }

        return false;
    }

    public boolean buyClothingItem(Item item) {
        if (item == null) {
            return false;
        }

        if (getInventory().contains(item)) {
            return false;
        }

        if (level < item.getMinLevel()) {
            return false;
        }

        boolean paid;

        if (item.getCurrency().equals("gold")) {
            paid = spendGold(item.getPrice());
        } else {
            paid = spendSilver(item.getPrice());
        }

        if (paid) {
            getInventory().add(item);
            addExperience(10);
            return true;
        }

        return false;
    }

    public void equipItem(Item item) {
        if (item == null) {
            return;
        }

        getEquippedItems().put(item.getCategory(), item);
    }

    public void placeItem(Item item) {
        if (item == null) {
            return;
        }

        if (!getPlacedItems().contains(item)) {
            getPlacedItems().add(item);
        }
    }

    public void addSilver(int amount) {
        silver += amount;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void addExperience(int amount) {
        experience += amount;

        if (experience >= level * 100) {
            experience = 0;
            level++;
            silver += 100;
            gold += 2;
            energy = 100;
        }
    }

    public void addXp(int amount) {
        addExperience(amount);
    }

    public void spendEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
        }
    }

    public void restoreEnergy() {
        energy = 100;
    }

    public boolean spendSilver(int amount) {
        if (silver >= amount) {
            silver -= amount;
            return true;
        }

        return false;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }

        return false;
    }

    public void addEnergy(int amount) {
        energy = Math.min(100, energy + amount);
    }

    public String getCharacterName() {
        return characterName;
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

    public void setCharacterInfo(String characterName, String gender) {
        this.characterName = characterName;
        this.gender = gender;
    }

    public void setAppearance(String skinColor, String hairStyle, String hairColor, String eyeColor) {
        this.skinColor = skinColor;
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
    }

    public boolean isJanitorWorkDone() {
        return janitorWorkDone;
    }

    public void setJanitorWorkDone() {
        this.janitorWorkDone = true;
    }

    public boolean isNpcQuestDone() {
        return npcQuestDone;
    }

    public void setNpcQuestDone() {
        this.npcQuestDone = true;
    }

    public boolean isCottonCandyDone() {
        return cottonCandyDone;
    }

    public void setCottonCandyDone() {
        this.cottonCandyDone = true;
    }
}