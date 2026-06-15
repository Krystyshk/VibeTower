package vibetower.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    private static final long serialVersionUID = 2L;

    private String id;
    private String name;
    private String type;
    private String backgroundImage;

    private ArrayList<Item> placedItems;
    private ArrayList<PlacedRoomItem> placedRoomItems;

    public Room(String id, String name, String type, String backgroundImage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.backgroundImage = backgroundImage;
        this.placedItems = new ArrayList<>();
        this.placedRoomItems = new ArrayList<>();
    }

    public void fixAfterLoad() {
        if (placedItems == null) {
            placedItems = new ArrayList<>();
        }

        if (placedRoomItems == null) {
            placedRoomItems = new ArrayList<>();
        }

        if (id == null || id.isEmpty()) {
            id = "room";
        }

        if (name == null || name.isEmpty()) {
            name = "Кімната";
        }

        if (type == null || type.isEmpty()) {
            type = "blue";
        }

        if (backgroundImage == null || backgroundImage.isEmpty()) {
            backgroundImage = "src/main/resources/apartment_blue.jpg";
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public ArrayList<Item> getPlacedItems() {
        fixAfterLoad();
        return placedItems;
    }

    public ArrayList<PlacedRoomItem> getPlacedRoomItems() {
        fixAfterLoad();
        return placedRoomItems;
    }

    public void addPlacedRoomItem(PlacedRoomItem placedRoomItem) {
        fixAfterLoad();

        if (placedRoomItem == null) {
            return;
        }

        placedRoomItems.add(placedRoomItem);

        if (placedRoomItem.getItem() != null) {
            placedItems.add(placedRoomItem.getItem());
        }
    }

    public void removePlacedRoomItem(PlacedRoomItem placedRoomItem) {
        fixAfterLoad();

        if (placedRoomItem == null) {
            return;
        }

        placedRoomItems.remove(placedRoomItem);

        if (placedRoomItem.getItem() != null) {
            placedItems.remove(placedRoomItem.getItem());
        }
    }

    public void clearRoomItems() {
        fixAfterLoad();
        placedItems.clear();
        placedRoomItems.clear();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}