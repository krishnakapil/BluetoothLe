package com.bluetooth.le.samspathfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    private enum PATH_DIRECTION {TOP, LEFT, BOTTOM, RIGHT};

    private float userX;
    private float userY;
    private static float SPEED = 8f;//Higher the value .. slower the user will move

    private SurfaceHolder mHolder;
    private Store mStore;
    private StoreMap mStoreMap;
    private Paint mAislePaint;
    private Paint mDebugPaint;
    private Paint mPathPaint;
    private Paint mUserPaint;
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moveUserTo(0,0);
        }

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

        userX = mUser.getUserPosition().x;
        userY = mUser.getUserPosition().y;

        PATH_DIRECTION axisChange = PATH_DIRECTION.LEFT;

        if (mUserPath != null) {
            axisChange = setNextStepDirection(mUserPath, userX, userY, mUserCurrentStepIndex);
        }

        while (mUserPath != null && mUserCurrentStepIndex < mUserPath.getLength() - 1) {
            cnt++;
            if (axisChange == PATH_DIRECTION.LEFT) {
                userX = mUserPath.getStep(mUserCurrentStepIndex).getX() - (cnt / SPEED);
            } else if (axisChange == PATH_DIRECTION.RIGHT) {
                userX = mUserPath.getStep(mUserCurrentStepIndex).getX() + (cnt / SPEED);
            } else if (axisChange == PATH_DIRECTION.TOP) {
                userY = mUserPath.getStep(mUserCurrentStepIndex).getY() - (cnt / SPEED);
            } else {
                userY = mUserPath.getStep(mUserCurrentStepIndex).getY() + (cnt / SPEED);
            }

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

            if (cnt / SPEED >= 1.0) {
                mUserCurrentStepIndex++;
                cnt = 0;
                if (mUserPath != null && mUserCurrentStepIndex + 1 < mUserPath.getLength()) {
                    axisChange = setNextStepDirection(mUserPath, userX, userY, mUserCurrentStepIndex);
                }
                mUser.setUserPosition(mUserPath.getStep(mUserCurrentStepIndex).getX(), mUserPath.getStep(mUserCurrentStepIndex).getY());
            }
        }

        DRAW_TYPE = DRAW_MOVE_NONE;
        mUserPath = null;
    }

    private PATH_DIRECTION setNextStepDirection(Path path, float x, float y, int index) {
        Path.Step step = path.getStep(index + 1);
        PATH_DIRECTION direction;
        if(step.getX() < x) {
            direction = PATH_DIRECTION.LEFT;
        } else if(step.getX() > x) {
            direction = PATH_DIRECTION.RIGHT;
        } else if(step.getY() < y) {
            direction =  PATH_DIRECTION.TOP;
        } else {
            direction = PATH_DIRECTION.BOTTOM;
        }

        Log.v(TAG, "Path Direction : " + x + " " + y + " " + direction + " PAth : " + step.getX() + " " + step.getY());

        return direction;
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
            mAislePaint.setColor(Color.parseColor("#7fbef3"));
        }
        if (mDebugPaint == null) {
            mDebugPaint = new Paint();
            mDebugPaint.setColor(Color.WHITE);
        }
        if (mPathPaint == null) {
            mPathPaint = new Paint();
            mPathPaint.setColor(Color.parseColor("#cde79f"));
        }
        if (mUserPaint == null) {
            mUserPaint = new Paint();
            mUserPaint.setColor(Color.parseColor("#61c46d"));
        }
        if (mTextPaint == null) {
            mTextPaint = new Paint();
            mTextPaint.setColor(Color.BLACK);
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

        float strokeWidth = 5;
        float mapX;
        float mapY;
        //Draw Tiles
        for (int x = 0; x < mStoreMap.getWidthInTiles(); x++) {
            for (int y = 0; y < mStoreMap.getHeightInTiles(); y++) {
                mapX = x * mTileWidth;
                mapY = y * mTileHeight;
                if (mStoreMap.getTerrain(x, y) == StoreMap.AISLE) {
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mAislePaint);
                } else {
                    canvas.drawRect(mapX, mapY, mapX + mTileWidth, mapY + mTileHeight, mDebugPaint);//TODO: REMOVE THIS . FOR DEUBG PURPOSE
                }

                if (paths != null) {
                    for (Path path : paths) {
                        if (path.contains(x, y)) {
                            mPathPaint.setColor(Color.parseColor("#6a8835"));
                            mPathPaint.setStrokeWidth(strokeWidth);
                            mPathPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawRoundRect(new RectF(mapX + mTileWidth / 3, mapY + mTileHeight / 3, mapX + mTileWidth * 2 / 3, mapY + mTileHeight * 2 / 3), 5, 5, mPathPaint);

                            mPathPaint.setColor(Color.parseColor("#cde79f"));
                            mPathPaint.setStrokeWidth(0);
                            mPathPaint.setStyle(Paint.Style.FILL);
                            canvas.drawRect(mapX + mTileWidth / 3 + strokeWidth, mapY + mTileHeight / 3 + strokeWidth, mapX + mTileWidth * 2 / 3 - strokeWidth, mapY + mTileHeight * 2 / 3 - strokeWidth, mPathPaint);
                        }
                    }
                }

                if (mUser.getUserPosition().x == x && mUser.getUserPosition().y == y && DRAW_TYPE != DRAW_MOVE_USER) {
                    drawUser(canvas, mapX + mTileWidth / 2, mapY + mTileHeight / 2);
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
                if (mUserPath != null) {
                    mapX = userX * mTileWidth;
                    mapY = userY * mTileHeight;
                    drawUser(canvas, mapX + mTileWidth / 2, mapY + mTileHeight / 2);
                    canvas.drawCircle(mapX + mTileWidth / 2, mapY + mTileHeight / 2, mTileWidth / 4, mUserPaint);
                }
                break;
            case DRAW_MOVE_NONE:
                break;
        }


    }


    private void drawUser(Canvas canvas, float x, float y) {
        float strokeWidth = 5;
        mUserPaint.setColor(Color.parseColor("#1d7928"));
        mUserPaint.setStrokeWidth(strokeWidth);
        mUserPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x, y, mTileWidth / 3, mUserPaint);

        mUserPaint.setColor(Color.parseColor("#61c46d"));
        mUserPaint.setStrokeWidth(0);
        mUserPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, mTileWidth / 3 - strokeWidth, mUserPaint);
    }
}
