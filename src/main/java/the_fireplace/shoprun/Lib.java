package the_fireplace.shoprun;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.io.*;
import java.net.URL;

public abstract class Lib {
	public static final String[] F2P_locations = new String[]{"Al Kharid", "Barbarian Village/Gunnarsgrunn", "Burthorpe/Taverley", "Draynor Village", "Dwarven Mines", "Edgeville", "Falador", "Lumbridge", "Port Sarim", "Rimmington", "Varrock", "Wilderness Bandit Camp"};
	public static final String[] P2P_locations = new String[]{"Ape Atoll", "Ardougne", "Brimhaven", "Burgh de Rott", "Canifis", "Catherby", "Darkmeyer", "Desert Bandit Camp", "Dorgesh-Kaan", "Gu'Tanoth", "Jatizso", "Keldagrim", "Lighthouse", "Lletya", "Lunar Isle", "Meiyerditch", "Miscellania", "Mort'ton", "Nardah", "Neitiznot", "Oo'glog", "Piscatoris Fishing Colony", "Pollnivneach", "Port Khazard", "Port Phasmatys", "Prifddinas", "Rellekka", "Seers' Village", "Shantay Pass", "Sophanem", "Shilo Village", "Tai Bwo Wannai", "Tree Gnome Stronghold", "Tree Gnome Village", "Tyras Camp", "TzHaar City", "Void Knights' Outpost", "Yanille", "Zanaris"};

	public static String[] getAllLocations() {
		String[] locs = new String[F2P_locations.length + P2P_locations.length];
		int index = 0;
		for (String loc : F2P_locations)
			locs[index++] = loc;
		for (String loc : P2P_locations)
			locs[index++] = loc;
		return locs;
	}

	public static class IntegerOnlyFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.insert(offset, string);

			if (isInteger(sb.toString()))
				super.insertString(fb, offset, string, attr);
		}

		private boolean isInteger(String text) {
			try {
				int textInt = Integer.parseInt(text);
				return textInt >= 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder();
			sb.append(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text);

			if (isInteger(sb.toString()) || sb.toString().isEmpty())
				super.replace(fb, offset, length, text, attrs);
		}
	}

	private static JsonObject itemsJson;

	static void getRSIds() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getClass().getResourceAsStream("/items_rs3.json")));

			itemsJson = new Gson().fromJson(br, JsonObject.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int getItemIdFromName(String itemName, int tryIndex) {
		itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1).toLowerCase();
		int intTryIndex = 0;
		for (String key : itemsJson.keySet()) {
			JsonElement e = itemsJson.get(key);
			if (e.isJsonObject()) {
				if (((JsonObject) e).get("name").getAsString().equals(itemName)) {
					if(intTryIndex++ >= tryIndex)
						return Integer.valueOf(key);
				}
			}
		}
		if(tryIndex == 0)
			throw new IllegalArgumentException("Item name " + itemName + " could not be found in the database!");
		else
			throw new IllegalArgumentException("Item name " + itemName + " could not be found with try index "+tryIndex);
	}

	static int getItemGEPrice(String itemName, int curVal, int tryIndex) {
		//Use the GE API to retrieve the item's current GE Price. http://runescape.wikia.com/wiki/Application_programming_interface
		int itemId = getItemIdFromName(itemName, tryIndex);

		try {
			URL url = new URL("http://services.runescape.com/m=itemdb_rs/api/graph/" + String.valueOf(itemId) + ".json");
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			JsonObject inJson = new Gson().fromJson(br, JsonObject.class);
			JsonElement e = inJson.get("average");
			if (e.isJsonObject()) {
				JsonObject daily = (JsonObject) e;
				Object[] keys = daily.keySet().toArray();
				return daily.get((String) keys[keys.length - 1]).getAsInt();
			}
		} catch (FileNotFoundException e){
			return getItemGEPrice(itemName, curVal, ++tryIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return curVal;
	}
}
