package the_fireplace.shoprun;

public enum EnumDataKey {
	ITEM_NAME,
	INITIAL_PRICE,
	GE_PRICE,
	NUMBER_PER_STORE,
	STACKABLE,
	SELL_SPEED,
	LOCATIONS;

	static Class getDataType(EnumDataKey key) {
		switch (key) {
			case SELL_SPEED:
			case ITEM_NAME:
				return String.class;
			case STACKABLE:
				return Boolean.class;
			case LOCATIONS:
				return String[].class;
			default:
				return Integer.class;
		}
	}
}
