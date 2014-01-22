package com.bluetooth.le.bluetooth;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BeaconTracker {

    private static final int REQUEST_ENABLE_BT = 100;
    private BeaconHolder mBeaconList;
    private EstimoteHelper estimoteTracker;
    private Handler mHandler;
    private Context mContext;
    private Listener mListener;

    public BeaconTracker(Context context) {
        mContext = context;
        mBeaconPositionMap = new HashMap<String, PointF>();
        mHandler = new Handler();
        setPositionForBeacon("C0:77:77:16:CC:E7", new PointF(0.0f, 0.0f));
        setPositionForBeacon("C3:DF:51:99:ED:E6", new PointF(14.0f, 19.0f));
    }

    private HashMap<String, PointF> mBeaconPositionMap;

    public void setPositionForBeacon(String beaconId, PointF point) {
        mBeaconPositionMap.put(beaconId, point);
    }

    private boolean mIsRefreshing;

    private static final int POLL_DURATION = 6000;

    public void refreshPosition() {
        if(mIsRefreshing) {
            return;
        }

        start();
        mIsRefreshing = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, POLL_DURATION);
    }

    private void start() {
        com.estimote.sdk.utils.L.enableDebugLogging(true);

        mBeaconList = new BeaconHolder(mContext);

        estimoteTracker = new EstimoteHelper(mContext);
        estimoteTracker.setListener(new EstimoteHelper.Listener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    mBeaconList.addBeaconData(beacon);
                }
            }
        });
        estimoteTracker.init();
        estimoteTracker.start();
    }

    public void stop() {
        if(mIsRefreshing) {
            estimoteTracker.stop();
            estimoteTracker.destroy();
            mIsRefreshing = false;

            if(mListener != null) {
                mListener.onFinishedRefreshing();
            }
        }
    }

    private class BeaconHolder {
        private final String TAG = BeaconHolder.class.getSimpleName();
        private final Context mContext;
        private HashMap<String, BeaconData> mBeaconMap;
        private ArrayList<BeaconData> mBeaconList;

        private int mCurrentSort;
        public static final int SORT_NAME = 0;

        private BeaconData mCurrentNearbyBeacon;

        public BeaconHolder(Context context) {
            mContext = context;
            mBeaconMap = new HashMap<String, BeaconData>();
            mBeaconList = new ArrayList<BeaconData>();
        }

        public void addBeaconData(Beacon beacon) {
            String address = beacon.getMacAddress();
            BeaconData beaconData = mBeaconMap.get(address);
            if(beaconData != null) {
                Log.i(TAG, "Updating beacon: " + address + ", " + beacon.getRssi());
                beaconData.setRSSI(beacon.getRssi());
            } else {
                Log.i(TAG, "Adding beacon: " + address + ", " + beacon.getRssi());
                beaconData = new BeaconData(beacon);
                mBeaconMap.put(address, beaconData);
                mBeaconList.add(beaconData);
                sort();
            }

            checkIfNear(beaconData);
        }

        private void checkIfNear(BeaconData beaconData) {
            if(beaconData.isClose() && beaconData != mCurrentNearbyBeacon) {
                mCurrentNearbyBeacon = beaconData;
                String address = beaconData.getAddress();
                Toast.makeText(mContext, "Near: " + address, Toast.LENGTH_LONG).show();
                if(mListener != null) {
                    mListener.atBeacon(address, mBeaconPositionMap.get(address));
                }
                stop();
            }
        }

        private void sort() {
            Collections.sort(mBeaconList, new Comparator<BeaconData>() {
                @Override
                public int compare(BeaconData lhs, BeaconData rhs) {
                    return lhs.mBeaconAddress.compareTo(rhs.mBeaconAddress);
                }
            });
        }

        public BeaconData getBeacon(int index) {
            return mBeaconList.get(index);
        }

        public int getCount() {
            return mBeaconList.size();
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        public void atBeacon(String id, PointF position);

        public void onFinishedRefreshing();
    }

    public static class BeaconData {
        private String mBeaconAddress;
        private int mRSSI;
        private int mAverageRSSI;
        private int mRSSISetCount;

        private LinkedList<Integer> mPreviousRSSIs;
        private static final int PREVIOUS_RSSI_COUNT = 2;

        public BeaconData(Beacon beacon) {
            mBeaconAddress = beacon.getMacAddress();
            mAverageRSSI = mRSSI = beacon.getRssi();
            mPreviousRSSIs = new LinkedList<Integer>();
            mRSSISetCount++;
        }

        public void setRSSI(int rssi) {
            mRSSI = rssi;
            int totalRSSI = mAverageRSSI * mRSSISetCount + rssi;
            mRSSISetCount++;
            mAverageRSSI = totalRSSI / mRSSISetCount;

            mPreviousRSSIs.add(rssi);
            while(mPreviousRSSIs.size() > PREVIOUS_RSSI_COUNT) {
                mPreviousRSSIs.removeFirst();
            }
        }

        public boolean isClose() {
            boolean isClose = false;

            if(mPreviousRSSIs.size() == PREVIOUS_RSSI_COUNT) {
                int total = 0;
                for (Integer mPreviousRSSI : mPreviousRSSIs) {
                    total += mPreviousRSSI;
                }
                if(total / PREVIOUS_RSSI_COUNT > -72) {
                    return true;
                }
            }

            return isClose;
        }

        public int getRSSI() {
            return mRSSI;
        }

        public int getAverageRSSI() {
            return mAverageRSSI;
        }

        public void resetAverageRSSI() {
            mAverageRSSI = 0;
            mRSSISetCount = 0;
        }

        public String getAddress() {
            return mBeaconAddress;
        }
    }

    public int getCount() {
        if(mBeaconList != null) {
            return mBeaconList.getCount();
        }

        return 0;
    }

    public BeaconData getBeacon(int index) {
        if(mBeaconList != null) {
            return mBeaconList.getBeacon(index);
        }

        return null;
    }
}
