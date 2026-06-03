package vibetower.model;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private int level;
    private long cafeCooldownEndTime = 0;
    private int xp;
    private int silver;
    private int gold;
    private int energy;

    private String apartment;

    private ArrayList<String> inventory;
    private ArrayList<String> placedFurniture;
    private ArrayList<String> completedTasks;

    public GameState() {
        this.level = 2;
        this.xp = 0;
        this.silver = 500;
        this.gold = 20;
        this.energy = 100;

        this.apartment = null;

        this.inventory = new ArrayList<>();
        this.placedFurniture = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
    }

    // ---------------------------
    // GETTERS
    // ---------------------------

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

    public String getApartment() {
        return apartment;
    }

    public ArrayList<String> getInventory() {
        return inventory;
    }

    public ArrayList<String> getPlacedFurniture() {
        return placedFurniture;
    }

    public ArrayList<String> getCompletedTasks() {
        return completedTasks;
    }

    // ---------------------------
    // APARTMENT
    // ---------------------------

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    // ---------------------------
    // ENERGY
    // ---------------------------

    public void restoreEnergy() {
        this.energy = 100;
    }

    public void spendEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
        } else {
            energy = 0;
        }
    }

    // ---------------------------
    // CURRENCY
    // ---------------------------

    public void addSilver(int amount) {
        silver += amount;
    }

    public void addGold(int amount) {
        gold += amount;
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

    // ---------------------------
    // INVENTORY
    // ---------------------------

    public void addItemToInventory(String item) {
        inventory.add(item);
    }

    public void placeFurniture(String item) {
        placedFurniture.add(item);
    }

    // ---------------------------
    // TASKS
    // ---------------------------

    public void completeTask(String taskName) {
        if (!completedTasks.contains(taskName)) {
            completedTasks.add(taskName);
        }
    }

    public boolean isTaskCompleted(String taskName) {
        return completedTasks.contains(taskName);
    }

    // ---------------------------
    // XP AND LEVELS
    // ---------------------------

    public void addXp(int amount) {
        xp += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int[] xpNeeded = {
                0,     // просто заглушка для 0 рівня
                100,   // 1 -> 2
                250,   // 2 -> 3
                450,   // 3 -> 4
                700,   // 4 -> 5
                1000,  // 5 -> 6
                1350,  // 6 -> 7
                1750,  // 7 -> 8
                2200,  // 8 -> 9
                2700   // 9 -> 10
        };

        while (level < 10 && xp >= xpNeeded[level]) {
            xp -= xpNeeded[level];
            level++;

            JOptionPane.showMessageDialog(
                    null,
                    "Вітаємо! Ви перейшли на " + level + " рівень!"
            );
        }
    }

    public String getLevelDescription() {
        switch (level) {
            case 1:
                return "Навчання та старт гри";
            case 2:
                return "Вибір власної квартири";
            case 3:
                return "Робота офіціантом і базовий ремонт";
            case 4:
                return "Парк і пляж";
            case 5:
                return "Кінотеатр";
            case 6:
                return "Салон краси";
            case 7:
                return "Нові меблі та спальня";
            case 8:
                return "Модні завдання та новий декор";
            case 9:
                return "Кухня та велика квартира";
            case 10:
                return "Максимальний рівень першої версії гри";
            default:
                return "Невідомий рівень";
        }
    }

    public long getCafeCooldownEndTime() {
        return cafeCooldownEndTime;
    }

    public void setCafeCooldownEndTime(long timeMillis) {
        this.cafeCooldownEndTime = timeMillis;
    }
}