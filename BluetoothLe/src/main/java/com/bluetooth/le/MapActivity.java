package com.bluetooth.le;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.bluetooth.le.model.Store;
import com.bluetooth.le.samspathfinder.MapSurfaceView;

/**
 * Created by stadiko on 1/21/14.
 */
public class MapActivity extends Activity{

    private MapSurfaceView mStoreMap;
    private Store mCurrentStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mCurrentStore = new Store();
        drawMap();
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
}
