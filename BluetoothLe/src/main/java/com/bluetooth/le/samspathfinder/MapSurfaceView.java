package com.bluetooth.le.samspathfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
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
    private Paint mSmallTextPaint;
    private Paint mItemPaint;

    private PathFinder finder;
    private Path[] paths;
    private int mWidth;
    private int mHeight;
    private float mTileWidth;
    private float mTileHeight;

    private User mUser;

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
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void refreshUserOnMap() {
        mUser.setUserPosition(new PointF(0f,0f));
        Canvas c = getHolder().lockCanvas();
        onDraw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    public void drawPath(int newX, int newY) {
        paths = new Path[1];
        paths[0] = finder.findPath((int) mUser.getUserPosition().x, (int) mUser.getUserPosition().y, newX, newY);
    }

    public void drawPaths(ArrayList<Point> positions) {
        sortComparePoint = new Point((int) mUser.getUserPosition().x, (int) mUser.getUserPosition().y);
        ArrayList<Point> newPositions = new ArrayList<Point>();
        while(positions.size() > 0) {
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

    public class PathComparator implements Comparator<Path> {
        @Override
        public int compare(Path o1, Path o2) {
            int a = o1.getLength();
            int b = o2.getLength();
            return a > b ? -1
                    : a < b ? 1
                    : 0;
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


    public void moveUserTo(int newX, int newY) {
        mNewX = newX;
        mNewY = newY;
        DRAW_TYPE = DRAW_MOVE_USER;
        while (mUser.getUserPosition().y >= mNewY) {
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

                if (mUser.getUserPosition().x == x && mUser.getUserPosition().y == y) {
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
                float userMapX = mUser.getUserPosition().x * mTileWidth;
                float userMapY;
                float oldX = mUser.getUserPosition().x;
                float oldY = mUser.getUserPosition().y;
                float pathY = mUser.getUserPosition().y;
                oldY = oldY - SPEED;
                mUser.setUserPosition(oldX, oldY);
                userMapY = mUser.getUserPosition().y * mTileHeight;
                canvas.drawCircle(userMapX + mTileWidth / 2, userMapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                break;
            case DRAW_MOVE_NONE:
                break;
        }


    }
}
