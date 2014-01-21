package com.bluetooth.le.samspathfinder;

import android.util.Log;

import com.bluetooth.le.pathfinding.TileBasedMap;

/**
 * Created by stadiko on 1/11/14.
 */
public class StoreMap implements TileBasedMap {

    private static final String TAG = StoreMap.class.getSimpleName();
    /**
     * Indicate aisle terrain at a given location
     */
    public static final int AISLE = 1;
    /**
     * The map width in tiles
     */
    private int WIDTH;
    /**
     * The map height in tiles
     */
    private int HEIGHT;

    /**
     * The terrain settings for each tile in the map
     */
    private int[][] terrain;

    /**
     * Indicator if a given tile has been visited during the search
     */
    private boolean[][] visited;

    /**
     * Inputs required to create a map
     * Number of horizontal and vertical tiles the map is divided into
     * What Object is at each tile
     * Map Id to Load
     */
    public StoreMap(int width, int height, String data) {
        WIDTH = width;
        HEIGHT = height;

        terrain = new int[WIDTH][HEIGHT];
        visited = new boolean[WIDTH][HEIGHT];

        String[] val = data.split("&");
        for (String line : val) {
            String[] lineData = line.split(";");
            fillArea(Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2]), Integer.parseInt(lineData[3]), Integer.parseInt(lineData[4]), Integer.parseInt(lineData[0]));
        }
    }

    /**
     * Fill an area with a given terrain type
     *
     * @param x      The x coordinate to start filling at
     * @param y      The y coordinate to start filling at
     * @param width  The width of the area to fill
     * @param height The height of the area to fill
     * @param type   The terrain type to fill with
     */
    private void fillArea(int x, int y, int width, int height, int type) {
        for (int xp = x; xp < x + width; xp++) {
            for (int yp = y; yp < y + height; yp++) {
                Log.v(TAG, "(x,y) : " + xp + " " + yp + " TYPE : " + type);
                terrain[xp][yp] = type;
            }
        }
    }

    /**
     * Clear the array marking which tiles have been visted by the path
     * finder.
     */
    public void clearVisited() {
        for (int x = 0; x < getWidthInTiles(); x++) {
            for (int y = 0; y < getHeightInTiles(); y++) {
                visited[x][y] = false;
            }
        }
    }

    /**
     * @see TileBasedMap
     */
    public boolean visited(int x, int y) {
        return visited[x][y];
    }

    /**
     * Get the terrain at a given location
     *
     * @param x The x coordinate of the terrain tile to retrieve
     * @param y The y coordinate of the terrain tile to retrieve
     * @return The terrain tile at the given location
     */
    public int getTerrain(int x, int y) {
        return terrain[x][y];
    }

    /**
     * @see TileBasedMap#blocked(int, int)
     */
    public boolean blocked(int x, int y) {
        return terrain[x][y] == AISLE;
    }

    /**
     * @see TileBasedMap#getCost(int, int, int, int)
     */
    public float getCost(int sx, int sy, int tx, int ty) {
        return 1;
    }

    /**
     * @see TileBasedMap#getHeightInTiles()
     */
    public int getHeightInTiles() {
        return HEIGHT;
    }

    /**
     * @see TileBasedMap#getWidthInTiles()
     */
    public int getWidthInTiles() {
        return WIDTH;
    }

    /**
     * @see TileBasedMap#pathFinderVisited(int, int)
     */
    public void pathFinderVisited(int x, int y) {
        visited[x][y] = true;
    }

}
