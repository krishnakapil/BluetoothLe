package com.bluetooth.le.model;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by stadiko on 1/20/14.
 */
public class Category {
    public static final int ELECTRONICS = 1;
    public static final int KITCHEN = 2;
    public static final int FURNITURE = 3;
    public static final int APPAREL = 4;
    public static final int VIDEO_GAMES = 5;
    public static final int TOYS = 6;
    public static final int GROCERY = 7;
    public static final int MOVIES = 8;
    public static final int AUTO = 9;
    public static final int HEALTH = 10;
    public static final int JEWELRY = 11;
    public static final int OFFICE = 12;

    private int categoryId;
    private String categoryName;
    private BeaconModel beacon;
    private Point position;//walkable position on the map for user(used for navigation)

    /**
     * Position on the map where category text should be drawn
     */
    private PointF textTilePositionOnMap;

    public Category(int categoryId, String categoryName, BeaconModel beacon, Point position, PointF textTilePositionOnMap) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.beacon = beacon;
        this.position = position;
        this.textTilePositionOnMap = textTilePositionOnMap;
    }

    public PointF getTextTilePositionOnMap() {
        return textTilePositionOnMap;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BeaconModel getBeacon() {
        return beacon;
    }

    public Point getPosition(){
        return position;
    }
}


