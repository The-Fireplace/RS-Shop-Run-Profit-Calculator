package the_fireplace.shoprun;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.LinkedList;

class GuiEditData extends JPanel {
	private JButton saveExitB, exitB, addB, delB, gePriceB;
	private JLabel identifierL, itemNameL, initPriceL, gePriceL, storeStockL, sellSpeedL;
	private JTextField identifierTF, itemNameTF, initPriceTF, gePriceTF, storeStockTF, sellSpeedTF;
	private JCheckBox stackableCB;
	private JList<String> entries, locations;
	private JScrollPane entriesSc, locationsSc;
	private JComboBox<String> sortS;
	private static final String[] sorts = new String[]{"Profit/Store/Run (H->L)", "Profit/Store (H->L)", "Profit Margin (H->L)", "Profit/Item (H->L)", "Initial Price (L->H)", "Identifier (A->Z)", "Identifier (Z->A)"};

	private EnableEditorListener checkEditorEnabled;

	GuiEditData() {
		CanAddDataListener canAddToDatabase = new CanAddDataListener();
		checkEditorEnabled = new EnableEditorListener();
		saveExitB = new JButton("Save and Exit");
		saveExitB.addActionListener(new ConfirmButtonHandler());
		saveExitB.setEnabled(false);
		exitB = new JButton("Exit(Cancel editing this entry)");
		exitB.addActionListener(new CancelButtonHandler());
		addB = new JButton("Add Data");
		addB.addActionListener(new AddButtonHandler());
		delB = new JButton("Delete Selected Data");
		delB.addActionListener(new DelButtonHandler());
		identifierTF = new JTextField();
		identifierTF.setEditable(false);
		itemNameTF = new JTextField();
		itemNameTF.getDocument().addDocumentListener(canAddToDatabase);
		DocumentFilter numbersOnly = new Lib.IntegerOnlyFilter();
		initPriceTF = new JTextField();
		initPriceTF.getDocument().addDocumentListener(canAddToDatabase);
		AbstractDocument absDoc;
		Document doc = initPriceTF.getDocument();
		if (doc instanceof AbstractDocument) {
			absDoc = (AbstractDocument) doc;
			absDoc.setDocumentFilter(numbersOnly);
		}
		gePriceTF = new JTextField();
		gePriceTF.getDocument().addDocumentListener(canAddToDatabase);
		doc = gePriceTF.getDocument();
		if (doc instanceof AbstractDocument) {
			absDoc = (AbstractDocument) doc;
			absDoc.setDocumentFilter(numbersOnly);
		}
		storeStockTF = new JTextField();
		storeStockTF.getDocument().addDocumentListener(canAddToDatabase);
		doc = storeStockTF.getDocument();
		if (doc instanceof AbstractDocument) {
			absDoc = (AbstractDocument) doc;
			absDoc.setDocumentFilter(numbersOnly);
		}
		sellSpeedTF = new JTextField();
		sellSpeedTF.getDocument().addDocumentListener(canAddToDatabase);
		identifierL = new JLabel("<html>Data Identifier:<br /><i>Must be unique!</i></html>", SwingConstants.CENTER);
		itemNameL = new JLabel("Item Name:", SwingConstants.CENTER);
		initPriceL = new JLabel("Shop Price:", SwingConstants.CENTER);
		gePriceL = new JLabel("GE Sell Price:", SwingConstants.CENTER);
		storeStockL = new JLabel("Amount per Store:", SwingConstants.CENTER);
		sellSpeedL = new JLabel("GE Sell Speed:", SwingConstants.CENTER);
		stackableCB = new JCheckBox("Is the Item Stackable?");
		locations = new JList<>(Lib.getAllLocations());
		locations.addListSelectionListener(canAddToDatabase);
		locations.setLayoutOrientation(JList.VERTICAL_WRAP);
		locations.setVisibleRowCount(0);
		locations.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if(super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
				}
				else {
					super.addSelectionInterval(index0, index1);
				}
			}
		});
		locationsSc = new JScrollPane(locations);
		locationsSc.setPreferredSize(new Dimension(ShopRunData.DEFAULT_WIDTH/5*2, ShopRunData.DEFAULT_HEIGHT/5));
		sortS = new JComboBox<>(sorts);
		sortS.addActionListener(new SortChangeListener());
		gePriceB = new JButton("Get GE Price");
		gePriceB.addActionListener(new GePriceHandler());

		entries = new JList<>(Database.getIdentifiers());
		entries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entries.addListSelectionListener(checkEditorEnabled);
		entriesSc = new JScrollPane(entries);

		setLayout(new GridBagLayout());//5x5
		//Row 0
		add(entriesSc, 0, 0, 1, 4, 0.5, 0.25);
		add(identifierL, 1, 0, 1, 1);
		add(identifierTF, 2, 0, 1, 1, 0.05, 0.5);
		add(itemNameL, 3, 0, 1, 1, 0.15, 0.5);
		add(itemNameTF, 4, 0, 1, 1, 1.0, 0.5);
		//Row 1
		add(initPriceL, 1, 1, 1, 1);
		add(initPriceTF, 2, 1, 1, 1, 0.05, 0.5);
		add(gePriceL, 3, 1, 1, 1, 0.15, 0.5);
		add(gePriceTF, 4, 1, 1, 1, 1.0, 0.5);
		//Row 2
		add(storeStockL, 1, 2, 1, 1);
		add(storeStockTF, 2, 2, 1, 1, 0.05, 0.5);
		add(sellSpeedL, 3, 2, 1, 1, 0.15, 0.5);
		add(sellSpeedTF, 4, 2, 1, 1, 0.75, 0.5);
		//Row 3
		add(gePriceB, 1, 3, 1, 1);
		add(stackableCB, 2, 3, 1, 1, 0.05, 0.01);
		add(locationsSc, 3, 3, 2, 1, 0.3, 0.01);
		//Row 4
		add(addB, 0, 4, 1, 1);
		add(sortS, 1, 4, 1, 1);
		add(saveExitB, 2, 4, 1, 1, 0.05, 0.5);
		add(exitB, 3, 4, 1, 1, 0.15, 0.5);
		add(delB, 4, 4, 1, 1, 1.0, 0.5);

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

	public class ConfirmButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Save and return to the previous screen
			String[] locs = new String[locations.getSelectedValuesList().size()];
			int index = 0;
			for (String loc : locations.getSelectedValuesList())
				locs[index++] = loc;
			Database.addNewItemData(identifierTF.getText(), itemNameTF.getText(), Integer.parseInt(initPriceTF.getText()), Integer.parseInt(gePriceTF.getText()), Integer.parseInt(storeStockTF.getText()), stackableCB.getModel().isSelected(), sellSpeedTF.getText(), locs);
			Database.save();
			clear();
			ShopRunData.actionSelectScreen();
		}
	}

	public class CancelButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Return to the previous screen
			clear();
			ShopRunData.actionSelectScreen();
		}
	}

	public class GePriceHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!itemNameTF.getText().isEmpty())
				gePriceTF.setText(String.valueOf(Lib.getItemGEPrice(itemNameTF.getText(), gePriceTF.getText().isEmpty() ? -1 : Integer.valueOf(gePriceTF.getText()), 0)));
		}
	}

	public class AddButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (checkEditorEnabled.editorEnabled) {
				saveCurrentData();
				checkEditorEnabled.disableEditor();
				entries.clearSelection();
			}
			ShopRunData.addDataScreen();
		}
	}

	public class DelButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Database.deleteData(identifierTF.getText());
			clear();
			checkEditorEnabled.disableEditor();
			entries.setListData(Database.getIdentifiers());
			entries.setEnabled(true);
			sortEntries();
		}
	}

	private void clear() {
		identifierTF.setText("");
		itemNameTF.setText("");
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedTF.setText("");
		stackableCB.getModel().setSelected(false);
		locations.clearSelection();
	}

	private void saveCurrentData() {
		String[] locs = new String[locations.getSelectedValuesList().size()];
		int index = 0;
		for (String loc : locations.getSelectedValuesList())
			locs[index++] = loc;
		String identifier = identifierTF.getText();
		Database.setData(identifier, EnumDataKey.ITEM_NAME, itemNameTF.getText());
		Database.setData(identifier, EnumDataKey.INITIAL_PRICE, Integer.parseInt(initPriceTF.getText()));
		Database.setData(identifier, EnumDataKey.GE_PRICE, Integer.parseInt(gePriceTF.getText()));
		Database.setData(identifier, EnumDataKey.NUMBER_PER_STORE, Integer.parseInt(storeStockTF.getText()));
		Database.setData(identifier, EnumDataKey.STACKABLE, stackableCB.getModel().isSelected());
		Database.setData(identifier, EnumDataKey.SELL_SPEED, sellSpeedTF.getText());
		Database.setData(identifier, EnumDataKey.LOCATIONS, locs);
		Database.save();
	}

	private void checkButton() {
		String[] locs = new String[locations.getSelectedValuesList().size()];
		int index = 0;
		for (String loc : locations.getSelectedValuesList())
			locs[index++] = loc;
		boolean validData = Database.isValidData(identifierTF.getText(), itemNameTF.getText(), Integer.parseInt(initPriceTF.getText().isEmpty() ? "-1" : initPriceTF.getText()), Integer.parseInt(gePriceTF.getText().isEmpty() ? "-1" : gePriceTF.getText()), Integer.parseInt(storeStockTF.getText().isEmpty() ? "-1" : storeStockTF.getText()), locs);
		if (!saveExitB.isEnabled() && validData)
			saveExitB.setEnabled(true);
		else if (saveExitB.isEnabled() && !validData)
			saveExitB.setEnabled(false);
		if (!entries.isEnabled() && validData)
			entries.setEnabled(true);
		else if (entries.isEnabled() && !validData)
			entries.setEnabled(false);
		if (!addB.isEnabled() && validData)
			addB.setEnabled(true);
		else if (addB.isEnabled() && !validData)
			addB.setEnabled(false);
		if (!gePriceB.isEnabled() && !itemNameTF.getText().isEmpty())
			gePriceB.setEnabled(true);
		else if (gePriceB.isEnabled() && itemNameTF.getText().isEmpty())
			gePriceB.setEnabled(false);
	}

	private class CanAddDataListener implements DocumentListener, ListSelectionListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			checkButton();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkButton();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkButton();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			checkButton();
		}
	}

	private class VisibilityAdapter extends ComponentAdapter {
		@Override
		public void componentShown(ComponentEvent e) {
			entries.setListData(Database.getIdentifiers());

			sortEntries();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			clear();
			checkEditorEnabled.disableEditor();
		}
	}

	private class EnableEditorListener implements ListSelectionListener {
		boolean editorEnabled;

		@Override
		public void valueChanged(ListSelectionEvent e) {
			String editingIdent = entries.getSelectedValue();
			if (editingIdent == null)
				disableEditor();
			else {
				if (!editorEnabled)
					enableEditor();
				else
					saveCurrentData();
				identifierTF.setText(editingIdent);
				itemNameTF.setText(Database.getItemName(editingIdent));
				initPriceTF.setText(String.valueOf(Database.getInitialPrice(editingIdent)));
				gePriceTF.setText(String.valueOf(Database.getGEPrice(editingIdent)));
				storeStockTF.setText(String.valueOf(Database.getAmountPerStore(editingIdent)));
				stackableCB.getModel().setSelected(Database.getStackable(editingIdent));
				sellSpeedTF.setText(Database.getItemSellSpeed(editingIdent));
				String[] selectedLocations = Database.getLocations(editingIdent);
				int[] selectedLocationIndeces = new int[selectedLocations.length];
				int index = 0;
				for (String loc : selectedLocations) {
					for (int i = 0; i < Lib.getAllLocations().length; i++)
						if (Lib.getAllLocations()[i].equals(loc)) {
							selectedLocationIndeces[index++] = i;
							break;
						}
				}
				locations.setSelectedIndices(selectedLocationIndeces);
			}
		}

		void enableEditor() {
			identifierTF.setEnabled(true);
			itemNameTF.setEnabled(true);
			initPriceTF.setEnabled(true);
			gePriceTF.setEnabled(true);
			storeStockTF.setEnabled(true);
			sellSpeedTF.setEnabled(true);
			stackableCB.setEnabled(true);
			locations.setEnabled(true);
			delB.setEnabled(true);
			editorEnabled = true;
		}

		void disableEditor() {
			identifierTF.setEnabled(false);
			itemNameTF.setEnabled(false);
			initPriceTF.setEnabled(false);
			gePriceTF.setEnabled(false);
			storeStockTF.setEnabled(false);
			sellSpeedTF.setEnabled(false);
			stackableCB.setEnabled(false);
			locations.setEnabled(false);
			delB.setEnabled(false);
			editorEnabled = false;
			clear();
		}
	}

	private void sortEntries() {
		entries.clearSelection();
		String[] allIdents = Database.getIdentifiers();
		LinkedList<String> idents = new LinkedList<>(Arrays.asList(allIdents));

		idents.sort((o1, o2) -> {
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

		String[] listValues = new String[idents.size()];
		for (int i = 0; i < idents.size(); i++)
			listValues[i] = idents.get(i);
		entries.setListData(listValues);

		clear();
		checkEditorEnabled.disableEditor();
		entries.setEnabled(true);
		addB.setEnabled(true);
	}

	private class SortChangeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			sortEntries();
		}
	}
}
