package net.cloudescape.skyblock.island.shop;

/**
 * Created by Matthew E on 4/9/2018.
 */
public enum ShopType {

    SELL,
    BUY;

    public static ShopType getShopTypeByName(String name) {
        for (ShopType shopType : values()) {
            if (shopType.toString().equalsIgnoreCase(name)) {
                return shopType;
            }
        }
        return null;
    }
}
