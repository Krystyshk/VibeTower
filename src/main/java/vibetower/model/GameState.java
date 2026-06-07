package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L; // Фіксуємо версію для сумісності збережень

    private int silver;
    private int gold;
    private int level;
    private int experience;
    private int energy;

    private long cafeCooldownEndTime;
    private long shellCooldownEndTime;         // Кулдаун мушель (1 година)
    private long cinemaWorkCooldownEndTime;  // Кулдаун роботи кінотеатру (30 хв)

    private ArrayList<Item> inventory;
    private ArrayList<Item> placedItems;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 5;
        experience = 0;
        energy = 100;

        cafeCooldownEndTime = 0;

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

    public int getXp() {
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

    public long getCafeCooldownEndTime() {
        return cafeCooldownEndTime;
    }

    public void setCafeCooldownEndTime(long cafeCooldownEndTime) {
        this.cafeCooldownEndTime = cafeCooldownEndTime;
    }

    public long getShellCooldownEndTime() { return shellCooldownEndTime; }
    public void setShellCooldownEndTime(long t) { this.shellCooldownEndTime = t; }

    public long getCinemaWorkCooldownEndTime() { return cinemaWorkCooldownEndTime; }
    public void setCinemaWorkCooldownEndTime(long t) { this.cinemaWorkCooldownEndTime = t; }

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

    // Витратити срібло. Повертає true, якщо вистачало.
    public boolean spendSilver(int amount) {
        if (silver >= amount) {
            silver -= amount;
            return true;
        }
        return false;
    }

    // Витратити золото. Повертає true, якщо вистачало.
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    // Відновити енергію (не вище 100).
    public void addEnergy(int amount) {
        energy = Math.min(100, energy + amount);
    }

    // Додаткові поля для стану локацій
    private boolean janitorWorkDone = false;
    private boolean npcQuestDone    = false;
    private boolean cottonCandyDone = false;

    public boolean isJanitorWorkDone()  { return janitorWorkDone; }
    public void    setJanitorWorkDone() { this.janitorWorkDone = true; }
    public boolean isNpcQuestDone()     { return npcQuestDone; }
    public void    setNpcQuestDone()    { this.npcQuestDone = true; }
    public boolean isCottonCandyDone()  { return cottonCandyDone; }
    public void    setCottonCandyDone() { this.cottonCandyDone = true; }
}
