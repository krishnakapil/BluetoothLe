package com.bluetooth.le;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluetooth.le.samspathfinder.MapSurfaceView;
import com.bluetooth.le.samspathfinder.StoreMap;

public class MainActivity extends Activity {

    private BeaconModel[] data;
    private TextView mResult;

    private MapSurfaceView mStoreMap;

    /**
     * Data from the server
     * Object Type refer to StoreMap (AISLE = 1 ,WALKABLE = 2)
     * 0;1;1;2;8 - OBJECT Type ; X coordinate ; Y coordinate ; Width ; Height
     */
    private String storeMapData = "1;1;1;2;10&1;4;1;2;10&1;7;1;2;10";

    /**
     * Data from the server
     * Number of horizontal tiles the map is divided into
     */
    private int storeMapWidth = 10;

    /**
     * Data from the server
     * Number of Vertical tiles the map is divided into
     */
    private int storeMapHeight = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResult = (TextView) findViewById(R.id.result_text);

        data = new BeaconModel[3];
        data[0] = new BeaconModel(1f, 1f, 3.605551f);
        data[1] = new BeaconModel(4f, 6f, 2.236067f);
        data[2] = new BeaconModel(5f, 4f, 1.414213f);

        getUserPosition();

        drawMap();
    }

    private void drawMap() {
        mStoreMap = new MapSurfaceView(MainActivity.this, new StoreMap(storeMapWidth, storeMapHeight, storeMapData));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ((RelativeLayout) findViewById(R.id.container)).addView(mStoreMap, params);
    }


    //Method to find user coordinates based on 3 nearest points

    /**
     * Trilateration Estimation
     * Check http://www.intechopen.com/download/get/type/pdfs/id/13525
     * Calculate user position based on Beacon Coordinates (x1,y1) (x2,y2) (x3,y3) and distance from the user d1, d2 ,d3
     * <p/>
     * Formula :
     * User x = (AY32 + BY13 + CY21) / 2(x1Y32 + x2Y13 + x3Y21)
     * User y = (AX32 + BX13 + CX21) / 2(y1X32 + y2X13 + y3X21)
     * <p/>
     * A = x1 * x1 + y1 * y1 - d1 * d1
     * B = x2 * x2 + y2 * y2 - d2 * d2
     * C = x3 * x3 + y3 * y3 - d3 * d3
     * <p/>
     * X32 = x3 - x2
     * X13 = x1 - x3
     * X21 = X2 - x1
     * Y32 = y3 - y2
     * Y13 = y1 - y3
     * Y21 = y2 - y1
     *
     * @return PointF
     */
    public PointF getUserPosition() {
        //TODO : ADD Null checks
        PointF userPosition;

        float x1 = data[0].getPoint().x;
        float x2 = data[1].getPoint().x;
        float x3 = data[2].getPoint().x;

        float y1 = data[0].getPoint().y;
        float y2 = data[1].getPoint().y;
        float y3 = data[2].getPoint().y;

        float d1 = data[0].getDistanceFromUser();
        float d2 = data[1].getDistanceFromUser();
        float d3 = data[2].getDistanceFromUser();

        float A = (x1 * x1) + (y1 * y1) - (d1 * d1);
        float B = (x2 * x2) + (y2 * y2) - (d2 * d2);
        float C = (x3 * x3) + (y3 * y3) - (d3 * d3);

        float X32 = x3 - x2;
        float X13 = x1 - x3;
        float X21 = x2 - x1;

        float Y32 = y3 - y2;
        float Y13 = y1 - y3;
        float Y21 = y2 - y1;

        float x = ((A * Y32) + (B * Y13) + (C * Y21)) / (2 * ((x1 * Y32) + (x2 * Y13) + (x3 * Y21)));
        float y = ((A * X32) + (B * X13) + (C * X21)) / (2 * ((y1 * X32) + (y2 * X13) + (y3 * X21)));

        StringBuilder log = new StringBuilder();
        log.append("Beacon 1 : ");
        log.append(data[0].toString());
        log.append("\n");

        log.append("Beacon 2 : ");
        log.append(data[1].toString());
        log.append("\n");

        log.append("Beacon 3 : ");
        log.append(data[2].toString());
        log.append("\n");

        log.append("User : ");
        log.append("(" + x + " , " + y + ")");

        mResult.setText(log.toString());

        userPosition = new PointF(x, y);

        return userPosition;
    }


    public static class BeaconModel {
        private PointF mPoint;
        private float mDistanceFromUser;

        public BeaconModel(float x, float y) {
            mPoint = new PointF(x, y);
        }

        public BeaconModel(float x, float y, float distance) {
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
    }


}
