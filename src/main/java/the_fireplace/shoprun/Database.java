package the_fireplace.shoprun;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class Database implements Serializable {
	public static final long serialVersionUID = 0xA17DA7AB;
	private static final String databaseFileName = "shopruns.dat";

	private static Database instance;
	private static boolean initDone;

	//Identifier, DataKey, StoredData
	private HashMap<String, HashMap<String, Object>> itemDatabase;

	//Data getters and setters
	static void setData(String identifier, EnumDataKey dataKey, Object value) {
		if (!value.getClass().isAssignableFrom(EnumDataKey.getDataType(dataKey)))
			throw new IllegalArgumentException(value.toString() + " is not valid for the data type " + dataKey.name());
		instance.itemDatabase.get(identifier).put(dataKey.name(), value);
	}

	static boolean isValidData(String identifier, String itemName, int initialPrice, int gePrice, int numberPerStore, String[] locations) {
		return !identifier.isEmpty()
				&& !itemName.isEmpty()
				&& initialPrice >= 0
				&& gePrice > 0
				&& numberPerStore > 0
				&& locations.length > 0;
	}

	static boolean isValidIdentifier(String identifier) {
		return !instance.itemDatabase.containsKey(identifier);
	}

	static void addNewItemData(String identifier, String itemName, int initialPrice, int gePrice, int numberPerStore, boolean stackable, String sellSpeed, String[] locations) {
		instance.itemDatabase.put(identifier, new HashMap<>());
		setData(identifier, EnumDataKey.ITEM_NAME, itemName);
		setData(identifier, EnumDataKey.INITIAL_PRICE, initialPrice);
		setData(identifier, EnumDataKey.GE_PRICE, gePrice);
		setData(identifier, EnumDataKey.NUMBER_PER_STORE, numberPerStore);
		setData(identifier, EnumDataKey.STACKABLE, stackable);
		setData(identifier, EnumDataKey.SELL_SPEED, sellSpeed.isEmpty() ? "Unknown" : sellSpeed);
		setData(identifier, EnumDataKey.LOCATIONS, locations);
	}

	static String[] getIdentifiers() {
		String[] identifiers = new String[instance.itemDatabase.keySet().size()];
		int index = 0;
		for (String s : instance.itemDatabase.keySet())
			identifiers[index++] = s;
		return identifiers;
	}

	static int getInitialPrice(String identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.INITIAL_PRICE.name());
	}

	static int getGEPrice(String identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.GE_PRICE.name());
	}

	static int getAmountPerStore(String identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.NUMBER_PER_STORE.name());
	}

	static boolean getStackable(String identifier) {
		return (boolean) instance.itemDatabase.get(identifier).get(EnumDataKey.STACKABLE.name());
	}

	static String getItemName(String identifier) {
		return (String) instance.itemDatabase.get(identifier).get(EnumDataKey.ITEM_NAME.name());
	}

	static String getItemSellSpeed(String identifier) {
		return (String) instance.itemDatabase.get(identifier).get(EnumDataKey.SELL_SPEED.name());
	}

	static String[] getLocations(String identifier) {
		return (String[]) instance.itemDatabase.get(identifier).get(EnumDataKey.LOCATIONS.name());
	}

	static String[] getAllSellSpeeds() {
		List<String> speeds = new ArrayList<>();
		for (String ident : getIdentifiers()) {
			String speed = getItemSellSpeed(ident);
			if (!speeds.contains(speed))
				speeds.add(speed);
		}
		String[] out = new String[speeds.size()];
		for (int i = 0; i < speeds.size(); i++)
			out[i] = speeds.get(i);
		return out;
	}

	static int getProfitPerItem(String identifier) {
		return getGEPrice(identifier) - getInitialPrice(identifier);
	}

	static int getProfitPerStore(String identifier) {
		return getProfitPerItem(identifier) * getAmountPerStore(identifier);
	}

	static int getProfitPerStorePerRun(String identifier) {
		if (!getStackable(identifier) && getAmountPerStore(identifier) > 28)
			return getProfitPerItem(identifier) * 28;
		else
			return getProfitPerStore(identifier);
	}

	static float getProfitMarginPercent(String identifier) {
		return Float.valueOf(String.format("%.2f", (1 - ((float) getInitialPrice(identifier)) / ((float) getGEPrice(identifier))) * 100));
	}

	static void deleteData(String identifier) {
		instance.itemDatabase.remove(identifier);
	}

	//Database save/load code below this point

	private Database() {
		itemDatabase = new HashMap<>();
		instance = this;
		initDone = true;
	}

	static void init() {
		if (!initDone)
			loadFromFile();
	}

	static void save() {
		saveToFile();
	}

	private static void loadFromFile() {
		File f = new File(databaseFileName);
		if (f.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
				instance = (Database) stream.readObject();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				instance = new Database();
				f.delete();
			}
		}
		if (instance == null)
			instance = new Database();
	}

	private static void saveToFile() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(databaseFileName)));
			out.writeObject(instance);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
