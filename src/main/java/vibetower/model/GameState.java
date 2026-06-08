package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class GameState implements Serializable {

    private static final long serialVersionUID = 4L;

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
    private long lastDailyRewardTime;

    private ArrayList<Item> inventory;
    private ArrayList<Item> placedItems;
    private HashSet<String> completedTasks;
    private HashSet<String> rewardTakenTasks;

    private String activeTaskId;

    private boolean janitorWorkDone;
    private boolean npcQuestDone;
    private boolean cottonCandyDone;

    public GameState() {
        silver = 500;
        gold = 20;
        level = 1;
        experience = 0;
        energy = 100;

        selectedApartment = "blue";
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
        lastDailyRewardTime = 0;

        inventory = new ArrayList<>();
        placedItems = new ArrayList<>();
        completedTasks = new HashSet<>();
        rewardTakenTasks = new HashSet<>();

        activeTaskId = "";

        janitorWorkDone = false;
        npcQuestDone = false;
        cottonCandyDone = false;
    }

    public void fixAfterLoad() {
        if (inventory == null) inventory = new ArrayList<>();
        if (placedItems == null) placedItems = new ArrayList<>();
        if (completedTasks == null) completedTasks = new HashSet<>();
        if (rewardTakenTasks == null) rewardTakenTasks = new HashSet<>();
        if (activeTaskId == null) activeTaskId = "";

        if (selectedApartment == null || selectedApartment.isEmpty()) selectedApartment = "blue";
        if (wallpaper == null) wallpaper = "Білі шпалери";
        if (floor == null) floor = "Коричневий паркет";

        if (level < 1) level = 1;
        if (level > 10) level = 10;

        if (silver < 0) silver = 0;
        if (gold < 0) gold = 0;
        if (experience < 0) experience = 0;
        if (energy < 0) energy = 0;
        if (energy > 100) energy = 100;
    }

    public void startTask(String taskId) {
        fixAfterLoad();
        activeTaskId = taskId == null ? "" : taskId;
        SaveManager.saveGame(this);
    }

    public String getActiveTaskId() {
        fixAfterLoad();
        return activeTaskId;
    }

    public boolean hasActiveTask(String taskId) {
        fixAfterLoad();
        return taskId != null && taskId.equals(activeTaskId);
    }

    public void clearActiveTask() {
        activeTaskId = "";
        SaveManager.saveGame(this);
    }

    public boolean isTaskCompleted(String taskId) {
        fixAfterLoad();
        return completedTasks.contains(taskId);
    }

    public boolean isRewardTaken(String taskId) {
        fixAfterLoad();
        return rewardTakenTasks.contains(taskId);
    }

    public String finishActiveTask(Task task) {
        fixAfterLoad();

        if (task == null) return "";
        if (!hasActiveTask(task.getId())) return "";
        if (completedTasks.contains(task.getId())) return "";

        if (energy < task.getEnergyCost()) {
            return "Недостатньо енергії для завершення завдання.";
        }

        spendEnergy(task.getEnergyCost());

        completedTasks.add(task.getId());
        rewardTakenTasks.add(task.getId());
        activeTaskId = "";

        addSilver(task.getRewardSilver());
        addGold(task.getRewardGold());
        String xpMessage = addExperience(task.getRewardXp());

        if (task.getRewardItem() != null) {
            inventory.add(task.getRewardItem());
        }

        SaveManager.saveGame(this);

        return "✅ Завдання виконано!\n\n"
                + task.getTitle()
                + "\n\nНагорода:"
                + "\n+ " + task.getRewardSilver() + " срібла"
                + "\n+ " + task.getRewardGold() + " золота"
                + "\n+ " + task.getRewardXp() + " XP"
                + (task.getRewardItem() != null ? "\n+ предмет: " + task.getRewardItem().getName() : "")
                + (xpMessage == null || xpMessage.isEmpty() ? "" : "\n\n" + xpMessage);
    }

    public String addExperience(int amount) {
        fixAfterLoad();

        if (amount <= 0) return "";

        if (level >= 10) {
            experience = 0;
            SaveManager.saveGame(this);
            return "Максимальний рівень першої версії гри.";
        }

        experience += amount;

        StringBuilder message = new StringBuilder();

        while (level < 10 && experience >= getXpToNextLevel()) {
            experience -= getXpToNextLevel();
            level++;

            silver += 100;
            gold += 2;
            energy = 100;

            message.append("🎉 Вітаємо! Новий рівень: ")
                    .append(level)
                    .append("\n+100 срібла")
                    .append("\n+2 золота")
                    .append("\nЕнергію відновлено.");

            if (level == 2) message.append("\nВідкрито вибір власної квартири.");
            if (level == 3) message.append("\nВідкрито базовий ремонт.");
            if (level == 5) message.append("\nВідкрито спальню.");
            if (level == 7) message.append("\nВідкрито кухню.");
            if (level == 10) message.append("\nДосягнуто максимального рівня.");
        }

        SaveManager.saveGame(this);
        return message.toString();
    }

    public void addXp(int amount) {
        addExperience(amount);
    }

    public int getXpToNextLevel() {
        int[] requirements = {100, 250, 450, 700, 1000, 1350, 1750, 2200, 2700};

        if (level >= 10) return 0;

        return requirements[level - 1];
    }

    public int getExperienceToNextLevel() {
        return getXpToNextLevel();
    }

    public String tryBuyItem(Item item) {
        fixAfterLoad();

        if (item == null) return "Предмет не знайдено.";

        if (level < item.getRequiredLevel()) {
            return "🔒 Предмет відкривається з " + item.getRequiredLevel() + " рівня.";
        }

        boolean paid;

        if (item.isGoldItem()) {
            paid = spendGold(item.getPrice());

            if (!paid) return "Недостатньо золота для покупки.";
        } else {
            paid = spendSilver(item.getPrice());

            if (!paid) return "Недостатньо срібла для покупки.";
        }

        inventory.add(item);
        addExperience(10);

        SaveManager.saveGame(this);

        return "✅ " + item.getName() + " додано в інвентар!";
    }

    public boolean buyItem(Item item) {
        return tryBuyItem(item).contains("додано");
    }

    public void placeItem(Item item) {
        fixAfterLoad();

        if (item != null && !placedItems.contains(item)) {
            placedItems.add(item);
            SaveManager.saveGame(this);
        }
    }

    public void removePlacedItem(Item item) {
        fixAfterLoad();
        placedItems.remove(item);
        SaveManager.saveGame(this);
    }

    public int getComfort() {
        fixAfterLoad();

        int comfort = 0;

        for (Item item : placedItems) {
            if (item == null) continue;

            comfort += 5;

            if (item.isGoldItem()) comfort += 5;
            if ("Ліжка".equals(item.getCategory())) comfort += 10;
            if ("Дивани".equals(item.getCategory())) comfort += 8;
            if ("Техніка".equals(item.getCategory())) comfort += 7;
            if ("Декор".equals(item.getCategory())) comfort += 4;
            if ("Рослини".equals(item.getCategory())) comfort += 4;
        }

        return comfort;
    }

    public String buyRoom(String roomName) {
        int requiredLevel;
        int price;

        switch (roomName) {
            case "Друга кімната":
                requiredLevel = 3;
                price = 700;
                if (secondRoomBought) return "Ця кімната вже куплена.";
                break;

            case "Санвузол":
                requiredLevel = 3;
                price = 450;
                if (bathroomBought) return "Санвузол вже куплений.";
                break;

            case "Спальня":
                requiredLevel = 5;
                price = 1200;
                if (bedroomBought) return "Спальня вже куплена.";
                break;

            case "Кухня":
                requiredLevel = 7;
                price = 1800;
                if (kitchenBought) return "Кухня вже куплена.";
                break;

            default:
                requiredLevel = 9;
                price = 2500;
                if (bigRoomBought) return "Велика кімната вже куплена.";
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
        SaveManager.saveGame(this);

        return roomName + " куплено! Кімната додана до квартири.";
    }

    public boolean canTakeDailyReward() {
        long now = System.currentTimeMillis();
        long oneDay = 24L * 60L * 60L * 1000L;

        return now - lastDailyRewardTime >= oneDay;
    }

    public String takeDailyReward() {
        if (!canTakeDailyReward()) {
            return "Щоденну нагороду вже отримано.";
        }

        addSilver(300);
        addGold(1);
        addEnergy(50);
        addExperience(20);

        lastDailyRewardTime = System.currentTimeMillis();
        SaveManager.saveGame(this);

        return "🎁 Щоденну нагороду отримано!\n+300 срібла\n+1 золото\n+50 енергії\n+20 XP";
    }

    public void completeTask(String taskId) {
        fixAfterLoad();
        completedTasks.add(taskId);
        SaveManager.saveGame(this);
    }

    public String completeTask(Task task) {
        return finishActiveTask(task);
    }

    public String getLevelUnlockText() {
        switch (level) {
            case 1: return "До 2 рівня: вибір власної квартири";
            case 2: return "До 3 рівня: робота офіціантом і базовий ремонт";
            case 3: return "До 4 рівня: парк і пляж";
            case 4: return "До 5 рівня: кінотеатр";
            case 5: return "До 6 рівня: салон краси";
            case 6: return "До 7 рівня: нові меблі та спальня";
            case 7: return "До 8 рівня: модні завдання та декор";
            case 8: return "До 9 рівня: кухня та велика квартира";
            case 9: return "До 10 рівня: преміальні предмети";
            default: return "Максимальний рівень першої версії гри";
        }
    }

    public void addSilver(int amount) {
        silver = Math.max(0, silver + Math.max(0, amount));
        SaveManager.saveGame(this);
    }

    public void addGold(int amount) {
        gold = Math.max(0, gold + Math.max(0, amount));
        SaveManager.saveGame(this);
    }

    public boolean spendSilver(int amount) {
        if (amount <= 0) return true;

        if (silver >= amount) {
            silver -= amount;
            SaveManager.saveGame(this);
            return true;
        }

        return false;
    }

    public boolean spendGold(int amount) {
        if (amount <= 0) return true;

        if (gold >= amount) {
            gold -= amount;
            SaveManager.saveGame(this);
            return true;
        }

        return false;
    }

    public void spendEnergy(int amount) {
        energy = Math.max(0, energy - Math.max(0, amount));
        SaveManager.saveGame(this);
    }

    public boolean useEnergy(int amount) {
        if (amount <= 0) return true;

        if (energy >= amount) {
            energy -= amount;
            SaveManager.saveGame(this);
            return true;
        }

        return false;
    }

    public void restoreEnergy() {
        energy = 100;
        SaveManager.saveGame(this);
    }

    public void addEnergy(int amount) {
        energy = Math.min(100, energy + Math.max(0, amount));
        SaveManager.saveGame(this);
    }

    public void selectApartment(String apartmentName) {
        setApartmentType(apartmentName);
        addExperience(100);
        SaveManager.saveGame(this);
    }

    public String getApartmentType() {
        fixAfterLoad();

        if (selectedApartment == null || selectedApartment.isEmpty()) return "blue";

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

    public void setApartmentType(String apartmentType) {
        if (apartmentType == null || apartmentType.isEmpty()) {
            selectedApartment = "blue";
        } else if (apartmentType.equalsIgnoreCase("blue")
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

        SaveManager.saveGame(this);
    }

    public int getSilver() { return silver; }
    public int getGold() { return gold; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getXp() { return experience; }
    public int getEnergy() { return energy; }

    public String getSelectedApartment() {
        fixAfterLoad();
        return selectedApartment;
    }

    public boolean hasSelectedApartment() {
        fixAfterLoad();
        return selectedApartment != null && !selectedApartment.isEmpty();
    }

    public String getWallpaper() {
        fixAfterLoad();
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
        SaveManager.saveGame(this);
    }

    public String getFloor() {
        fixAfterLoad();
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
        SaveManager.saveGame(this);
    }

    public ArrayList<Item> getInventory() {
        fixAfterLoad();
        return inventory;
    }

    public ArrayList<Item> getPlacedItems() {
        fixAfterLoad();
        return placedItems;
    }

    public HashSet<String> getCompletedTasks() {
        fixAfterLoad();
        return completedTasks;
    }

    public boolean isSecondRoomBought() { return secondRoomBought; }
    public boolean isBathroomBought() { return bathroomBought; }
    public boolean isBedroomBought() { return bedroomBought; }
    public boolean isKitchenBought() { return kitchenBought; }
    public boolean isBigRoomBought() { return bigRoomBought; }

    public long getCafeCooldownEndTime() { return cafeCooldownEndTime; }

    public void setCafeCooldownEndTime(long cafeCooldownEndTime) {
        this.cafeCooldownEndTime = cafeCooldownEndTime;
        SaveManager.saveGame(this);
    }

    public long getShellCooldownEndTime() { return shellCooldownEndTime; }

    public void setShellCooldownEndTime(long shellCooldownEndTime) {
        this.shellCooldownEndTime = shellCooldownEndTime;
        SaveManager.saveGame(this);
    }

    public long getCinemaWorkCooldownEndTime() { return cinemaWorkCooldownEndTime; }

    public void setCinemaWorkCooldownEndTime(long cinemaWorkCooldownEndTime) {
        this.cinemaWorkCooldownEndTime = cinemaWorkCooldownEndTime;
        SaveManager.saveGame(this);
    }

    public boolean isJanitorWorkDone() { return janitorWorkDone; }

    public void setJanitorWorkDone() {
        janitorWorkDone = true;
        SaveManager.saveGame(this);
    }

    public boolean isNpcQuestDone() { return npcQuestDone; }

    public void setNpcQuestDone() {
        npcQuestDone = true;
        SaveManager.saveGame(this);
    }

    public boolean isCottonCandyDone() { return cottonCandyDone; }

    public void setCottonCandyDone() {
        cottonCandyDone = true;
        SaveManager.saveGame(this);
    }

    public void setSilver(int silver) {
        this.silver = Math.max(0, silver);
        SaveManager.saveGame(this);
    }

    public void setGold(int gold) {
        this.gold = Math.max(0, gold);
        SaveManager.saveGame(this);
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(100, energy));
        SaveManager.saveGame(this);
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
        SaveManager.saveGame(this);
    }
}