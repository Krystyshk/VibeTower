package vibetower.model;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private String icon;
    private int price;
    private String currency;
    private int requiredLevel;
    private int x;
    private int y;

    public Item(String name, int price) {
        this(name, "Меблі", "▣", price, "silver", 1);
    }

    public Item(String name, String category, String icon, int price, String currency, int requiredLevel) {
        this.name = name;
        this.category = category;
        this.icon = icon;
        this.price = price;
        this.currency = currency;
        this.requiredLevel = requiredLevel;
        this.x = 120;
        this.y = 120;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public int getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public boolean isGoldItem() {
        return "gold".equalsIgnoreCase(currency);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
