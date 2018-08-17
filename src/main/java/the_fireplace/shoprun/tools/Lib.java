package the_fireplace.shoprun.tools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import the_fireplace.shoprun.Database;
import the_fireplace.shoprun.LocationData;
import the_fireplace.shoprun.ShopRunData;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Lib {
	public static final ArrayList<String> F2P_locations = new ArrayList<>();
	public static final ArrayList<String> P2P_locations = new ArrayList<>();

	static {
		Collections.addAll(F2P_locations, "Al Kharid", "Barbarian Village/Gunnarsgrunn", "Burthorpe/Taverley", "Draynor Village", "Dwarven Mines", "Edgeville", "Falador", "Lumbridge", "Port Sarim", "Rimmington", "Varrock", "Wilderness Bandit Camp");
		Collections.addAll(P2P_locations, "Ape Atoll", "Ardougne", "Brimhaven", "Burgh de Rott", "Canifis", "Catherby", "Darkmeyer", "Desert Bandit Camp", "Dorgesh-Kaan", "Gu'Tanoth", "Jatizso", "Keldagrim", "Lighthouse", "Lletya", "Lunar Isle", "Meiyerditch", "Miscellania", "Mort'ton", "Nardah", "Neitiznot", "Oo'glog", "Piscatoris Fishing Colony", "Pollnivneach", "Port Khazard", "Port Phasmatys", "Prifddinas", "Rellekka", "Seers' Village", "Shantay Pass", "Sophanem", "Shilo Village", "Tai Bwo Wannai", "Tree Gnome Stronghold", "Tree Gnome Village", "Tyras Camp", "TzHaar City", "Void Knights' Outpost", "Yanille", "Zanaris");
	}

	public static ArrayList<String> getAllLocations() {
		ArrayList<String> allLocs = new ArrayList<>();
		allLocs.addAll(F2P_locations);
		allLocs.addAll(P2P_locations);
		return allLocs;
	}

	static boolean isInteger(String text) {
		try {
			int textInt = Integer.parseInt(text);
			return textInt >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
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

	public static void loadItemIDs() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("items_rs3.json")));

			itemsJson = new Gson().fromJson(br, JsonObject.class);
		} catch (Exception e) {
			ShopRunData.LOGGER.severe("Error reading item ID database!");
			e.printStackTrace();
		}
	}

	public static ArrayList<Integer> getPotentialItemIdents(String itemName) {
		itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1).toLowerCase();
		if(Database.getAllIdentsForItem().containsKey(itemName)) {
			ArrayList<Integer> outputIdents = new ArrayList<>(Database.getAllIdentsForItem().get(itemName));
			//We only want unused identifiers
			outputIdents.removeIf(ident -> !Database.isValidIdentifier(ident));
			return outputIdents;
		} else {
			ArrayList<Integer> potentialItemIdents = new ArrayList<>();
			for (String key : itemsJson.keySet()) {
				JsonElement e = itemsJson.get(key);
				if (e.isJsonObject())
					if (((JsonObject) e).get("name").getAsString().equals(itemName))
						potentialItemIdents.add(Integer.valueOf(key));
			}
			potentialItemIdents.removeIf(ident -> {
				while(true)//Loop so we can have it try again if the API Feeds us garbage
					try {
						URL url = new URL("http://services.runescape.com/m=itemdb_rs/api/graph/" + String.valueOf(ident) + ".json");
						InputStream is = url.openStream();
						//Check to make sure RS API isn't feeding us garbage
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(is));
							JsonObject inJson = new Gson().fromJson(br, JsonObject.class);
							JsonElement e = inJson.get("average");
							if (e.isJsonObject())
								return false;
						}catch (Exception e) {
							//Wait and try again
							Thread.sleep(5001);
						}
					} catch (FileNotFoundException e) {
						return true;
					} catch (Exception e) {
						ShopRunData.LOGGER.warning("Error attempting to read data for " + ident + ':');
						e.printStackTrace();
						return true;
					}
			});
			Database.addPossibleItemIdents(itemName, potentialItemIdents);
			return getPotentialItemIdents(itemName);
		}
	}

	public static int getItemGEPrice(int itemId, int curVal) {
		//Use the GE API to retrieve the item's current GE Price. http://runescape.wikia.com/wiki/Application_programming_interface
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
		} catch (FileNotFoundException e) {
			ShopRunData.LOGGER.severe("GE Price Data not found for ID " + itemId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return curVal;
	}

	public static LocationData createLocationDataDialog() {
		JComboBox locations = new JComboBox<>(Lib.getAllLocations().toArray());
		JTextField bankRoundTripTime = new JTextField("Unknown");
		JTextField restockTime = new JTextField("Unknown");
		JTextField itemPrice = new JTextField("Default");
		JTextField storeStock = new JTextField("Default");

		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new GridLayout(0, 2));
		dataPanel.add(new JLabel("Location:"));
		dataPanel.add(locations);
		dataPanel.add(new JLabel("<html><i>Enter just the number, or it will be unknown.</i><br />Avg. Bank Round Trip(in seconds):</html>"));
		dataPanel.add(bankRoundTripTime);
		dataPanel.add(new JLabel("<html><i>Enter just the number, or it will be unknown.</i><br />Avg. Restock Time(in minutes):</html>"));
		dataPanel.add(restockTime);
		dataPanel.add(new JLabel("<html><i>Enter just the number, or it will be default.</i><br />Item Price:</html>"));
		dataPanel.add(itemPrice);
		dataPanel.add(new JLabel("<html><i>Enter just the number, or it will be default.</i><br />Store Stock:</html>"));
		dataPanel.add(storeStock);

		int result = JOptionPane.showConfirmDialog(null, dataPanel, "Enter the information for this location.", JOptionPane.OK_CANCEL_OPTION);
		return result == JOptionPane.OK_OPTION ? new LocationData((String) locations.getSelectedItem(), Lib.isInteger(itemPrice.getText()) ? Integer.parseInt(itemPrice.getText()) : -1, Lib.isInteger(storeStock.getText()) ? Integer.parseInt(storeStock.getText()) : -1, Lib.isInteger(bankRoundTripTime.getText()) ? Integer.parseInt(bankRoundTripTime.getText()) : -1, Lib.isInteger(restockTime.getText()) ? Integer.parseInt(restockTime.getText()) : -1) : null;
	}
}
