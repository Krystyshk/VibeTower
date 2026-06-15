package vibetower.model;

import java.io.Serializable;

public class PlacedRoomItem implements Serializable {

    private static final long serialVersionUID = 3L;

    private Item item;

    private String itemName;
    private String itemIcon;
    private String itemCategory;
    private String imageFile;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean mirrored;

    private boolean door;
    private String targetRoomId;

    public PlacedRoomItem(Item item, int x, int y, int width, int height) {
        this.item = item;

        if (item != null) {
            this.itemName = item.getName();
            this.itemIcon = item.getIcon();
            this.itemCategory = item.getCategory();
            this.imageFile = item.getImageFile();
        } else {
            this.itemName = "Предмет";
            this.itemIcon = "?";
            this.itemCategory = "Декор";
            this.imageFile = "";
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.mirrored = false;

        this.door = false;
        this.targetRoomId = "";
    }

    public void fixAfterLoad() {
        if (itemName == null || itemName.trim().isEmpty()) {
            if (item != null && item.getName() != null) {
                itemName = item.getName();
            } else {
                itemName = "Предмет";
            }
        }

        if (itemIcon == null || itemIcon.trim().isEmpty()) {
            if (item != null && item.getIcon() != null) {
                itemIcon = item.getIcon();
            } else {
                itemIcon = "?";
            }
        }

        if (itemCategory == null || itemCategory.trim().isEmpty()) {
            if (item != null && item.getCategory() != null) {
                itemCategory = item.getCategory();
            } else {
                itemCategory = "Декор";
            }
        }

        if (imageFile == null || imageFile.trim().isEmpty()) {
            if (item != null && item.getImageFile() != null) {
                imageFile = item.getImageFile();
            } else {
                imageFile = "";
            }
        }

        if (targetRoomId == null) {
            targetRoomId = "";
        }

        if (width <= 0) {
            width = 150;
        }

        if (height <= 0) {
            height = 100;
        }
    }

    public void refreshItemInfo() {
        if (item == null) return;

        itemName = item.getName();
        itemIcon = item.getIcon();
        itemCategory = item.getCategory();
        imageFile = item.getImageFile();
    }

    public Item getItem() {
        fixAfterLoad();
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        refreshItemInfo();
    }

    public String getItemName() {
        fixAfterLoad();
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemIcon() {
        fixAfterLoad();
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getItemCategory() {
        fixAfterLoad();
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getImageFile() {
        fixAfterLoad();
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public int getX() {
        fixAfterLoad();
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        fixAfterLoad();
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        fixAfterLoad();
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        fixAfterLoad();
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMirrored() {
        fixAfterLoad();
        return mirrored;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public boolean isDoor() {
        fixAfterLoad();
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public String getTargetRoomId() {
        fixAfterLoad();
        return targetRoomId;
    }

    public void setTargetRoomId(String targetRoomId) {
        this.targetRoomId = targetRoomId;
    }
}