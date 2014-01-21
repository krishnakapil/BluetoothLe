package com.bluetooth.le;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PointF;
import android.os.Bundle;

import com.bluetooth.le.model.BeaconModel;
import com.bluetooth.le.model.Store;

public class MainActivity extends Activity implements MainFragment.MainFragmentInterface {

    private Store mCurrentStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadHomeFragment();
        mCurrentStore = new Store();
    }

    private void loadHomeFragment() {
        Fragment newFragment = new MainFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container,newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

        BeaconModel[] data = mCurrentStore.getBeacons();

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

        userPosition = new PointF(x, y);

        return userPosition;
    }


    @Override
    public void OnSearchSubmitted(String query) {
        Fragment newFragment = SearchResultFragment.newInstance(query);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
