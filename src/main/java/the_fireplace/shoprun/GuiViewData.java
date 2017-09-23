package the_fireplace.shoprun;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

class GuiViewData extends JPanel {
	private JButton exitB;
	private JLabel itemNameL, initPriceL, gePriceL, storeStockL, sellSpeedL, stackableL, profitL, profitMarginL, profitStoreL;
	private JTextField itemNameTF, initPriceTF, gePriceTF, storeStockTF, sellSpeedTF;//TODO: These can all be turned into Labels, using icons to distinguish between sets of locations.
	private JList<Integer> entries;
	private JList<LocationData> locations;
	private JScrollPane entriesSc, locationsSc;
	private JComboBox<String> sortS, locationF, sellSpeedF, stackableF;
	private static final String[] sorts = new String[]{"Profit/Store/Run (H->L)", "Profit/Store (H->L)", "Profit Margin (H->L)", "Profit/Item (H->L)", "Initial Price (L->H)", "Identifier (A->Z)", "Identifier (Z->A)"};
	private String[] locationModelList, sellSpeedModelList;
	private static final String[] stackableModelList = new String[]{"Filter by stackability", "Stackable", "Not Stackable"};

	GuiViewData() {
		exitB = new JButton("Exit");
		exitB.addActionListener(new CancelButtonHandler());
		itemNameTF = new JTextField();
		itemNameTF.setEditable(false);
		initPriceTF = new JTextField();
		initPriceTF.setEditable(false);
		gePriceTF = new JTextField();
		gePriceTF.setEditable(false);
		storeStockTF = new JTextField();
		storeStockTF.setEditable(false);
		sellSpeedTF = new JTextField();
		sellSpeedTF.setEditable(false);
		itemNameL = new JLabel("Item Name:", SwingConstants.CENTER);
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
		add(itemNameL, 1, 0, 1, 1);
		add(itemNameTF, 2, 0, 1, 1, 0.05, 0.5);
		add(initPriceL, 3, 0, 1, 1, 0.15, 0.5);
		add(initPriceTF, 4, 0, 1, 1, 1.0, 0.5);
		//Row 1
		add(profitL, 1, 1, 1, 1);
		add(profitStoreL, 2, 1, 1, 1, 0.05, 0.5);
		add(gePriceL, 3, 1, 1, 1, 0.15, 0.5);
		add(gePriceTF, 4, 1, 1, 1, 1.0, 0.5);
		//Row 2
		add(storeStockL, 1, 2, 1, 1);
		add(storeStockTF, 2, 2, 1, 1, 0.05, 0.5);
		add(sellSpeedL, 3, 2, 1, 1, 0.15, 0.5);
		add(sellSpeedTF, 4, 2, 1, 1, 0.75, 0.5);
		//Row 3
		add(profitMarginL, 1, 3, 1, 1);
		add(stackableL, 2, 3, 1, 1, 0.05, 0.01);
		add(locationsSc, 3, 3, 2, 1, 0.3, 0.01);
		//Row 4
		add(sortS, 0, 4, 1, 1);
		add(sellSpeedF, 1, 4, 1, 1);
		add(stackableF, 2, 4, 1, 1, 0.05, 0.5);
		add(locationF, 3, 4, 1, 1, 0.15, 0.5);
		add(exitB, 4, 4, 1, 1, 1.0, 0.5);//TODO: Fill space

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

		idents.sort((o1, o2) -> {//TODO: Rework all of these options to take location in to account
			int ret = 0;
			switch ((String) sortS.getSelectedItem()) {
				case "Profit/Store/Run (H->L)":
					int psr1 = Database.getProfitPerStorePerRun(o1);
					int psr2 = Database.getProfitPerStorePerRun(o2);
					if (psr1 < psr2)
						ret = 1;
					else if (psr1 > psr2)
						ret = -1;
					return ret;
				case "Profit/Store (H->L)":
					int ps1 = Database.getProfitPerStore(o1);
					int ps2 = Database.getProfitPerStore(o2);
					if (ps1 < ps2)
						ret = 1;
					else if (ps1 > ps2)
						ret = -1;
					return ret;
				case "Profit Margin (H->L)":
					float pm1 = Database.getProfitMarginPercent(o1);
					float pm2 = Database.getProfitMarginPercent(o2);
					if (pm1 < pm2)
						ret = 1;
					else if (pm1 > pm2)
						ret = -1;
					return ret;
				case "Profit/Item (H->L)":
					float pi1 = Database.getProfitPerItem(o1);
					float pi2 = Database.getProfitPerItem(o2);
					if (pi1 < pi2)
						ret = 1;
					else if (pi1 > pi2)
						ret = -1;
					return ret;
				case "Initial Price (L->H)":
					float ip1 = Database.getInitialPrice(o1);
					float ip2 = Database.getInitialPrice(o2);
					if (ip1 > ip2)
						ret = 1;
					else if (ip1 < ip2)
						ret = -1;
					return ret;
				case "Identifier (A->Z)":
					return o1.compareTo(o2);
				case "Identifier (Z->A)":
					return o2.compareTo(o1);
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
		itemNameTF.setText("");
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedTF.setText("");
		stackableL.setText("Stackable: ");
		profitL.setText("Profit/Item: ");
		profitMarginL.setText("Profit Margin: ");
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
			clear();
		}
	}

	private class EntrySelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			Integer editingIdent = entries.getSelectedValue();
			if (editingIdent == null)
				clear();
			else {
				itemNameTF.setText(Database.getItemName(editingIdent));
				initPriceTF.setText(String.valueOf(Database.getInitialPrice(editingIdent)));
				gePriceTF.setText(String.valueOf(Database.getGEPrice(editingIdent)));
				storeStockTF.setText(String.valueOf(Database.getAmountPerStore(editingIdent)));
				stackableL.setText("Stackable: " + (Database.getStackable(editingIdent) ? "Yes" : "No"));
				sellSpeedTF.setText(Database.getItemSellSpeed(editingIdent));
				locations.setListData(Database.getLocations(editingIdent));
				profitL.setText("Profit/Item: " + String.valueOf(Database.getProfitPerItem(editingIdent)));
				profitMarginL.setText("Profit Margin: " + String.valueOf(Database.getProfitMarginPercent(editingIdent)) + "%");
				int profitStore = Database.getProfitPerStore(editingIdent);
				int profitStoreRun = Database.getProfitPerStorePerRun(editingIdent);
				if (profitStore != profitStoreRun)
					profitStoreL.setText("<html>Profit/Store/Run: " + String.valueOf(profitStoreRun) + "<br /><i>Total Profit/Store: </i>" + String.valueOf(profitStore) + "</html>");
				else
					profitStoreL.setText("Profit/Store: " + String.valueOf(profitStore));
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
