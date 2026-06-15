package vibetower.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String name;
    private String category;
    private int price;
    private String currency;
    private int minLevel;
    private String icon;
    private String imageFile;

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
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getRequiredLevel() {
        return minLevel;
    }

    public boolean isGoldItem() {
        return "gold".equals(currency);
    }

    public String getIcon() {
        return icon;
    }

    public String getImageFile() {
        return imageFile;
    }

    public String getFullName() {
        return icon + " " + name + " (" + category + ")";
    }
}