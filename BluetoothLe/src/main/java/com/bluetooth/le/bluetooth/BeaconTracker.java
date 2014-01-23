package com.bluetooth.le.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bluetooth.le.DummyData;
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
        mHandler = new Handler();
    }

    private boolean mIsRefreshing;

    private static final int POLL_DURATION = 10000;

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    public void refreshPosition() {
        if (mIsRefreshing) {
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
        setBluetooth(true);
        com.estimote.sdk.utils.L.enableDebugLogging(true);

        Handler bluetoothDelayHandler = new Handler();
        bluetoothDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 3000);
    }

    public void stop() {
        if (mIsRefreshing) {
            estimoteTracker.stop();
            estimoteTracker.destroy();
            mIsRefreshing = false;

            mBeaconList.getNearestBeaconPoint();
        }
    }

    private class BeaconHolder {
        private final String TAG = BeaconHolder.class.getSimpleName();
        private final Context mContext;
        private HashMap<String, BeaconData> mBeaconMap;
        private ArrayList<BeaconData> mBeaconList;


        public BeaconHolder(Context context) {
            mContext = context;
            mBeaconMap = new HashMap<String, BeaconData>();
            mBeaconList = new ArrayList<BeaconData>();
        }

        public void addBeaconData(Beacon beacon) {
            String address = beacon.getMacAddress();
            BeaconData beaconData = mBeaconMap.get(address);
            if (beaconData != null) {
                Log.i(TAG, "Updating beacon: " + address + ", " + beacon.getRssi());
                beaconData.setRSSI(beacon.getRssi());
            } else {
                Log.i(TAG, "Adding beacon: " + address + ", " + beacon.getRssi());
                beaconData = new BeaconData(beacon);
                mBeaconMap.put(address, beaconData);
                mBeaconList.add(beaconData);
                sort();
            }
        }

        public void getNearestBeaconPoint() {
            int max = BeaconData.RSSI_RANGE;
            PointF point = null;
            String address= null;
            if (mBeaconList != null) {
                for (BeaconData beaconData : mBeaconList) {
                    if (beaconData.getRSSI() >= max) {
                        max = beaconData.getRSSI();
                        address = beaconData.getAddress();
                        point = DummyData.beacons.get(address).getPoint();
                    }
                }
            }

            if (mListener != null) {
                if(point != null) {
                    mListener.atBeacon(address,point);
                } else {
                    mListener.onFinishedRefreshing();
                }
            }
        }

        private void checkIfNear(BeaconData beaconData) {
            if (beaconData.isClose()) {
                String address = beaconData.getAddress();
                Toast.makeText(mContext, "Near: " + address, Toast.LENGTH_LONG).show();
                if (mListener != null) {
                    mListener.atBeacon(address, DummyData.beacons.get(address).getPoint());
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
        public static final int RSSI_RANGE = -72;
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
            while (mPreviousRSSIs.size() > PREVIOUS_RSSI_COUNT) {
                mPreviousRSSIs.removeFirst();
            }
        }

        public boolean isClose() {
            boolean isClose = false;

            if (mPreviousRSSIs.size() == PREVIOUS_RSSI_COUNT) {
                int total = 0;
                for (Integer mPreviousRSSI : mPreviousRSSIs) {
                    total += mPreviousRSSI;
                }
                if (total / PREVIOUS_RSSI_COUNT > RSSI_RANGE) {
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
        if (mBeaconList != null) {
            return mBeaconList.getCount();
        }

        return 0;
    }

    public BeaconData getBeacon(int index) {
        if (mBeaconList != null) {
            return mBeaconList.getBeacon(index);
        }

        return null;
    }
}
