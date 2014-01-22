package com.bluetooth.le;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.bluetooth.le.model.Store;
import com.bluetooth.le.model.StoreItem;
import com.bluetooth.le.samspathfinder.MapSurfaceView;

import java.util.ArrayList;

/**
 * Created by stadiko on 1/21/14.
 */
public class MapActivity extends Activity {

    public static String EXTRA_STORE_ITEM = "store_item";
    public static String EXTRA_STORE_ITEMS = "store_items";

    private MapSurfaceView mStoreMap;
    private Store mCurrentStore;
    private StoreItem mCurrentItem;
    private boolean showList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(EXTRA_STORE_ITEM) != null) {
                mCurrentItem = DummyData.items.get(getIntent().getExtras().getString(EXTRA_STORE_ITEM));
            } else if (getIntent().getExtras().getBoolean(EXTRA_STORE_ITEMS)) {
                showList = true;
            }
        }
        mCurrentStore = new Store();
        drawMap();
    }

    private void drawMap() {
        mStoreMap = new MapSurfaceView(MapActivity.this, mCurrentStore);
        if (mCurrentItem != null) {
            mStoreMap.drawPath(mCurrentItem.getCategory().getPosition().x, mCurrentItem.getCategory().getPosition().y);
        } else if (showList) {
            ArrayList<Point> positions = new ArrayList<Point>();
            for(String key: DummyData.userItems.keySet()) {
                StoreItem si = DummyData.userItems.get(key);
                positions.add(si.getCategory().getPosition());
            }
            mStoreMap.drawPaths(positions);
        }
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
}
