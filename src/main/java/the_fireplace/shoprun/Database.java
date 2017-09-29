package the_fireplace.shoprun;

import javafx.util.Pair;

import java.io.*;
import java.util.*;

final class Database implements Serializable {
	private static final long serialVersionUID = 0x1A21E510;
	private static final String databaseFileName = "shopruns.dat";

	private static Database instance;
	private static boolean initDone;

	//Identifier, DataKey, StoredData
	private HashMap<Integer, HashMap<String, Object>> itemDatabase;

	//Store the potential identifiers for each item here, to avoid being locked out of the API for calling it too much.
	private HashMap<String, ArrayList<Integer>> allIdentsForItem;

	static HashMap<String, ArrayList<Integer>> getAllIdentsForItem() {
		if(instance.allIdentsForItem == null)
			return instance.allIdentsForItem = new HashMap<>();
		else
			return instance.allIdentsForItem;
	}

	static void addPossibleItemIdents(String itemName, ArrayList<Integer> idents){
		instance.allIdentsForItem.put(itemName, idents);
	}

	//Data getters and setters
	static void setData(int identifier, EnumDataKey dataKey, Object value) {
		if (!value.getClass().isAssignableFrom(EnumDataKey.getDataType(dataKey)))
			throw new IllegalArgumentException(value.toString() + " is not valid for the data type " + dataKey.name());
		instance.itemDatabase.get(identifier).put(dataKey.name(), value);
	}

	static boolean isValidData(String itemName, int initialPrice, int gePrice, int numberPerStore, LocationData[] locations) {
		return !itemName.isEmpty()
				&& initialPrice >= 0
				&& gePrice > 0
				&& numberPerStore > 0
				&& locations.length > 0;
	}

	static boolean isValidIdentifier(int identifier) {
		return !instance.itemDatabase.containsKey(identifier);
	}

	static void addNewItemData(int identifier, String itemName, int initialPrice, int gePrice, int numberPerStore, boolean stackable, String sellSpeed, LocationData[] locations) {
		instance.itemDatabase.put(identifier, new HashMap<>());
		setData(identifier, EnumDataKey.ITEM_NAME, itemName);
		setData(identifier, EnumDataKey.DEFAULT_INITIAL_PRICE, initialPrice);
		setData(identifier, EnumDataKey.GE_PRICE, gePrice);
		setData(identifier, EnumDataKey.DEFAULT_NUMBER_PER_STORE, numberPerStore);
		setData(identifier, EnumDataKey.STACKABLE, stackable);
		setData(identifier, EnumDataKey.SELL_SPEED, sellSpeed.isEmpty() ? "Unknown" : sellSpeed);
		setData(identifier, EnumDataKey.LOCATION_DATA, locations);
	}

	static Integer[] getIdentifiers() {
		Integer[] identifiers = new Integer[instance.itemDatabase.keySet().size()];
		int index = 0;
		for (int s : instance.itemDatabase.keySet())
			identifiers[index++] = s;
		return identifiers;
	}

