package com.bluetooth.le.samspathfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bluetooth.le.model.BeaconModel;
import com.bluetooth.le.model.Category;
import com.bluetooth.le.model.Store;
import com.bluetooth.le.pathfinding.AStarPathFinder;
import com.bluetooth.le.pathfinding.Path;
import com.bluetooth.le.pathfinding.PathFinder;

/**
 * Created by stadiko on 1/13/14.
 */
public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MapSurfaceView.class.getSimpleName();

    public static final int DRAW_MOVE_NONE = 1;
    public static final int DRAW_MOVE_USER = 2;

    public static int DRAW_TYPE = 0;

    private static float SPEED = 0.1f;

    private int mNewY;
    private int mNewX;

    private SurfaceHolder mHolder;
    private Store mStore;
    private StoreMap mStoreMap;
    private Paint mAislePaint;
    private Paint mDebugPaint;
    private Paint mPathPaint;
    private Paint mUserPaint;
    private Paint mBeaconPaint;
    private Paint mTextPaint;

    /**
     * The path finder we'll use to search our map
     */
    private PathFinder finder;
    /**
     * The last path found for the current unit
     */
    private Path path;
    /**
     * Width of the MapSurfaceView in the parent
     */
    private int mWidth;
    /**
     * Height of the MapSurafceView in the parent
     */
    private int mHeight;
    /**
     * Width of each tile in the map. Calculated based on mWidth
     */
    private float mTileWidth;
    /**
     * Height of each tile in the map. Calculated based on mHeight
     */
    private float mTileHeight;

    public MapSurfaceView(Context context) {
        this(context, null, 0);
    }

    public MapSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapSurfaceView(Context context, Store store) {
        this(context, null, 0, store);
    }

    public MapSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public MapSurfaceView(Context context, AttributeSet attrs, int defStyle, Store store) {
        super(context, attrs, defStyle);
        mStoreMap = store.getMap();
        finder = new AStarPathFinder(mStoreMap, 500, false);
        mStore = store;
        mHolder = getHolder();
        if (mHolder != null) {
            mHolder.addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas c = surfaceHolder.lockCanvas();
        onDraw(c);
        surfaceHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //moveUserTo(9, 1);
        drawPath(mStore.getCategory(Category.APPAREL).getPosition().x,mStore.getCategory(Category.APPAREL).getPosition().y);
        //moveUserTo(4, 1);
        return true;
    }

    public void drawPath(int newX,int newY) {
        path = finder.findPath((int) mStore.getUserPosition().x, (int) mStore.getUserPosition().y, newX, newY);
        Canvas c = getHolder().lockCanvas();
        onDraw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    public void moveUserTo(int newX, int newY) {
        mNewX = newX;
        mNewY = newY;
        DRAW_TYPE = DRAW_MOVE_USER;
        while (mStore.getUserPosition().y >= mNewY) {
            Canvas c = null;
            try {
                c = getHolder().lockCanvas();
                synchronized (getHolder()) {
                    onDraw(c);
                }
            } finally {
                if (c != null) {
                    getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mAislePaint == null) {
            mAislePaint = new Paint();
            mAislePaint.setColor(Color.BLUE);
        }
        if (mDebugPaint == null) {
            mDebugPaint = new Paint();
            mDebugPaint.setColor(Color.GRAY);
        }
        if (mPathPaint == null) {
            mPathPaint = new Paint();
            mPathPaint.setColor(Color.YELLOW);
        }
        if (mUserPaint == null) {
            mUserPaint = new Paint();
            mUserPaint.setColor(Color.RED);
        }
        if (mBeaconPaint == null) {
            mBeaconPaint = new Paint();
            mBeaconPaint.setColor(Color.CYAN);
        }
        if (mTextPaint == null) {
            mTextPaint = new Paint();
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            mTextPaint.setTextSize(50);
        }
        canvas.drawColor(Color.WHITE);

        mTileWidth = canvas.getWidth() / mStoreMap.getWidthInTiles();
        mTileHeight = canvas.getHeight() / mStoreMap.getHeightInTiles();

        float mapX;
        float mapY;
        //Draw Tiles
        for (int x = 0; x < mStoreMap.getWidthInTiles(); x++) {
            for (int y = 0; y < mStoreMap.getHeightInTiles(); y++) {
                mapX = x * mTileWidth;
                mapY = y * mTileHeight;
                if (mStoreMap.getTerrain(x, y) == StoreMap.AISLE) {
                    Log.v(TAG, "Store Map At Position : " + x + " " + y + " " + (x * mTileWidth));
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mAislePaint);
                } else {
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mDebugPaint);//TODO: REMOVE THIS . FOR DEUBG PURPOSE
                }

                if (path != null) {
                    if (path.contains(x, y)) {
                        canvas.drawRect(mapX + mTileWidth / 3, mapY + mTileHeight / 3, mapX + mTileWidth * 2 / 3, mapY + mTileHeight * 2 / 3, mPathPaint);
                    }
                }

                if (mStore.getUserPosition().x == x && mStore.getUserPosition().y == y) {
                    //Draw User
                    canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                }
            }
        }

        //Draw Beacons
        BeaconModel[] beacons = mStore.getBeacons();

        for (BeaconModel beacon : beacons) {
            mapX = beacon.getPoint().x * mTileWidth;
            mapY = beacon.getPoint().y * mTileHeight;
            canvas.drawCircle(mapX, mapY, mTileWidth / 4, mBeaconPaint);
        }

        //Draw Category Text
        canvas.save();
        canvas.rotate(90);
        for (Category category : mStore.getCategories()) {
            mapX = category.getTextTilePositionOnMap().x * mTileWidth;
            mapY = category.getTextTilePositionOnMap().y * mTileHeight;
            canvas.drawText(category.getCategoryName(), mapY, -mapX, mTextPaint);
        }
        canvas.restore();

        switch (DRAW_TYPE) {
            case DRAW_MOVE_USER:
                float userMapX = mStore.getUserPosition().x * mTileWidth;
                float userMapY;
                float oldX = mStore.getUserPosition().x;
                float oldY = mStore.getUserPosition().y;
                float pathY = mStore.getUserPosition().y;
                oldY = oldY - SPEED;
                mStore.setUserPosition(oldX, oldY);
                userMapY = mStore.getUserPosition().y * mTileHeight;
                canvas.drawCircle(userMapX + mTileWidth / 2, userMapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                break;
            case DRAW_MOVE_NONE:
                break;
        }


    }
}
