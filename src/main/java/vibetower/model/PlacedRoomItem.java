package vibetower.model;

import java.io.Serializable;

public class PlacedRoomItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Item item;

    private int x;
    private int y;
    private int width;
    private int height;

    private boolean mirrored;

    private boolean door;
    private String targetRoomId;

    public PlacedRoomItem(Item item, int x, int y, int width, int height) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mirrored = false;
        this.door = false;
        this.targetRoomId = "";
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public String getTargetRoomId() {
        return targetRoomId;
    }

    public void setTargetRoomId(String targetRoomId) {
        this.targetRoomId = targetRoomId;
    }
}