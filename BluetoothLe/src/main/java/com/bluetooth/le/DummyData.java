package com.bluetooth.le;

import android.graphics.Point;
import android.graphics.PointF;

import com.bluetooth.le.model.BeaconModel;
import com.bluetooth.le.model.Category;
import com.bluetooth.le.model.StoreItem;

import java.util.HashMap;

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
    public static String mapData = "1;1;1;3;18&1;6;1;3;18&1;11;1;3;18";
    public static int storeWidth = 15;
    public static int storeHeight = 20;
    public static PointF userPosition = new PointF(storeWidth-1, storeHeight-1);
    public static BeaconModel[] beacons;
    public static Category[] categories;
    public static HashMap<String,StoreItem> items;
    public static HashMap<String,StoreItem> userItems;

    static {
        beacons = new BeaconModel[6];
        beacons[0] = new BeaconModel(BeaconModel.AISLE_1_TOP, 2.5f, 1f);
        beacons[1] = new BeaconModel(BeaconModel.AISLE_1_BOTTOM, 2.5f, 19f);
        beacons[2] = new BeaconModel(BeaconModel.AISLE_2_TOP, 7.5f, 1f);
        beacons[3] = new BeaconModel(BeaconModel.AISLE_2_BOTTOM, 7.5f, 19f);
        beacons[4] = new BeaconModel(BeaconModel.AISLE_3_TOP, 12.5f, 1f);
        beacons[5] = new BeaconModel(BeaconModel.AISLE_3_BOTTOM, 12.5f, 19f);

        categories = new Category[12];
        categories[0] = new Category(Category.ELECTRONICS, "Electronics", beacons[0], new Point(0,3), new PointF(1.5f,3f));
        categories[1] = new Category(Category.KITCHEN, "Kitchen", beacons[0], new Point(4,3), new PointF(3f,3f));
        categories[2] = new Category(Category.FURNITURE, "Furniture", beacons[1], new Point(0,14), new PointF(1.5f,14f));
        categories[3] = new Category(Category.APPAREL, "Apparel", beacons[1], new Point(4,14), new PointF(3f,14f));
        categories[4] = new Category(Category.VIDEO_GAMES, "Video Games", beacons[2], new Point(5,3), new PointF(6.5f,3f));
        categories[5] = new Category(Category.TOYS, "Toys", beacons[2], new Point(9,3), new PointF(8f,3f));
        categories[6] = new Category(Category.GROCERY, "Grocery", beacons[3], new Point(5,14), new PointF(6.5f,14f));
        categories[7] = new Category(Category.MOVIES, "Movies", beacons[3], new Point(9,14), new PointF(8f,14f));
        categories[8] = new Category(Category.AUTO, "Auto", beacons[4], new Point(10,3), new PointF(11.5f,3f));
        categories[9] = new Category(Category.HEALTH, "Health", beacons[4], new Point(14,3), new PointF(13f,3f));
        categories[10] = new Category(Category.JEWELRY, "Jewelry", beacons[5], new Point(10,14), new PointF(11.5f,14f));
        categories[11] = new Category(Category.OFFICE, "Office", beacons[5], new Point(14,14), new PointF(13f,14f));

        items = new HashMap<String, StoreItem>();
        items.put("item1",new StoreItem("item1",categories[0],"Vizio Tv",600.00));
        items.put("item2",new StoreItem("item2",categories[1],"Tall Kitchen Bags",6.97));
        items.put("item3",new StoreItem("item3",categories[2],"Sofa",150.00));
        items.put("item4",new StoreItem("item4",categories[3],"Jeans",20.00));
        items.put("item5",new StoreItem("item5",categories[4],"Xbox One",500.00));
        items.put("item6",new StoreItem("item6",categories[5],"Iron Man Figure",10.00));
        items.put("item7",new StoreItem("item7",categories[6],"Milk",5.00));
        items.put("item8",new StoreItem("item8",categories[7],"Spiderman",10.00));
        items.put("item9",new StoreItem("item9",categories[8],"Tire",70.00));
        items.put("item10",new StoreItem("item10",categories[9],"Advil",600.00));
        items.put("item11",new StoreItem("item11",categories[10],"Necklace",50.00));
        items.put("item12",new StoreItem("item12",categories[11],"Calculator",20.00));

        userItems = new HashMap<String, StoreItem>();
    }
}
