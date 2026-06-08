package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class GameState implements Serializable {

    private static final long serialVersionUID = 2L;

    private int silver;
    private int gold;
    private int level;
    private int experience;
    private int energy;

    private String selectedApartment;
    private String wallpaper;
    private String floor;

    private boolean secondRoomBought;
    private boolean bathroomBought;
    private boolean bedroomBought;
    private boolean kitchenBought;
    private boolean bigRoomBought;

    private long cafeCooldownEndTime;
    private long shellCooldownEndTime;
    private long cinemaWorkCooldownEndTime;

    private ArrayList<Item> inventory;
    private ArrayList<Item> placedItems;
    private HashSet<String> completedTasks;

    private boolean janitorWorkDone;
    private boolean npcQuestDone;
    private boolean cottonCandyDone;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 1;
        experience = 0;
        energy = 100;

        selectedApartment = "";

        wallpaper = "Білі шпалери";
        floor = "Коричневий паркет";

        secondRoomBought = false;
        bathroomBought = false;
        bedroomBought = false;
        kitchenBought = false;
        bigRoomBought = false;

        cafeCooldownEndTime = 0;
        shellCooldownEndTime = 0;
        cinemaWorkCooldownEndTime = 0;

        inventory = new ArrayList<>();
        placedItems = new ArrayList<>();
        completedTasks = new HashSet<>();

        janitorWorkDone = false;
        npcQuestDone = false;
        cottonCandyDone = false;
    }

    public void fixAfterLoad() {
        if (inventory == null) {
            inventory = new ArrayList<>();
        }

        if (placedItems == null) {
            placedItems = new ArrayList<>();
        }

        if (completedTasks == null) {
            completedTasks = new HashSet<>();
        }

        if (selectedApartment == null || selectedApartment.isEmpty()) {
            selectedApartment = "";
        }

        if (wallpaper == null) {
            wallpaper = "Білі шпалери";
        }

        if (floor == null) {
            floor = "Коричневий паркет";
        }

        if (level < 1) {
            level = 1;
        }

        if (level > 10) {
            level = 10;
        }

        if (silver < 0) {
            silver = 0;
        }

        if (gold < 0) {
            gold = 0;
        }

        if (experience < 0) {
            experience = 0;
        }

        if (energy < 0) {
            energy = 0;
        }

        if (energy > 100) {
            energy = 100;
        }
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

    public void setSilver(int silver) {
        this.silver = Math.max(0, silver);
    }

    public void setGold(int gold) {
        this.gold = Math.max(0, gold);
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(100, energy));
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }

    public ArrayList<Item> getInventory() {
        fixAfterLoad();
        return inventory;
    }

    public ArrayList<Item> getPlacedItems() {
        fixAfterLoad();
        return placedItems;
    }

    public String getSelectedApartment() {
        fixAfterLoad();
        return selectedApartment;
    }

    public boolean hasSelectedApartment() {
        fixAfterLoad();
        return selectedApartment != null && !selectedApartment.isEmpty();
    }

    public void selectApartment(String apartmentName) {
        setApartmentType(apartmentName);
        addExperience(100);
    }

    // Метод повертає тип квартири у короткому форматі: blue, pink або peach
    public String getApartmentType() {
        fixAfterLoad();

        if (selectedApartment == null || selectedApartment.isEmpty()) {
            return "blue";
        }

        if (selectedApartment.equalsIgnoreCase("blue")
                || selectedApartment.equalsIgnoreCase("Синя квартира")
                || selectedApartment.equalsIgnoreCase("blue_apartment")) {
            return "blue";
        }

        if (selectedApartment.equalsIgnoreCase("pink")
                || selectedApartment.equalsIgnoreCase("Рожева квартира")
                || selectedApartment.equalsIgnoreCase("pink_apartment")) {
            return "pink";
        }

        if (selectedApartment.equalsIgnoreCase("peach")
                || selectedApartment.equalsIgnoreCase("Персикова квартира")
                || selectedApartment.equalsIgnoreCase("peach_apartment")) {
            return "peach";
        }

        return "blue";
    }

    // Метод встановлює вибрану квартиру
    public void setApartmentType(String apartmentType) {
        if (apartmentType == null || apartmentType.isEmpty()) {
            selectedApartment = "blue";
            return;
        }

        if (apartmentType.equalsIgnoreCase("blue")
                || apartmentType.equalsIgnoreCase("Синя квартира")
                || apartmentType.equalsIgnoreCase("blue_apartment")) {
            selectedApartment = "blue";
        } else if (apartmentType.equalsIgnoreCase("pink")
                || apartmentType.equalsIgnoreCase("Рожева квартира")
                || apartmentType.equalsIgnoreCase("pink_apartment")) {
            selectedApartment = "pink";
        } else if (apartmentType.equalsIgnoreCase("peach")
                || apartmentType.equalsIgnoreCase("Персикова квартира")
                || apartmentType.equalsIgnoreCase("peach_apartment")) {
            selectedApartment = "peach";
        } else {
            selectedApartment = apartmentType;
        }
    }

    public String getWallpaper() {
        fixAfterLoad();
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getFloor() {
        fixAfterLoad();
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public boolean isSecondRoomBought() {
        return secondRoomBought;
    }

    public boolean isBathroomBought() {
        return bathroomBought;
    }

    public boolean isBedroomBought() {
        return bedroomBought;
    }

    public boolean isKitchenBought() {
        return kitchenBought;
    }

    public boolean isBigRoomBought() {
        return bigRoomBought;
    }

    public String buyRoom(String roomName) {
        int requiredLevel;
        int price;

        switch (roomName) {
            case "Друга кімната":
                requiredLevel = 3;
                price = 700;

                if (secondRoomBought) {
                    return "Ця кімната вже куплена.";
                }

                break;

            case "Санвузол":
                requiredLevel = 3;
                price = 450;

                if (bathroomBought) {
                    return "Санвузол вже куплений.";
                }

                break;

            case "Спальня":
                requiredLevel = 5;
                price = 1200;

                if (bedroomBought) {
                    return "Спальня вже куплена.";
                }

                break;

            case "Кухня":
                requiredLevel = 7;
                price = 1800;

                if (kitchenBought) {
                    return "Кухня вже куплена.";
                }

                break;

            default:
                requiredLevel = 9;
                price = 2500;

                if (bigRoomBought) {
                    return "Велика кімната вже куплена.";
                }

                break;
        }

        if (level < requiredLevel) {
            return "Недоступно. Потрібен " + requiredLevel + " рівень.";
        }

        if (!spendSilver(price)) {
            return "Недостатньо срібла. Потрібно " + price + ".";
        }

        if (roomName.equals("Друга кімната")) {
            secondRoomBought = true;
        } else if (roomName.equals("Санвузол")) {
            bathroomBought = true;
        } else if (roomName.equals("Спальня")) {
            bedroomBought = true;
        } else if (roomName.equals("Кухня")) {
            kitchenBought = true;
        } else {
            bigRoomBought = true;
        }

        addExperience(50);

        return roomName + " куплено! Кімната додана до квартири.";
    }

    public long getCafeCooldownEndTime() {
        return cafeCooldownEndTime;
    }

    public void setCafeCooldownEndTime(long cafeCooldownEndTime) {
        this.cafeCooldownEndTime = cafeCooldownEndTime;
    }

    public long getShellCooldownEndTime() {
        return shellCooldownEndTime;
    }

    public void setShellCooldownEndTime(long shellCooldownEndTime) {
        this.shellCooldownEndTime = shellCooldownEndTime;
    }

    public long getCinemaWorkCooldownEndTime() {
        return cinemaWorkCooldownEndTime;
    }

    public void setCinemaWorkCooldownEndTime(long cinemaWorkCooldownEndTime) {
        this.cinemaWorkCooldownEndTime = cinemaWorkCooldownEndTime;
    }

    public boolean buyItem(Item item) {
        fixAfterLoad();

        if (level < item.getRequiredLevel()) {
            return false;
        }

        boolean paid;

        if (item.isGoldItem()) {
            paid = spendGold(item.getPrice());
        } else {
            paid = spendSilver(item.getPrice());
        }

        if (paid) {
            inventory.add(item);
            addExperience(10);
            return true;
        }

        return false;
    }

    public String tryBuyItem(Item item) {
        fixAfterLoad();

        if (level < item.getRequiredLevel()) {
            return "Недоступно. Потрібен " + item.getRequiredLevel() + " рівень.";
        }

        boolean paid;

        if (item.isGoldItem()) {
            paid = spendGold(item.getPrice());
        } else {
            paid = spendSilver(item.getPrice());
        }

        if (!paid) {
            if (item.isGoldItem()) {
                return "Недостатньо золота для покупки.";
            } else {
                return "Недостатньо срібла для покупки.";
            }
        }

        inventory.add(item);
        addExperience(10);

        return item.getName() + " додано в інвентар!";
    }

    public void placeItem(Item item) {
        fixAfterLoad();

        if (!placedItems.contains(item)) {
            placedItems.add(item);
        }
    }

    public void removePlacedItem(Item item) {
        fixAfterLoad();
        placedItems.remove(item);
    }

    public void addSilver(int amount) {
        silver = Math.max(0, silver + amount);
    }

    public void addGold(int amount) {
        gold = Math.max(0, gold + amount);
    }

    public void addExperience(int amount) {
        experience += Math.max(0, amount);

        while (level < 10 && experience >= getXpToNextLevel()) {
            experience -= getXpToNextLevel();
            level++;
            silver += 100;
            gold += 2;
            energy = 100;
        }

        if (level >= 10) {
            level = 10;
            experience = Math.max(0, experience);
        }
    }

    public void addXp(int amount) {
        addExperience(amount);
    }

    public int getXpToNextLevel() {
        int[] requirements = {
                100,
                250,
                450,
                700,
                1000,
                1350,
                1750,
                2200,
                2700
        };

        if (level >= 10) {
            return 0;
        }

        return requirements[level - 1];
    }

    // Метод потрібен для відображення XP у HomeFrame
    public int getExperienceToNextLevel() {
        return getXpToNextLevel();
    }

    public String getLevelUnlockText() {
        switch (level) {
            case 1:
                return "До 2 рівня: вибір власної квартири";

            case 2:
                return "До 3 рівня: робота офіціантом і базовий ремонт";

            case 3:
                return "До 4 рівня: парк і пляж";

            case 4:
                return "До 5 рівня: кінотеатр";

            case 5:
                return "До 6 рівня: салон краси";

            case 6:
                return "До 7 рівня: нові меблі та спальня";

            case 7:
                return "До 8 рівня: модні завдання та декор";

            case 8:
                return "До 9 рівня: кухня та велика квартира";

            case 9:
                return "До 10 рівня: преміум-предмети";

            default:
                return "Максимальний рівень першої версії гри";
        }
    }

    public void spendEnergy(int amount) {
        energy = Math.max(0, energy - Math.max(0, amount));
    }

    public void restoreEnergy() {
        energy = 100;
    }

    public void addEnergy(int amount) {
        energy = Math.min(100, energy + Math.max(0, amount));
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

    public boolean isTaskCompleted(String taskId) {
        fixAfterLoad();
        return completedTasks.contains(taskId);
    }

    public void completeTask(String taskId) {
        fixAfterLoad();
        completedTasks.add(taskId);
    }

    public boolean isJanitorWorkDone() {
        return janitorWorkDone;
    }

    public void setJanitorWorkDone() {
        janitorWorkDone = true;
    }

    public boolean isNpcQuestDone() {
        return npcQuestDone;
    }

    public void setNpcQuestDone() {
        npcQuestDone = true;
    }

    public boolean isCottonCandyDone() {
        return cottonCandyDone;
    }

    public void setCottonCandyDone() {
        cottonCandyDone = true;
    }
}