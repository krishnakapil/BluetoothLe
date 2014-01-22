package com.bluetooth.le.model;

import android.graphics.PointF;

import com.bluetooth.le.DummyData;

/**
 * Created by stadiko on 1/21/14.
 */
public class User {
    private PointF userPosition;

    private static User instance;

    private User() {

    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }

        return instance;
    }

    public PointF getUserPosition() {
        if (userPosition == null) {
            userPosition = DummyData.userPosition;
        }
        return userPosition;
    }

    public void setUserPosition(PointF userPosition) {
        this.userPosition = userPosition;
    }

    public void setUserPosition(float x, float y) {
        if (userPosition == null) {
            userPosition = new PointF(x, y);
        } else {
            userPosition.set(x, y);
        }
    }
}
