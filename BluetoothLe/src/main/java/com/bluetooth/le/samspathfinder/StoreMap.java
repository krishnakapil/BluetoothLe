package com.bluetooth.le.samspathfinder;

import com.bluetooth.le.pathfinding.Mover;
import com.bluetooth.le.pathfinding.TileBasedMap;

/**
 * Created by stadiko on 1/11/14.
 */
public class StoreMap implements TileBasedMap {
    /**
     * The map width in tiles
     */
    public static final int WIDTH = 100;
    /**
     * The map height in tiles
     */
    public static final int HEIGHT = 100;

    /**
     * Indicate aisle terrain at a given location
     */
    public static final int AISLE = 0;

    /**
     * Indicate walkable path terrain at a given location
     */
    public static final int WALKABLE = 1;

    /**
     * Indicate person terrain at a given location
     */
    public static final int PERSON = 2;

    /**
     * The terrain settings for each tile in the map
     */
    private int[][] terrain = new int[WIDTH][HEIGHT];
    /**
     * The unit in each tile of the map
     */
    private int[][] units = new int[WIDTH][HEIGHT];
    /**
     * Indicator if a given tile has been visited during the search
     */
    private boolean[][] visited = new boolean[WIDTH][HEIGHT];

    public StoreMap() {
        //TODO : Populate the map data here
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
     * Get the unit at a given location
     *
     * @param x The x coordinate of the tile to check for a unit
     * @param y The y coordinate of the tile to check for a unit
     * @return The ID of the unit at the given location or 0 if there is no unit
     */
    public int getUnit(int x, int y) {
        return units[x][y];
    }

    /**
     * Set the unit at the given location
     *
     * @param x    The x coordinate of the location where the unit should be set
     * @param y    The y coordinate of the location where the unit should be set
     * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
     *             given location
     */
    public void setUnit(int x, int y, int unit) {
        units[x][y] = unit;
    }

    /**
     * @see TileBasedMap#blocked(com.bluetooth.le.pathfinding.Mover, int, int)
     */
    public boolean blocked(Mover mover, int x, int y) {
        // if theres a unit at the location, then it's blocked
        if (getUnit(x, y) != 0) {
            return true;
        }

        int unit = PERSON;//((UnitMover) mover).getType();

        // person can only move across walkable path
        if (unit == PERSON) {
            return terrain[x][y] != WALKABLE;
        }

        // unknown unit so everything blocks
        return true;
    }

    /**
     * @see TileBasedMap#getCost(Mover, int, int, int, int)
     */
    public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
        return 1;
    }

    /**
     * @see TileBasedMap#getHeightInTiles()
     */
    public int getHeightInTiles() {
        return WIDTH;
    }

    /**
     * @see TileBasedMap#getWidthInTiles()
     */
    public int getWidthInTiles() {
        return HEIGHT;
    }

    /**
     * @see TileBasedMap#pathFinderVisited(int, int)
     */
    public void pathFinderVisited(int x, int y) {
        visited[x][y] = true;
    }
}
