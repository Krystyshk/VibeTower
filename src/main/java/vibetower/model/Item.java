package vibetower.model;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private int price;
    private String currency;
    private int minLevel;

    public Item(String name, int price) {
        this(name, "Інтер'єр", price, "silver", 1);
    }

    public Item(String name, String category, int price, String currency, int minLevel) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.currency = currency;
        this.minLevel = minLevel;
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

    public String getPriceText() {
        if (currency.equals("gold")) {
            return price + " золота";
        }

        return price + " срібла";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item other)) {
            return false;
        }

        return Objects.equals(name, other.name)
                && Objects.equals(category, other.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }
}