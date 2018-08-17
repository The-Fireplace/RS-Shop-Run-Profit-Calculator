package the_fireplace.shoprun.gui;

import the_fireplace.shoprun.*;
import the_fireplace.shoprun.render.SellingEntryRenderer;
import the_fireplace.shoprun.render.ViewLocationDataRenderer;
import the_fireplace.shoprun.tools.Lib;
import the_fireplace.shoprun.tools.Pair;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;

public class GuiViewData extends JPanel {
	private JButton exitB;
	private JLabel initPriceL, gePriceL, storeStockL, sellSpeedL, stackableL, profitL, profitMarginL, profitStoreL, initPriceTF, gePriceTF, storeStockTF, profitTF, profitMarginTF, profitStoreTF;
	public JList<Integer> entries;
	private JList<LocationData> locations;
	private JScrollPane entriesSc, locationsSc;
	private JComboBox<String> sortS, locationF, sellSpeedF, stackableF;
	private static final String[] sorts = new String[]{"Default Profit/Store/Run (H->L)", "Default Profit/Store (H->L)", "Default Profit Margin (H->L)", "Default Profit/Item (H->L)", "Default Initial Price (L->H)"};
	private String[] locationModelList, sellSpeedModelList;
	private static final String[] stackableModelList = new String[]{"Filter by stackability", "Stackable", "Not Stackable"};

	//ID, Pair(Shop Price, Shop Stock), List of associated data
	public static HashMap<Integer, HashMap<Pair<Integer, Integer>, ArrayList<LocationData>>> locationGroups = new HashMap<>();

