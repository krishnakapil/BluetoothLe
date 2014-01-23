package com.bluetooth.le;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RelativeLayout;

import com.bluetooth.le.bluetooth.BeaconTracker;
import com.bluetooth.le.model.Store;
import com.bluetooth.le.model.StoreItem;
import com.bluetooth.le.model.User;
import com.bluetooth.le.samspathfinder.MapSurfaceView;

import java.util.ArrayList;

/**
 * Created by stadiko on 1/21/14.
 */
public class MapActivity extends Activity {

    public static String EXTRA_STORE_ITEM = "store_item";
    public static String EXTRA_STORE_ITEMS = "store_items";

    private static final int POLL_DURATION = 10000;

    private MapSurfaceView mStoreMap;
    private Store mCurrentStore;
    private StoreItem mCurrentItem;
    private boolean showList = false;
    private BeaconTracker mBeaconTracker;

    private ProgressDialog pd;

    private Handler mHandler;

    private boolean isRefreshingLocation = false;
    private boolean showMapOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(EXTRA_STORE_ITEM) != null) {
                mCurrentItem = DummyData.items.get(getIntent().getExtras().getString(EXTRA_STORE_ITEM));
            } else if (getIntent().getExtras().getBoolean(EXTRA_STORE_ITEMS)) {
                showList = true;
            }
        } else {
            showMapOnly = true;
        }

        mHandler = new Handler();

        mCurrentStore = new Store();
        drawMap();

        pd = new ProgressDialog(MapActivity.this);
        pd.setMessage("Getting location");
        pd.setCancelable(false);

        mBeaconTracker = new BeaconTracker(this);
        mBeaconTracker.setListener(new BeaconTracker.Listener() {
            @Override
            public void atBeacon(String id, PointF position) {
                if (position != null) {
                    if (!isRefreshingLocation) {
                        User.getInstance().setUserPosition(position);
                        mStoreMap.refreshUserOnMap();
                        loadPaths();
                    } else {
                        mStoreMap.clearMap();
                        mStoreMap.setMoveUserListener(new MapSurfaceView.UserMoveInterface() {
                            @Override
                            public void userMoveFinished() {
                                loadPaths();
                            }
                        });
                        mStoreMap.moveUserTo((int) position.x, (int) position.y, 4f);
                    }

                }

                Log.v("MapActivity","Got Location " + position);

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                BeaconTracker.setBluetooth(false);
                setProgressBarIndeterminateVisibility(false);
                startRefreshTimer();
            }

            @Override
            public void onFinishedRefreshing() {
                loadPaths();

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                BeaconTracker.setBluetooth(false);
                setProgressBarIndeterminateVisibility(false);
                startRefreshTimer();
            }
        });

        mBeaconTracker.refreshPosition();
        pd.show();
    }



    private void startRefreshTimer() {
        if(mHandler != null && !showMapOnly) {
            isRefreshingLocation = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBeaconTracker.refreshPosition();
                    setProgressBarIndeterminateVisibility(true);
                }
            }, POLL_DURATION);
        }

    }

    private void loadPaths() {
        if (showList) {
            ArrayList<Point> positions = new ArrayList<Point>();
            for (String key : DummyData.userItems.keySet()) {
                StoreItem si = DummyData.userItems.get(key);
                positions.add(si.getCategory().getPosition());
            }
            mStoreMap.drawPaths(positions);
        } else if (mCurrentItem != null) {
            mStoreMap.drawPath(mCurrentItem.getCategory().getPosition().x, mCurrentItem.getCategory().getPosition().y);
        }
    }

    private void drawMap() {
        mStoreMap = new MapSurfaceView(MapActivity.this, mCurrentStore);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ((RelativeLayout) findViewById(R.id.container)).addView(mStoreMap, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
        BeaconTracker.setBluetooth(false);
        mBeaconTracker.stop();
    }

    public static final String REFRESH_MENU_ITEM = "Refresh user position";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (REFRESH_MENU_ITEM.equals(item.getTitle())) {
            isRefreshingLocation = false;
            mBeaconTracker.refreshPosition();
            pd.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(REFRESH_MENU_ITEM);
        return true;
    }
}
