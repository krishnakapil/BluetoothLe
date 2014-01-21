package com.bluetooth.le.model;

import android.graphics.PointF;

/**
 * Created by stadiko on 1/20/14.
 */
public class BeaconModel {

    //UUID
    public static final String AISLE_1_TOP = "AISLE-0001-TOP";
    public static final String AISLE_1_BOTTOM = "AISLE-0001-BOTTOM";
    public static final String AISLE_2_TOP = "AISLE-0002-TOP";
    public static final String AISLE_2_BOTTOM = "AISLE-0002-BOTTOM";
    public static final String AISLE_3_TOP = "AISLE-0003-TOP";
    public static final String AISLE_3_BOTTOM = "AISLE-0003-BOTTOM";

    private String mBeaconId;
    private PointF mPoint;
    private float mDistanceFromUser;

    /**
     *
     * @param beaconId : UUID of the beacon
     * @param x : x value of the AISLE the beacon is on
     * @param y : y value of the AISLE the beacon is on
     */
    public BeaconModel(String beaconId, float x, float y) {
        mPoint = new PointF(x, y);
        mBeaconId = beaconId;
    }

    /**
     *
     * @param beaconId : UUID of the beacon
     * @param x : x value of the AISLE the beacon is on
     * @param y : y value of the AISLE the beacon is on
     * @param distance : distance of the beacon from user
     */
    public BeaconModel(String beaconId, float x, float y, float distance) {
        mBeaconId = beaconId;
        mPoint = new PointF(x, y);
        mDistanceFromUser = distance;
    }

    public void BeaconModel(PointF point) {
        mPoint = point;
    }

    @Override
    public String toString() {
        return "(" + mPoint.x + " , " + mPoint.y + ") Distance : " + mDistanceFromUser;
    }

    public PointF getPoint() {
        return mPoint;
    }

    public void setPoint(PointF mPoint) {
        this.mPoint = mPoint;
    }

    public float getDistanceFromUser() {
        return mDistanceFromUser;
    }

    public void setDistanceFromUser(float mDistanceFromUser) {
        this.mDistanceFromUser = mDistanceFromUser;
    }

    public String getBeaconId() {
        return mBeaconId;
    }

    public void setBeaconId(String mBeaconId) {
        this.mBeaconId = mBeaconId;
    }
}