	public GuiViewData() {
		exitB = new JButton("Exit");
		exitB.addActionListener(new CancelButtonHandler());
		initPriceTF = new JLabel();
		gePriceTF = new JLabel();
		storeStockTF = new JLabel();
		profitTF = new JLabel();
		profitMarginTF = new JLabel();
		profitStoreTF = new JLabel();
		initPriceTF.setLayout(new BoxLayout(initPriceTF, BoxLayout.Y_AXIS));
		storeStockTF.setLayout(new BoxLayout(storeStockTF, BoxLayout.Y_AXIS));
		profitMarginTF.setLayout(new BoxLayout(profitMarginTF, BoxLayout.Y_AXIS));
		profitTF.setLayout(new BoxLayout(profitTF, BoxLayout.Y_AXIS));
		profitStoreTF.setLayout(new BoxLayout(profitStoreTF, BoxLayout.Y_AXIS));
		initPriceL = new JLabel("Shop Price:", SwingConstants.CENTER);
		gePriceL = new JLabel("GE Sell Price:", SwingConstants.CENTER);
		storeStockL = new JLabel("Amount per Store:", SwingConstants.CENTER);
		sellSpeedL = new JLabel("GE Sell Speed:", SwingConstants.CENTER);
		stackableL = new JLabel("Stackable: ", SwingConstants.CENTER);
		profitL = new JLabel("Profit/Item: ", SwingConstants.CENTER);
		profitMarginL = new JLabel("Profit Margin: ", SwingConstants.CENTER);
		profitStoreL = new JLabel("Profit/Store: ", SwingConstants.CENTER);
		locations = new JList<>();
		locations.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {
				//Do nothing
			}
		});
		locations.setLayoutOrientation(JList.VERTICAL_WRAP);
		locations.setVisibleRowCount(0);
		locations.setCellRenderer(new ViewLocationDataRenderer(this));
		locationsSc = new JScrollPane(locations);
		locationsSc.setPreferredSize(new Dimension(ShopRunData.DEFAULT_WIDTH / 5 * 2, ShopRunData.DEFAULT_HEIGHT / 5));
		entries = new JList<>(Database.getIdentifiers());
		entries.addListSelectionListener(new EntrySelectionListener());
		entries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entries.setCellRenderer(new SellingEntryRenderer());
		entriesSc = new JScrollPane(entries);
		SortChangeListener onValueChange = new SortChangeListener();
		sortS = new JComboBox<>(sorts);
		sortS.addActionListener(onValueChange);
		ArrayList<String> allLocations = Lib.getAllLocations();
		locationModelList = new String[allLocations.size() + 1];
		locationModelList[0] = "Filter by Location";
		//noinspection SuspiciousSystemArraycopy
		System.arraycopy(allLocations.toArray(), 0, locationModelList, 1, allLocations.size());
		locationF = new JComboBox<>(locationModelList);
		locationF.addActionListener(onValueChange);
		String[] allSellSpeeds = Database.getAllSellSpeeds();
		sellSpeedModelList = new String[allSellSpeeds.length + 1];
		sellSpeedModelList[0] = "Filter by Sell Speed";
		System.arraycopy(allSellSpeeds, 0, sellSpeedModelList, 1, allSellSpeeds.length);
		sellSpeedF = new JComboBox<>(sellSpeedModelList);
		sellSpeedF.addActionListener(onValueChange);
		stackableF = new JComboBox<>(stackableModelList);
		stackableF.addActionListener(onValueChange);

		setLayout(new GridBagLayout());//5x5
		//Row 0
		add(entriesSc, 0, 0, 1, 4, 0.5, 0.25);
		add(initPriceL, 1, 0, 1, 1);
		add(initPriceTF, 2, 0, 1, 1, 0.05, 0.5);
		add(gePriceL, 3, 0, 1, 1, 0.15, 0.5);
		add(gePriceTF, 4, 0, 1, 1, 1.0, 0.5);
		//Row 1
		add(profitL, 1, 1, 1, 1);
		add(profitTF, 2, 1, 1, 1, 0.05, 0.5);
		add(profitMarginL, 3, 1, 1, 1, 0.15, 0.5);
		add(profitMarginTF, 4, 1, 1, 1, 1.0, 0.5);
		//Row 2
		add(storeStockL, 1, 2, 1, 1);
		add(storeStockTF, 2, 2, 1, 1, 0.05, 0.5);
		add(sellSpeedL, 3, 2, 1, 1, 0.15, 0.5);
		add(stackableL, 4, 2, 1, 1, 0.75, 0.5);
		//Row 3
		add(profitStoreL, 1, 3, 1, 1);
		add(profitStoreTF, 2, 3, 1, 1, 0.05, 0.01);
		add(locationsSc, 3, 3, 2, 1, 0.3, 0.01);
		//Row 4
		add(sortS, 0, 4, 1, 1);
		add(sellSpeedF, 1, 4, 1, 1);
		add(stackableF, 2, 4, 1, 1, 0.05, 0.5);
		add(locationF, 3, 4, 1, 1, 0.15, 0.5);
		add(exitB, 4, 4, 1, 1, 1.0, 0.5);

		sortEntries();
		addComponentListener(new VisibilityAdapter());
	}

	private void add(JComponent comp, int posX, int posY, int w, int h) {
		add(comp, posX, posY, w, h, 0.5);
	}

	private void add(JComponent comp, int posX, int posY, int w, int h, double weight) {
		add(comp, posX, posY, w, h, weight, weight);
	}

	private void add(JComponent comp, int posX, int posY, int w, int h, double weightX, double weightY) {
		GridBagConstraints bag = new GridBagConstraints();

		bag.gridx = posX;
		bag.gridy = posY;
		bag.gridwidth = w;
		bag.gridheight = h;
		bag.weightx = weightX;
		bag.weighty = weightY;
		bag.insets = new Insets(2, 2, 2, 2);
		bag.anchor = GridBagConstraints.CENTER;
		bag.fill = GridBagConstraints.BOTH;

		add(comp, bag);
	}

	private void sortEntries() {
		Integer[] allIdents = Database.getIdentifiers();
		LinkedList<Integer> idents = new LinkedList<>(Arrays.asList(allIdents));
		//Filter first, then sort
		idents.removeIf(identifier -> {
			if (!locationF.getSelectedItem().equals("Filter by Location")) {
				boolean hasLoc = false;
				for (LocationData loc : Database.getLocations(identifier))
					if (loc.locationName.equals(locationF.getSelectedItem())) {
						hasLoc = true;
						break;
					}
				if (!hasLoc)
					return true;
			}
			if (!sellSpeedF.getSelectedItem().equals("Filter by Sell Speed"))
				if (!Database.getItemSellSpeed(identifier).equals(sellSpeedF.getSelectedItem()))
					return true;
			switch ((String) stackableF.getSelectedItem()) {
				case "Stackable":
					if (!Database.getStackable(identifier))
						return true;
					break;
				case "Not Stackable":
					if (Database.getStackable(identifier))
						return true;
			}
			return false;
		});

		idents.sort((o1, o2) -> {//TODO: Rework all of these options to take location in to account, rather than only working for defaults. Add Max, Min, and Average.
			int ret = 0;
			switch ((String) sortS.getSelectedItem()) {
				case "Default Profit/Store/Run (H->L)":
					int psr1 = Database.getDefaultProfitPerStorePerRun(o1);
					int psr2 = Database.getDefaultProfitPerStorePerRun(o2);
					if (psr1 < psr2)
						ret = 1;
					else if (psr1 > psr2)
						ret = -1;
					return ret;
				case "Default Profit/Store (H->L)":
					int ps1 = Database.getDefaultProfitPerStore(o1);
					int ps2 = Database.getDefaultProfitPerStore(o2);
					if (ps1 < ps2)
						ret = 1;
					else if (ps1 > ps2)
						ret = -1;
					return ret;
				case "Default Profit Margin (H->L)":
					float pm1 = Database.getDefaultProfitMarginPercent(o1);
					float pm2 = Database.getDefaultProfitMarginPercent(o2);
					if (pm1 < pm2)
						ret = 1;
					else if (pm1 > pm2)
						ret = -1;
					return ret;
				case "Default Profit/Item (H->L)":
					float pi1 = Database.getDefaultProfitPerItem(o1);
					float pi2 = Database.getDefaultProfitPerItem(o2);
					if (pi1 < pi2)
						ret = 1;
					else if (pi1 > pi2)
						ret = -1;
					return ret;
				case "Default Initial Price (L->H)":
					float ip1 = Database.getDefaultInitialPrice(o1);
					float ip2 = Database.getDefaultInitialPrice(o2);
					if (ip1 > ip2)
						ret = 1;
					else if (ip1 < ip2)
						ret = -1;
					return ret;
			}
			return ret;
		});

		Integer[] listValues = new Integer[idents.size()];
		for (int i = 0; i < idents.size(); i++)
			listValues[i] = idents.get(i);
		entries.setListData(listValues);
	}

	public class CancelButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Return to the previous screen
			clear();
			ShopRunData.actionSelectScreen();
		}
	}

	private void clear() {
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedL.setText("GE Sell Speed: ");
		stackableL.setText("Stackable: ");
		profitTF.setText("");
		profitMarginTF.setText("");
		profitStoreTF.setText("");
		profitStoreL.setText("Profit/Store: ");
		locations.clearSelection();
	}

	private class VisibilityAdapter extends ComponentAdapter {
		@Override
		public void componentShown(ComponentEvent e) {
			entries.setListData(Database.getIdentifiers());
			entries.setEnabled(true);
			sortEntries();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			entries.clearSelection();
			clear();
		}
	}

	private class EntrySelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			Integer viewingIdent = entries.getSelectedValue();
			if (viewingIdent == null)
				clear();
			else {
				LocationData[] locs = Database.getLocations(viewingIdent);
				locations.setListData(locs);
				//Group the location data by price and store stock. This rebuilds every time the item is viewed because locations could have been changed.
				locationGroups.put(viewingIdent, new HashMap<>());
				for (Pair<Integer, Integer> locPriceDat : Database.getInitialPricesAndStoreStocks(viewingIdent))
					locationGroups.get(viewingIdent).put(locPriceDat, Database.locationsWithPair(viewingIdent, locs, locPriceDat));
				//Values that don't change by location
				gePriceTF.setText(String.valueOf(Database.getGEPrice(viewingIdent)));
				stackableL.setText("Stackable: " + (Database.getStackable(viewingIdent) ? "Yes" : "No"));
				sellSpeedL.setText("GE Sell Speed: " + Database.getItemSellSpeed(viewingIdent));

				HashMap<Pair<Integer, Integer>, ArrayList<LocationData>> pairMap = GuiViewData.locationGroups.get(entries.getSelectedValue());
				Object[] pairs = pairMap.keySet().toArray();
				//Values that can change by location
				//Clear the old data
				initPriceTF.removeAll();
				initPriceTF.updateUI();
				storeStockTF.removeAll();
				storeStockTF.updateUI();
				profitMarginTF.removeAll();
				profitMarginTF.updateUI();
				profitTF.removeAll();
				profitTF.updateUI();
				profitStoreTF.removeAll();
				profitStoreTF.updateUI();
				//Add the data
				int defaultProfitStore = Database.getDefaultProfitPerStore(viewingIdent);
				int defaultProfitStoreRun = Database.getDefaultProfitPerStorePerRun(viewingIdent);
				for(Pair<Integer, Integer> pair:locationGroups.get(viewingIdent).keySet()) {
					JLabel initPrice = new JLabel(String.valueOf(Database.getInitialPrice(viewingIdent, pair)), SwingConstants.CENTER);
					for(int i=0;i<pairs.length;i++)
						if(pairs[i].equals(pair))
							initPrice.setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
					initPriceTF.add(initPrice);
					int amountPerStore = Database.getAmountPerStore(viewingIdent, pair);
					JLabel storeStock = new JLabel(String.valueOf(amountPerStore), SwingConstants.CENTER);
					for(int i=0;i<pairs.length;i++)
						if(pairs[i].equals(pair))
							storeStock.setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
					storeStockTF.add(storeStock);
					JLabel profitMargin = new JLabel(String.valueOf(Database.getProfitMarginPercent(viewingIdent, pair)) + "%", SwingConstants.CENTER);
					for(int i=0;i<pairs.length;i++)
						if(pairs[i].equals(pair))
							profitMargin.setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
					profitMarginTF.add(profitMargin);
					JLabel profitItem = new JLabel(String.valueOf(Database.getProfitPerItem(viewingIdent, pair)), SwingConstants.CENTER);
					for(int i=0;i<pairs.length;i++)
						if(pairs[i].equals(pair))
							profitItem.setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
					profitTF.add(profitItem);
					JLabel profitStoreLabel;
					int profitStore = Database.getProfitPerStore(viewingIdent, pair);
					int profitStoreRun = Database.getProfitPerStorePerRun(viewingIdent, pair);
					if (profitStore != profitStoreRun)
						profitStoreLabel = new JLabel("<html>" + String.valueOf(profitStoreRun) + "<br />" + String.valueOf(profitStore) + "</html>");
					else
						profitStoreLabel = new JLabel(String.valueOf(profitStore));
					for(int i=0;i<pairs.length;i++)
						if(pairs[i].equals(pair))
							profitStoreLabel.setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
					profitStoreTF.add(profitStoreLabel);
				}
				if (defaultProfitStore != defaultProfitStoreRun)
					profitStoreL.setText("<html>Profit/Store/Run: <br /><i>Total Profit/Store: </i></html>");
				else
					profitStoreL.setText("Profit/Store: ");
			}
		}
	}

	private class SortChangeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			sortEntries();
		}
	}
}
