package com.bluetooth.le.samspathfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bluetooth.le.model.BeaconModel;
import com.bluetooth.le.model.Category;
import com.bluetooth.le.model.Store;
import com.bluetooth.le.model.User;
import com.bluetooth.le.pathfinding.AStarPathFinder;
import com.bluetooth.le.pathfinding.Path;
import com.bluetooth.le.pathfinding.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by stadiko on 1/13/14.
 */
public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MapSurfaceView.class.getSimpleName();

    public static final int DRAW_MOVE_NONE = 1;
    public static final int DRAW_MOVE_USER = 2;

    public static int DRAW_TYPE = 0;

    private static int SPEED = 4;

    private SurfaceHolder mHolder;
    private Store mStore;
    private StoreMap mStoreMap;
    private Paint mAislePaint;
    private Paint mDebugPaint;
    private Paint mPathPaint;
    private Paint mUserPaint;
    private Paint mBeaconPaint;
    private Paint mTextPaint;
    private Paint mSmallTextPaint;
    private Paint mItemPaint;

    private PathFinder finder;
    private Path[] paths;
    private int mWidth;
    private int mHeight;
    private float mTileWidth;
    private float mTileHeight;

    private User mUser;
    private Path mUserPath;
    private int mUserCurrentStepIndex = 0;

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
        mUser = User.getInstance();
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
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        moveUserTo(0,19);
        return true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void refreshUserOnMap() {
        Canvas c = getHolder().lockCanvas();
        onDraw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    public void moveUserTo(int newX, int newY) {
        mUserPath = finder.findPath((int) mUser.getUserPosition().x, (int) mUser.getUserPosition().y, newX, newY);

        DRAW_TYPE = DRAW_MOVE_USER;
        mUserCurrentStepIndex = 0;
        int cnt = 0;
        while (mUserPath != null && mUserCurrentStepIndex < mUserPath.getLength()) {
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
            cnt++;
            if(cnt /SPEED == 1) {
                mUserCurrentStepIndex++;
                cnt = 0;
            }
        }

        DRAW_TYPE = DRAW_MOVE_NONE;
    }

    public void drawPath(int newX, int newY) {
        paths = new Path[1];
        paths[0] = finder.findPath((int) mUser.getUserPosition().x, (int) mUser.getUserPosition().y, newX, newY);
    }

    public void drawPaths(ArrayList<Point> positions) {
        sortComparePoint = new Point((int) mUser.getUserPosition().x, (int) mUser.getUserPosition().y);
        ArrayList<Point> newPositions = new ArrayList<Point>();
        while (positions.size() > 0) {
            Collections.sort(positions, new PositionComparator());
            newPositions.add(positions.get(0));
            sortComparePoint = positions.get(0);
            positions.remove(0);
        }

        paths = new Path[newPositions.size()];
        int prevX = (int) mUser.getUserPosition().x;
        int prevY = (int) mUser.getUserPosition().y;
        for (int i = 0; i < paths.length; i++) {
            paths[i] = finder.findPath(prevX, prevY, newPositions.get(i).x, newPositions.get(i).y);
            prevX = newPositions.get(i).x;
            prevY = newPositions.get(i).y;
        }
    }

    private Point sortComparePoint;

    public class PositionComparator implements Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            int a = finder.findPath(sortComparePoint.x, sortComparePoint.y, o1.x, o1.y).getLength();
            int b = finder.findPath(sortComparePoint.x, sortComparePoint.y, o2.x, o2.y).getLength();
            return a < b ? -1
                    : a > b ? 1
                    : 0;
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
        if (mSmallTextPaint == null) {
            mSmallTextPaint = new Paint();
            mSmallTextPaint.setColor(Color.WHITE);
            mSmallTextPaint.setTextAlign(Paint.Align.CENTER);
            mSmallTextPaint.setTextSize(30);
        }
        if (mItemPaint == null) {
            mItemPaint = new Paint();
            mItemPaint.setColor(Color.BLACK);
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

                if (paths != null) {
                    for (Path path : paths) {
                        if (path.contains(x, y)) {
                            canvas.drawRect(mapX + mTileWidth / 3, mapY + mTileHeight / 3, mapX + mTileWidth * 2 / 3, mapY + mTileHeight * 2 / 3, mPathPaint);
                        }
                    }
                }

                if (mUser.getUserPosition().x == x && mUser.getUserPosition().y == y && DRAW_TYPE != DRAW_MOVE_USER) {
                    //Draw User
                    canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                }
            }
        }

        //Drawing items on map
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                Path path = paths[i];
                mapX = path.getStep(path.getLength() - 1).getX() * mTileWidth;
                mapY = path.getStep(path.getLength() - 1).getY() * mTileHeight;
                canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 3, mItemPaint);
                canvas.drawText((i + 1) + "", mapX + mTileWidth / 2, mapY + mTileHeight / 2, mSmallTextPaint);
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
                if(mUserPath != null) {
                    mUser.setUserPosition(mUserPath.getStep(mUserCurrentStepIndex).getX(),mUserPath.getStep(mUserCurrentStepIndex).getY());
                    mapX = mUserPath.getStep(mUserCurrentStepIndex).getX() * mTileWidth;
                    mapY = mUserPath.getStep(mUserCurrentStepIndex).getY() * mTileHeight;
                    canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                }
                break;
            case DRAW_MOVE_NONE:
                break;
        }


    }
}
