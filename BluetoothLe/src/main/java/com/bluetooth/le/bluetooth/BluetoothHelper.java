package com.bluetooth.le.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BluetoothHelper {

    private static final int REQUEST_ENABLE_BT = 10;
    private static final String TAG = BluetoothHelper.class.getSimpleName();
    private final Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BleListener mBleListener;

    public BluetoothHelper(Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    public void init() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            if(mBleListener != null) {
                mBleListener.onBluetoothNotEnabled();
            }
        } else {
            Log.i(TAG, "Starting scanning..");
            scanLeDevice(true);
        }
    }

    private static final long SCAN_PERIOD = 5000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.i(TAG, "startLeScan");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i(TAG, "stopLeScan");
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Device: " + device.getAddress());
                    Log.i(TAG, "RSSI: " + rssi);
                    if(mBleListener != null) {
                        mBleListener.onDeviceFound(device, rssi, scanRecord);
                    }
                }
            });
        }
    };

    public void setBleListener(BleListener listener) {
        mBleListener = listener;
    }

    public interface BleListener {
        public void onDeviceFound(BluetoothDevice device, final int rssi, byte[] scanRecord);

        public void onBluetoothNotEnabled();
    }
}