	static int getDefaultInitialPrice(int identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.DEFAULT_INITIAL_PRICE.name());
	}

	static int getGEPrice(int identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.GE_PRICE.name());
	}

	static int getDefaultAmountPerStore(int identifier) {
		return (int) instance.itemDatabase.get(identifier).get(EnumDataKey.DEFAULT_NUMBER_PER_STORE.name());
	}

	static boolean getStackable(int identifier) {
		return (boolean) instance.itemDatabase.get(identifier).get(EnumDataKey.STACKABLE.name());
	}

	static String getItemName(int identifier) {
		return (String) instance.itemDatabase.get(identifier).get(EnumDataKey.ITEM_NAME.name());
	}

	static String getItemSellSpeed(int identifier) {
		return (String) instance.itemDatabase.get(identifier).get(EnumDataKey.SELL_SPEED.name());
	}

	static LocationData[] getLocations(int identifier) {
		return (LocationData[]) instance.itemDatabase.get(identifier).get(EnumDataKey.LOCATION_DATA.name());
	}

	static String[] getAllSellSpeeds() {
		List<String> speeds = new ArrayList<>();
		for (int ident : getIdentifiers()) {
			String speed = getItemSellSpeed(ident);
			if (!speeds.contains(speed))
				speeds.add(speed);
		}
		String[] out = new String[speeds.size()];
		for (int i = 0; i < speeds.size(); i++)
			out[i] = speeds.get(i);
		return out;
	}

	static int getDefaultProfitPerItem(int identifier) {
		return getGEPrice(identifier) - getDefaultInitialPrice(identifier);
	}

	static int getDefaultProfitPerStore(int identifier) {
		return getDefaultProfitPerItem(identifier) * getDefaultAmountPerStore(identifier);
	}

	static int getDefaultProfitPerStorePerRun(int identifier) {
		if (!getStackable(identifier) && getDefaultAmountPerStore(identifier) > 28)
			return getDefaultProfitPerItem(identifier) * 28;
		else
			return getDefaultProfitPerStore(identifier);
	}

	static float getDefaultProfitMarginPercent(int identifier) {
		return Float.valueOf(String.format("%.2f", (1 - ((float) getDefaultInitialPrice(identifier)) / ((float) getGEPrice(identifier))) * 100));
	}

	static void deleteData(int identifier) {
		instance.itemDatabase.remove(identifier);
	}

	//Location-based data getters
	static LinkedList<Pair<Integer, Integer>> getInitialPricesAndStoreStocks(int identifier){
		LocationData[] locationData = getLocations(identifier);
		LinkedList<Pair<Integer, Integer>> outList = new LinkedList<>();
		for (LocationData loc : locationData) {
			Pair<Integer, Integer> locPriceDat = new Pair<>(loc.itemPrice, loc.itemCount);
			if (!outList.contains(locPriceDat))
				outList.add(locPriceDat);
		}
		return outList;
	}

	static int getInitialPrice(int identifier, LocationData loc){
		return loc.itemPrice == -1 ? getDefaultInitialPrice(identifier) : loc.itemPrice;
	}

	static int getAmountPerStore(int identifier, LocationData loc){
		return loc.itemCount == -1 ? getDefaultAmountPerStore(identifier) : loc.itemCount;
	}

	static int getInitialPrice(int identifier, Pair<Integer, Integer> data){
		return data.getKey() == -1 ? getDefaultInitialPrice(identifier) : data.getKey();
	}

	static int getAmountPerStore(int identifier, Pair<Integer, Integer> data){
		return data.getValue() == -1 ? getDefaultAmountPerStore(identifier) : data.getValue();
	}

	static float getProfitMarginPercent(int identifier, Pair<Integer, Integer> data){
		return Float.valueOf(String.format("%.2f", (1 - ((float) getInitialPrice(identifier, data)) / ((float) getGEPrice(identifier))) * 100));
	}

	static int getProfitPerItem(int identifier, Pair<Integer, Integer> data){
		return getGEPrice(identifier) - getInitialPrice(identifier, data);
	}

	static int getProfitPerStore(int identifier, Pair<Integer, Integer> data) {
		return getProfitPerItem(identifier, data) * getAmountPerStore(identifier, data);
	}

	static int getProfitPerStorePerRun(int identifier, Pair<Integer, Integer> data) {
		if (!getStackable(identifier) && getAmountPerStore(identifier, data) > 28)
			return getProfitPerItem(identifier, data) * 28;
		else
			return getProfitPerStore(identifier, data);
	}

	static ArrayList<LocationData> locationsWithPair(int identifier, LocationData[] data, Pair<Integer, Integer> pair){
		ArrayList<LocationData> out = new ArrayList<>();
		for(LocationData loc : data)
			if(getInitialPrice(identifier, pair) == getInitialPrice(identifier, loc) && getAmountPerStore(identifier, pair) == getAmountPerStore(identifier, loc))
				out.add(loc);
		return out;
	}

	//Database save/load code below this point

	private Database() {
		itemDatabase = new HashMap<>();
		allIdentsForItem = new HashMap<>();
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
