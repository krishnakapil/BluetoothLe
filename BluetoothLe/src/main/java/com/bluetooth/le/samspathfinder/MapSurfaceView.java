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
    private StoreMap mStoreMap;
    private Paint mAislePaint;
    private Paint mDebugPaint;
    private Paint mPathPaint;
    private Paint mUserPaint;

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

    public MapSurfaceView(Context context, StoreMap map) {
        this(context, null, 0, map);
    }

    public MapSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public MapSurfaceView(Context context, AttributeSet attrs, int defStyle, StoreMap map) {
        super(context, attrs, defStyle);
        finder = new AStarPathFinder(map, 500, true);
        mStoreMap = map;
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
        moveUserTo(4, 0);
        return true;
    }

    public void moveUserTo(int newX, int newY) {
        mNewX = newX;
        mNewY = newY;
        path = finder.findPath((int) mStoreMap.getUserPosition().x, (int) mStoreMap.getUserPosition().y, newX, newY);
        DRAW_TYPE = DRAW_MOVE_USER;
        while (mStoreMap.getUserPosition().y >= mNewY) {
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
        canvas.drawColor(Color.WHITE);

        mTileWidth = canvas.getWidth() / mStoreMap.getWidthInTiles();
        mTileHeight = canvas.getHeight() / mStoreMap.getHeightInTiles();

        //Draw Tiles
        for (int x = 0; x < mStoreMap.getWidthInTiles(); x++) {
            for (int y = 0; y < mStoreMap.getHeightInTiles(); y++) {
                float mapX = x * mTileWidth;
                float mapY = y * mTileHeight;
                if (mStoreMap.getTerrain(x, y) == StoreMap.AISLE) {
                    Log.v(TAG, "Store Map At Position : " + x + " " + y + " " + (x * mTileWidth));
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mAislePaint);
                } else {
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mDebugPaint);//TODO: REMOVE THIS . FOR DEUBG PURPOSE
                }

                if (path != null) {
                    if (path.contains(x, y)) {
                        canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mPathPaint);
                    }
                }

                if (mStoreMap.getUserPosition().x == x && mStoreMap.getUserPosition().y == y) {
                    //Draw User
                    canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                }
            }
        }

        switch (DRAW_TYPE) {
            case DRAW_MOVE_USER:
                float userMapX = mStoreMap.getUserPosition().x * mTileWidth;
                float userMapY;
                float oldX = mStoreMap.getUserPosition().x;
                float oldY = mStoreMap.getUserPosition().y;
                float pathY = mStoreMap.getUserPosition().y;
                oldY = oldY - SPEED;
                mStoreMap.setUserPosition(oldX, oldY);
                userMapY = mStoreMap.getUserPosition().y * mTileHeight;
                canvas.drawCircle(userMapX + mTileWidth / 2, userMapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                break;
            case DRAW_MOVE_NONE:
                break;
        }


    }
}
