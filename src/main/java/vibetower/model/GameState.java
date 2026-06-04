package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {

    private int silver;
    private int gold;
    private int level;
    private int experience;
    private int energy;

    private ArrayList<Item> inventory;
    private ArrayList<Item> placedItems;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 1;
        experience = 0;
        energy = 100;

        inventory = new ArrayList<>();
        placedItems = new ArrayList<>();
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

    public int getEnergy() {
        return energy;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public ArrayList<Item> getPlacedItems() {
        return placedItems;
    }

    public boolean buyItem(Item item) {
        if (silver >= item.getPrice()) {
            silver -= item.getPrice();
            inventory.add(item);
            addExperience(10);
            return true;
        }

        return false;
    }

    public void placeItem(Item item) {
        if (!placedItems.contains(item)) {
            placedItems.add(item);
        }
    }

    public void addSilver(int amount) {
        silver += amount;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
        }
    }

    public void restoreEnergy() {
        energy = 100;
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
}