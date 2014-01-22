package com.bluetooth.le.bluetooth;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class EstimoteHelper {

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private static final String TAG = EstimoteHelper.class.getSimpleName();

    private Context mContext;

    private BeaconManager beaconManager;
    private Listener mListener;

    public EstimoteHelper(Context context) {
        mContext = context;
    }

    public interface Listener {
        public void onBeaconsDiscovered(Region region, List<Beacon> beacons);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void init() {
        // Should be invoked in #onCreate.
        beaconManager = new BeaconManager(mContext);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, List < Beacon > beacons) {
                Log.d(TAG, "Ranged beacons: " + beacons);
                if(mListener != null) {
                    mListener.onBeaconsDiscovered(region, beacons);
                }
            }
        });
    }

    public void start() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    Log.e(TAG, "onServiceReady");
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void stop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }
    }

    public void destroy() {
        beaconManager.disconnect();
    }
}
