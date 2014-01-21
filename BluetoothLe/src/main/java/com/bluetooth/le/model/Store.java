package com.bluetooth.le.model;

import android.graphics.PointF;

import com.bluetooth.le.DummyData;
import com.bluetooth.le.samspathfinder.StoreMap;

/**
 * Created by stadiko on 1/20/14.
 */
public class Store {

    private String mStoreId;

    private BeaconModel[] mBeacons;
    private Category[] mCategories;
    /**
     * Data from the server
     * Object Type refer to StoreMap (AISLE = 1 ,ENTRANCE = 2)
     * 0;1;1;2;8 - OBJECT Type ; X coordinate ; Y coordinate ; Width ; Height
     */
    private String mMapData;

    /**
     * Data from the server
     * Number of horizontal tiles the map is divided into
     */
    private int mWidth;

    /**
     * Data from the server
     * Number of Vertical tiles the map is divided into
     */
    private int mHeight;

    /**
     * Store map object
     */
    private StoreMap map;

    /**
     * The user tile position on the map
     */
    private PointF mUserPosition;

    public Store() {
        this(DummyData.storeId,DummyData.mapData,DummyData.storeWidth,DummyData.storeHeight,DummyData.beacons,DummyData.categories);
        mUserPosition = DummyData.userPosition;
    }

    /**
     * @param storeId    Store Id of the store
     * @param mapData    Defines the map data of the store
     * @param mapWidth   Width in number of tiles
     * @param mapHeight  Height in number of tiles
     * @param beacons    Beacons in the store
     * @param categories Categories in the store
     */
    public Store(String storeId, String mapData, int mapWidth, int mapHeight, BeaconModel[] beacons, Category[] categories) {
        mStoreId = storeId;
        mMapData = mapData;
        mWidth = mapWidth;
        mHeight = mapHeight;
        mBeacons = beacons;
        mCategories = categories;
        map = new StoreMap(mWidth, mHeight, mMapData);
    }

    public PointF getUserPosition() {
        if (mUserPosition == null) {
            mUserPosition = new PointF(mWidth - 1, mHeight - 1);
        }
        return mUserPosition;
    }

    public void setUserPosition(PointF userPosition) {
        mUserPosition = userPosition;
    }

    public void setUserPosition(float x, float y) {
        if (mUserPosition == null) {
            mUserPosition = new PointF(x, y);
        } else {
            mUserPosition.set(x, y);
        }
    }

    public Category[] getCategories() {
        return mCategories;
    }

    public Category getCategory(int categoryId){
        return mCategories[categoryId-1];
    }

    public String getStoreId() {
        return mStoreId;
    }

    public StoreMap getMap() {
        return map;
    }

    public BeaconModel[] getBeacons() {
        return mBeacons;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
