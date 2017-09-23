package the_fireplace.shoprun;

public enum EnumDataKey {
	ITEM_NAME,
	DEFAULT_INITIAL_PRICE,
	GE_PRICE,
	DEFAULT_NUMBER_PER_STORE,
	STACKABLE,
	SELL_SPEED,
	LOCATION_DATA;

	static Class getDataType(EnumDataKey key) {
		switch (key) {
			case SELL_SPEED:
			case ITEM_NAME:
				return String.class;
			case STACKABLE:
				return Boolean.class;
			case LOCATION_DATA:
				return LocationData[].class;
			default:
				return Integer.class;
		}
	}
}
