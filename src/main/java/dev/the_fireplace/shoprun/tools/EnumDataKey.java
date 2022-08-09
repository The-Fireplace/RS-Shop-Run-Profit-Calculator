package dev.the_fireplace.shoprun.tools;

import dev.the_fireplace.shoprun.LocationData;

public enum EnumDataKey
{
    ITEM_NAME,
    DEFAULT_INITIAL_PRICE,
    GE_PRICE,
    DEFAULT_NUMBER_PER_STORE,
    STACKABLE,
    SELL_SPEED,
    LOCATION_DATA;

    public static Class<?> getDataType(EnumDataKey key) {
        return switch (key) {
            case SELL_SPEED, ITEM_NAME -> String.class;
            case STACKABLE -> Boolean.class;
            case LOCATION_DATA -> LocationData[].class;
            default -> Integer.class;
        };
    }
}
