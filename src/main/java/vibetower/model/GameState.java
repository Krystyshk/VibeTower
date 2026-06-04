package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {

    private int silver;
    private int gold;
    private int level;
    private int experience;
    private ArrayList<Item> inventory;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 1;
        experience = 0;
        inventory = new ArrayList<>();
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

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public boolean buyItem(Item item) {
        if (silver >= item.getPrice()) {
            silver = silver - item.getPrice();
            inventory.add(item);
            addExperience(10);
            return true;
        }

        return false;
    }

    public void addSilver(int amount) {
        silver = silver + amount;
    }

    public void addGold(int amount) {
        gold = gold + amount;
    }

    public void addExperience(int amount) {
        experience = experience + amount;

        if (experience >= level * 100) {
            experience = 0;
            level = level + 1;
            silver = silver + 100;
            gold = gold + 2;
        }
    }
}