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
import java.awt.event.*;
import java.util.ArrayList;

class GuiAddData extends JPanel {
	private JButton addB, exitB, gePriceB, addLocB, delLocB;
	private JLabel itemNameL, initPriceL, gePriceL, storeStockL, sellSpeedL, identBoxL;
	private JTextField itemNameTF, initPriceTF, gePriceTF, storeStockTF, sellSpeedTF;
	private JCheckBox stackableCB;
	private JList<LocationData> locations;
	private JScrollPane locationsSc;
	private JComboBox identBox;

	private ArrayList<Integer> potentialIdents;

	GuiAddData() {
		CanAddDataListener canAddToDatabase = new CanAddDataListener();
		addB = new JButton("Confirm");
		addB.addActionListener(new ConfirmButtonHandler());
		addB.setEnabled(false);
		exitB = new JButton("Cancel");
		exitB.addActionListener(new CancelButtonHandler());
		addLocB = new JButton("Add Location");
		addLocB.addActionListener(new AddLocButtonHandler());
		delLocB = new JButton("Delete Selected Location");
		delLocB.addActionListener(new DelLocButtonHandler());
		delLocB.setEnabled(false);
		itemNameTF = new JTextField();
		itemNameTF.getDocument().addDocumentListener(canAddToDatabase);
		itemNameTF.addFocusListener(new NameUnfocusListener());
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
		sellSpeedTF.setText("Unknown");
		sellSpeedTF.getDocument().addDocumentListener(canAddToDatabase);
		itemNameL = new JLabel("Item Name:", SwingConstants.CENTER);
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
		locationsSc.setPreferredSize(new Dimension(ShopRunData.DEFAULT_WIDTH / 4, ShopRunData.DEFAULT_HEIGHT / 5 * 2));
		gePriceB = new JButton("Get GE Price");
		gePriceB.addActionListener(new GePriceHandler());
		identBoxL = new JLabel("<html>Item ID:<br /><i>If this has multiple options,<br />check the GE Price of each<br />and choose the correct one.</i></html>", SwingConstants.CENTER);
		identBox = new JComboBox<>();
		identBox.addActionListener(canAddToDatabase);

		setLayout(new GridBagLayout());//4x5
		//Row 0
		add(identBoxL, 0, 0, 1, 1);
		add(identBox, 1, 0, 1, 1);
		add(gePriceL, 2, 0, 1, 1, 0.35);
		add(gePriceTF, 3, 0, 1, 1, 0.25);
		//Row 1
		add(itemNameL, 0, 1, 1, 1);
		add(itemNameTF, 1, 1, 1, 1);
		add(sellSpeedL, 2, 1, 1, 1, 0.35);
		add(sellSpeedTF, 3, 1, 1, 1, 0.25);
		//Row 2
		add(initPriceL, 0, 2, 1, 1);
		add(initPriceTF, 1, 2, 1, 1);
		add(gePriceB, 2, 2, 1, 1, 0.35);
		add(locationsSc, 3, 2, 1, 1, 0.25);
		//Row 3
		add(storeStockL, 0, 3, 1, 1);
		add(storeStockTF, 1, 3, 1, 1);
		add(stackableCB, 2, 3, 1, 1, 0.35);
		add(delLocB, 3, 3, 1, 1, 0.25);
		//Row 4
		add(new JLabel(" "), 0, 4, 1, 1);
		add(addB, 1, 4, 1, 1);
		add(exitB, 2, 4, 1, 1, 0.35);
		add(addLocB, 3, 4, 1, 1, 0.25);

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
			LocationData[] locs = new LocationData[locations.getModel().getSize()];
			for (int i = 0; i < locs.length; i++)
				locs[i] = locations.getModel().getElementAt(i);
			Database.addNewItemData((int) identBox.getSelectedItem(), itemNameTF.getText(), Integer.parseInt(initPriceTF.getText()), Integer.parseInt(gePriceTF.getText()), Integer.parseInt(storeStockTF.getText()), stackableCB.getModel().isSelected(), sellSpeedTF.getText(), locs);
			Database.save();
			clear();
			ShopRunData.editDataScreen();
		}
	}

	public class CancelButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Return to the previous screen
			clear();
			ShopRunData.editDataScreen();
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
				checkButton();
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

	public class GePriceHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (identBox.getSelectedItem() != null)
				gePriceTF.setText(String.valueOf(Lib.getItemGEPrice((int) identBox.getSelectedItem(), gePriceTF.getText().isEmpty() ? -1 : Integer.valueOf(gePriceTF.getText()))));
		}
	}

	public class NameUnfocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent e) {

		}

		@Override
		public void focusLost(FocusEvent e) {
			//TODO: Fix the cursor not changing when the mouse is over a textfield
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			potentialIdents = Lib.getPotentialItemIdents(itemNameTF.getText());
			ShopRunData.LOGGER.fine("Possible IDs for "+itemNameTF.getText()+": "+potentialIdents.toString());
			identBox.setModel(new DefaultComboBoxModel<>(potentialIdents.toArray()));
			identBox.setEditable(potentialIdents.size() > 1);
			checkButton();
			setCursor(Cursor.getDefaultCursor());
		}
	}

	public class LocationSelectListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			delLocB.setEnabled(locations.getSelectedValue() != null);
		}
	}

	private void clear() {
		itemNameTF.setText("");
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedTF.setText("Unknown");
		stackableCB.getModel().setSelected(false);
		locations.clearSelection();
		locations.setListData(new LocationData[]{});
		identBox.setModel(new DefaultComboBoxModel<>());
		delLocB.setEnabled(false);
	}

	private class VisibilityAdapter extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			clear();
		}
	}

	private void checkButton() {
		LocationData[] locs = new LocationData[locations.getModel().getSize()];
		for (int i = 0; i < locs.length; i++)
			locs[i] = locations.getModel().getElementAt(i);
		boolean validData = potentialIdents != null && !potentialIdents.isEmpty() && Database.isValidData(itemNameTF.getText(), Integer.parseInt(initPriceTF.getText().isEmpty() ? "-1" : initPriceTF.getText()), Integer.parseInt(gePriceTF.getText().isEmpty() ? "-1" : gePriceTF.getText()), Integer.parseInt(storeStockTF.getText().isEmpty() ? "-1" : storeStockTF.getText()), locs);
		if (!addB.isEnabled() && validData)
			addB.setEnabled(true);
		else if (addB.isEnabled() && !validData)
			addB.setEnabled(false);
		if (!gePriceB.isEnabled() && !itemNameTF.getText().isEmpty())
			gePriceB.setEnabled(true);
		else if (gePriceB.isEnabled() && itemNameTF.getText().isEmpty())
			gePriceB.setEnabled(false);
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
}
