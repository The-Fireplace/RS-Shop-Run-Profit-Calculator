package the_fireplace.shoprun.gui;

import the_fireplace.shoprun.*;
import the_fireplace.shoprun.render.IDListRenderer;
import the_fireplace.shoprun.render.LocationDataRenderer;
import the_fireplace.shoprun.tools.EnumDataKey;
import the_fireplace.shoprun.tools.Lib;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class GuiEditData extends JPanel {
	private JButton saveExitB, exitB, addB, delB, gePriceB, addLocB, delLocB;
	private JLabel identBoxL, initPriceL, gePriceL, storeStockL, sellSpeedL;
	private JTextField initPriceTF, gePriceTF, storeStockTF, sellSpeedTF;
	private JCheckBox stackableCB;
	private JList<Integer> entries;
	private JList<LocationData> locations;
	private JScrollPane entriesSc, locationsSc;
	private JComboBox<String> sortS;
	private JComboBox identBox;
	private static final String[] sorts = new String[]{"Default Profit/Store/Run (H->L)", "Default Profit/Store (H->L)", "Default Profit Margin (H->L)", "Default Profit/Item (H->L)", "Default Initial Price (L->H)"};

	private EnableEditorListener checkEditorEnabled;

	private ArrayList<Integer> potentialIdents;
	private String itemName = "";

	public GuiEditData() {
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
		addLocB = new JButton("Add Location");
		addLocB.addActionListener(new AddLocButtonHandler());
		delLocB = new JButton("Delete Selected Location");
		delLocB.addActionListener(new DelLocButtonHandler());
		delLocB.setEnabled(false);
		identBox = new JComboBox<>();
		identBox.addActionListener(canAddToDatabase);
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
		identBoxL = new JLabel("<html>Item ID:<br /><i>If this has multiple options,<br />check the GE Price of each<br />and choose the correct one.</i></html>", SwingConstants.CENTER);
		initPriceL = new JLabel("Default Shop Price:", SwingConstants.CENTER);
		gePriceL = new JLabel("GE Sell Price:", SwingConstants.CENTER);
		storeStockL = new JLabel("Default Amount per Store:", SwingConstants.CENTER);
		sellSpeedL = new JLabel("GE Sell Speed:", SwingConstants.CENTER);
		stackableCB = new JCheckBox("Is the Item Stackable?");
		locations = new JList<>();
		locations.setLayoutOrientation(JList.VERTICAL_WRAP);
		locations.setVisibleRowCount(0);
		locations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locations.addListSelectionListener(new LocationSelectListener());
		locations.setCellRenderer(new LocationDataRenderer());
		locationsSc = new JScrollPane(locations);
		locationsSc.setPreferredSize(new Dimension(ShopRunData.DEFAULT_WIDTH / 5 * 2, ShopRunData.DEFAULT_HEIGHT / 5));
		sortS = new JComboBox<>(sorts);
		sortS.addActionListener(new SortChangeListener());
		gePriceB = new JButton("Get GE Price");
		gePriceB.addActionListener(new GePriceHandler());

		entries = new JList<>(Database.getIdentifiers());
		entries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entries.addListSelectionListener(checkEditorEnabled);
		entries.setCellRenderer(new IDListRenderer());
		entriesSc = new JScrollPane(entries);

		setLayout(new GridBagLayout());//5x5
		//Row 0
		add(entriesSc, 0, 0, 1, 4, 0.5, 0.25);
		add(identBoxL, 1, 0, 1, 1);
		add(identBox, 2, 0, 1, 1, 0.05, 0.5);
		add(gePriceL, 3, 0, 1, 1, 0.15, 0.5);
		add(gePriceTF, 4, 0, 1, 1, 1.0, 0.5);
		//Row 1
		add(initPriceL, 1, 1, 1, 1);
		add(initPriceTF, 2, 1, 1, 1, 0.05, 0.5);
		add(sellSpeedL, 3, 1, 1, 1, 0.15, 0.5);
		add(sellSpeedTF, 4, 1, 1, 1, 1.0, 0.5);
		//Row 2
		add(storeStockL, 1, 2, 1, 1);
		add(storeStockTF, 2, 2, 1, 1, 0.05, 0.5);
		add(locationsSc, 3, 2, 2, 1, 0.15, 0.5);
		//Row 3
		add(gePriceB, 1, 3, 1, 1);
		add(stackableCB, 2, 3, 1, 1, 0.05, 0.01);
		add(addLocB, 3, 3, 1, 1, 0.3, 0.01);
		add(delLocB, 4, 3, 1, 1, 0.3, 0.01);
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
			LocationData[] locs = new LocationData[locations.getSelectedValuesList().size()];
			int index = 0;
			for (LocationData loc : locations.getSelectedValuesList())
				locs[index++] = loc;
			Database.addNewItemData((int) identBox.getSelectedItem(), itemName, Integer.parseInt(initPriceTF.getText()), Integer.parseInt(gePriceTF.getText()), Integer.parseInt(storeStockTF.getText()), stackableCB.getModel().isSelected(), sellSpeedTF.getText(), locs);
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
			if (identBox.getSelectedItem() != null)
				gePriceTF.setText(String.valueOf(Lib.getItemGEPrice((int) identBox.getSelectedItem(), gePriceTF.getText().isEmpty() ? -1 : Integer.valueOf(gePriceTF.getText()))));
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
			Database.deleteData((int) identBox.getSelectedItem());
			clear();
			checkEditorEnabled.disableEditor();
			entries.setListData(Database.getIdentifiers());
			entries.setEnabled(true);
			sortEntries();
		}
	}

	public class AddLocButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			LocationData newData = Lib.createLocationDataDialog();
			if (newData != null) {
				LocationData[] newListData = new LocationData[locations.getModel().getSize() + 1];
				for (int i = 0; i < newListData.length - 1; i++)
					newListData[i] = locations.getModel().getElementAt(i);
				newListData[newListData.length - 1] = newData;
				locations.setListData(newListData);
			}
		}
	}

	public class DelLocButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (locations.getSelectedIndex() != -1) {
				LocationData[] newListData = new LocationData[locations.getModel().getSize() - 1];
				boolean skipped = false;
				for (int i = 0; i < newListData.length + 1; i++)
					if (i != locations.getSelectedIndex())
						newListData[skipped ? i - 1 : i] = locations.getModel().getElementAt(i);
					else
						skipped = true;
				locations.setListData(newListData);
			}
		}
	}

	private void clear() {
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedTF.setText("");
		stackableCB.getModel().setSelected(false);
		locations.clearSelection();
		locations.setListData(new LocationData[]{});
		identBox.setModel(new DefaultComboBoxModel<>());
		delLocB.setEnabled(false);
	}

	private void saveCurrentData() {
		LocationData[] locs = new LocationData[locations.getModel().getSize()];
		for (int i = 0; i < locs.length; i++)
			locs[i] = locations.getModel().getElementAt(i);
		int identifier = (int) identBox.getSelectedItem();
		if (identifier != entries.getSelectedValue()) {
			Database.deleteData(entries.getSelectedValue());
			Database.addNewItemData(identifier, itemName, Integer.parseInt(initPriceTF.getText()), Integer.parseInt(gePriceTF.getText()), Integer.parseInt(storeStockTF.getText()), stackableCB.getModel().isSelected(), sellSpeedTF.getText(), locs);
		} else {
			Database.setData(identifier, EnumDataKey.DEFAULT_INITIAL_PRICE, Integer.parseInt(initPriceTF.getText()));
			Database.setData(identifier, EnumDataKey.GE_PRICE, Integer.parseInt(gePriceTF.getText()));
			Database.setData(identifier, EnumDataKey.DEFAULT_NUMBER_PER_STORE, Integer.parseInt(storeStockTF.getText()));
			Database.setData(identifier, EnumDataKey.STACKABLE, stackableCB.getModel().isSelected());
			Database.setData(identifier, EnumDataKey.SELL_SPEED, sellSpeedTF.getText());
			Database.setData(identifier, EnumDataKey.LOCATION_DATA, locs);
		}
		Database.save();
	}

	private void checkButton() {
		LocationData[] locs = new LocationData[locations.getModel().getSize()];
		for (int i = 0; i < locs.length; i++)
			locs[i] = locations.getModel().getElementAt(i);
		boolean validData = Database.isValidData(itemName, Integer.parseInt(initPriceTF.getText().isEmpty() ? "-1" : initPriceTF.getText()), Integer.parseInt(gePriceTF.getText().isEmpty() ? "-1" : gePriceTF.getText()), Integer.parseInt(storeStockTF.getText().isEmpty() ? "-1" : storeStockTF.getText()), locs);
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
	}

	private class CanAddDataListener implements DocumentListener, ActionListener {
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
		public void actionPerformed(ActionEvent e) {
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
			Integer editingIdent = entries.getSelectedValue();
			if (editingIdent == null)
				disableEditor();
			else {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (!editorEnabled)
					enableEditor();
				else
					saveCurrentData();
				itemName = Database.getItemName(editingIdent);
				initPriceTF.setText(String.valueOf(Database.getDefaultInitialPrice(editingIdent)));
				gePriceTF.setText(String.valueOf(Database.getGEPrice(editingIdent)));
				storeStockTF.setText(String.valueOf(Database.getDefaultAmountPerStore(editingIdent)));
				stackableCB.getModel().setSelected(Database.getStackable(editingIdent));
				sellSpeedTF.setText(Database.getItemSellSpeed(editingIdent));
				locations.setListData(Database.getLocations(editingIdent));

				potentialIdents = new ArrayList<>();
				potentialIdents.add(entries.getSelectedValue());
				potentialIdents.addAll(Lib.getPotentialItemIdents(itemName));
				ShopRunData.LOGGER.fine("Possible IDs for "+itemName+": "+potentialIdents.toString());
				identBox.setModel(new DefaultComboBoxModel<>(potentialIdents.toArray()));
				identBox.setEditable(potentialIdents.size() > 1);
				checkButton();
				setCursor(Cursor.getDefaultCursor());
			}
		}

		void enableEditor() {
			identBox.setEnabled(true);
			initPriceTF.setEnabled(true);
			gePriceTF.setEnabled(true);
			storeStockTF.setEnabled(true);
			sellSpeedTF.setEnabled(true);
			stackableCB.setEnabled(true);
			locations.setEnabled(true);
			delB.setEnabled(true);
			addLocB.setEnabled(true);
			editorEnabled = true;
		}

		void disableEditor() {
			identBox.setEnabled(false);
			itemName = "";
			initPriceTF.setEnabled(false);
			gePriceTF.setEnabled(false);
			storeStockTF.setEnabled(false);
			sellSpeedTF.setEnabled(false);
			stackableCB.setEnabled(false);
			locations.setEnabled(false);
			delB.setEnabled(false);
			addLocB.setEnabled(false);
			delLocB.setEnabled(false);
			editorEnabled = false;
			clear();
		}
	}

	public class LocationSelectListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			delLocB.setEnabled(locations.getSelectedValue() != null);
		}
	}

	private void sortEntries() {
		entries.clearSelection();
		Integer[] allIdents = Database.getIdentifiers();
		LinkedList<Integer> idents = new LinkedList<>(Arrays.asList(allIdents));

		idents.sort((o1, o2) -> {
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
