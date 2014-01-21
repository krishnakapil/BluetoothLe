package com.bluetooth.le;

import android.graphics.Point;
import android.graphics.PointF;

import com.bluetooth.le.model.BeaconModel;
import com.bluetooth.le.model.Category;
import com.bluetooth.le.model.StoreItem;

/**
 * Created by stadiko on 1/20/14.
 */
public class DummyData {
    public static String storeId = "san-bruno";
    /**
     * Data from the server
     * Object Type refer to StoreMap (AISLE = 1 ,ENTRANCE = 2)
     * 0;1;1;2;8 - OBJECT Type ; X coordinate ; Y coordinate ; Width ; Height
     */
    public static String mapData = "1;1;1;3;18&1;5;1;3;18&1;9;1;3;18";
    public static int storeWidth = 13;
    public static int storeHeight = 20;
    public static PointF userPosition = new PointF(storeWidth - 1, storeHeight - 1);
    public static BeaconModel[] beacons;
    public static Category[] categories;
    public static StoreItem[] items;

    static {
        beacons = new BeaconModel[6];
        beacons[0] = new BeaconModel(BeaconModel.AISLE_1_TOP, 2.5f, 1f);
        beacons[1] = new BeaconModel(BeaconModel.AISLE_1_BOTTOM, 2.5f, 19f);
        beacons[2] = new BeaconModel(BeaconModel.AISLE_2_TOP, 6.5f, 1f);
        beacons[3] = new BeaconModel(BeaconModel.AISLE_2_BOTTOM, 6.5f, 19f);
        beacons[4] = new BeaconModel(BeaconModel.AISLE_3_TOP, 10.5f, 1f);
        beacons[5] = new BeaconModel(BeaconModel.AISLE_3_BOTTOM, 10.5f, 19f);

        categories = new Category[12];
        categories[0] = new Category(Category.ELECTRONICS, "Electronics", beacons[0], new Point(0,3), new PointF(1.5f,3f));
        categories[1] = new Category(Category.KITCHEN, "Kitchen", beacons[0], new Point(4,3), new PointF(3f,3f));
        categories[2] = new Category(Category.FURNITURE, "Furniture", beacons[1], new Point(0,14), new PointF(1.5f,14f));
        categories[3] = new Category(Category.APPAREL, "Apparel", beacons[1], new Point(4,14), new PointF(3f,14f));
        categories[4] = new Category(Category.VIDEO_GAMES, "Video Games", beacons[2], new Point(4,3), new PointF(5.5f,3f));
        categories[5] = new Category(Category.TOYS, "Toys", beacons[2], new Point(8,3), new PointF(7f,3f));
        categories[6] = new Category(Category.GROCERY, "Grocery", beacons[3], new Point(4,14), new PointF(5.5f,14f));
        categories[7] = new Category(Category.MOVIES, "Movies", beacons[3], new Point(8,14), new PointF(7f,14f));
        categories[8] = new Category(Category.AUTO, "Auto", beacons[4], new Point(8,3), new PointF(9.5f,3f));
        categories[9] = new Category(Category.HEALTH, "Health", beacons[4], new Point(12,3), new PointF(11f,3f));
        categories[10] = new Category(Category.JEWELRY, "Jewelry", beacons[5], new Point(8,14), new PointF(9.5f,14f));
        categories[11] = new Category(Category.OFFICE, "Office", beacons[5], new Point(12,14), new PointF(11f,14f));

        items = new StoreItem[12];
        items[0] = new StoreItem("item1",categories[0],"Vizio Tv",600.00);
        items[1] = new StoreItem("item1",categories[1],"Vizio Tv",600.00);
        items[2] = new StoreItem("item1",categories[2],"Vizio Tv",600.00);
        items[3] = new StoreItem("item1",categories[3],"Vizio Tv",600.00);
        items[4] = new StoreItem("item1",categories[4],"Vizio Tv",600.00);
        items[5] = new StoreItem("item1",categories[5],"Vizio Tv",600.00);
        items[6] = new StoreItem("item1",categories[6],"Vizio Tv",600.00);
        items[7] = new StoreItem("item1",categories[7],"Vizio Tv",600.00);
        items[8] = new StoreItem("item1",categories[8],"Vizio Tv",600.00);
        items[9] = new StoreItem("item1",categories[9],"Vizio Tv",600.00);
        items[10] = new StoreItem("item1",categories[10],"Vizio Tv",600.00);
        items[11] = new StoreItem("item1",categories[11],"Vizio Tv",600.00);
    }
}
