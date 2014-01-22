package com.bluetooth.le.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bluetooth.le.R;

public class BluetoothListActivity extends Activity{

    private ListView mListView;
    private BleAdapter mBleAdapter;
    private BeaconTracker mBeaconTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_list);

        mBeaconTracker = new BeaconTracker(this);
        mListView = (ListView) findViewById(R.id.bluetooth_list);
        mBleAdapter = new BleAdapter(this);
        mListView.setAdapter(mBleAdapter);
        mBeaconTracker.refreshPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class BleAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mLayouterInflater;

        public BleAdapter(Context context) {
            mContext = context;
            mLayouterInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mBeaconTracker.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            BeaconTracker.BeaconData beacon = mBeaconTracker.getBeacon(position);

            if (convertView != null) {
                view = convertView;
            } else {
                view = mLayouterInflater.inflate(R.layout.ble_list_item, parent, false);
            }

            ((TextView) view.findViewById(R.id.beacon_rssi)).setText(Integer.toString(beacon.getRSSI()));
            ((TextView) view.findViewById(R.id.beacon_address)).setText(beacon.getAddress());

            return view;
        }
    }
}
