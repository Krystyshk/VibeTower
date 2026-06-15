package vibetower.model;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 4L;

    private String name;
    private String category;
    private int price;
    private String currency;
    private int minLevel;
    private String icon;
    private String imageFile;

    public Item(String name, String category, int price, String currency, int minLevel) {
        this(name, category, price, currency, minLevel, "👕", "");
    }

    public Item(String name, String category, int price, String currency, int minLevel, String icon) {
        this(name, category, price, currency, minLevel, icon, "");
    }

    public Item(String name, String category, int price, String currency, int minLevel, String icon, String imageFile) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.minLevel = minLevel;
        this.icon = icon;
        this.imageFile = imageFile;

        fixAfterLoad();
    }

    public void fixAfterLoad() {
        if (name == null || name.trim().isEmpty()) {
            name = "Предмет";
        }

        if (category == null || category.trim().isEmpty()) {
            category = "Декор";
        }

        if (currency == null || currency.trim().isEmpty()) {
            currency = "silver";
        }

        if (icon == null || icon.trim().isEmpty()) {
            icon = "📦";
        }

        if (imageFile == null) {
            imageFile = "";
        }

        if (price < 0) {
            price = 0;
        }

        if (minLevel < 1) {
            minLevel = 1;
        }
    }

    public String getName() {
        fixAfterLoad();
        return name;
    }

    public String getCategory() {
        fixAfterLoad();
        return category;
    }

    public int getPrice() {
        fixAfterLoad();
        return price;
    }

    public String getCurrency() {
        fixAfterLoad();
        return currency;
    }

    public int getMinLevel() {
        fixAfterLoad();
        return minLevel;
    }

    public int getRequiredLevel() {
        fixAfterLoad();
        return minLevel;
    }

    public boolean isGoldItem() {
        fixAfterLoad();
        return "gold".equalsIgnoreCase(currency);
    }

    public boolean isSilverItem() {
        fixAfterLoad();
        return "silver".equalsIgnoreCase(currency);
    }

    public String getIcon() {
        fixAfterLoad();
        return icon;
    }

    public String getImageFile() {
        fixAfterLoad();
        return imageFile;
    }

    public String getImagePath() {
        return getImageFile();
    }

    public String getFullName() {
        fixAfterLoad();
        return icon + " " + name + " (" + category + ")";
    }

    public String getPriceText() {
        fixAfterLoad();

        if (price <= 0) {
            return "Безкоштовно";
        }

        if ("gold".equalsIgnoreCase(currency)) {
            return price + " золота";
        }

        return price + " срібла";
    }

    public String getLevelText() {
        fixAfterLoad();

        if (minLevel <= 1) {
            return "Доступно з 1 рівня";
        }

        return "Доступно з " + minLevel + " рівня";
    }

    public String getShortInfo() {
        fixAfterLoad();
        return name + " — " + getPriceText();
    }

    public void setName(String name) {
        this.name = name;
        fixAfterLoad();
    }

    public void setCategory(String category) {
        this.category = category;
        fixAfterLoad();
    }

    public void setPrice(int price) {
        this.price = Math.max(0, price);
    }

    public void setCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            this.currency = "silver";
        } else {
            this.currency = currency;
        }

        fixAfterLoad();
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = Math.max(1, minLevel);
    }

    public void setRequiredLevel(int requiredLevel) {
        this.minLevel = Math.max(1, requiredLevel);
    }

    public void setIcon(String icon) {
        this.icon = icon;
        fixAfterLoad();
    }

    public void setImageFile(String imageFile) {
        if (imageFile == null) {
            this.imageFile = "";
        } else {
            this.imageFile = imageFile;
        }
    }

    public void setImagePath(String imagePath) {
        setImageFile(imagePath);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}