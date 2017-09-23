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

class GuiAddData extends JPanel {
	private JButton addB, exitB, gePriceB;
	private JLabel identifierL, itemNameL, initPriceL, gePriceL, storeStockL, sellSpeedL;
	private JTextField identifierTF, itemNameTF, initPriceTF, gePriceTF, storeStockTF, sellSpeedTF;
	private JCheckBox stackableCB;
	private JList<String> locations;
	private JScrollPane locationsSc;

	GuiAddData() {
		CanAddDataListener canAddToDatabase = new CanAddDataListener();
		addB = new JButton("Confirm");
		addB.addActionListener(new ConfirmButtonHandler());
		addB.setEnabled(false);
		exitB = new JButton("Cancel");
		exitB.addActionListener(new CancelButtonHandler());
		identifierTF = new JTextField();
		identifierTF.getDocument().addDocumentListener(canAddToDatabase);
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
		sellSpeedTF.setText("Unknown");
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
		locationsSc.setPreferredSize(new Dimension(ShopRunData.DEFAULT_WIDTH/4, ShopRunData.DEFAULT_HEIGHT/5*2));
		gePriceB = new JButton("Get GE Price");
		gePriceB.addActionListener(new GePriceHandler());

		setLayout(new GridBagLayout());//4x5
		//Row 0
		add(identifierL, 0, 0, 1, 1);
		add(identifierTF, 1, 0, 1, 1);
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
		add(locationsSc, 3, 2, 1, 4, 0.25);
		//Row 3
		add(storeStockL, 0, 3, 1, 1);
		add(storeStockTF, 1, 3, 1, 1);
		add(stackableCB, 2, 3, 1, 1, 0.35);
		//Row 4
		add(new JLabel(" "), 0, 4, 1, 1);
		add(addB, 1, 4, 1, 1);
		add(exitB, 2, 4, 1, 1, 0.35);

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

	public class GePriceHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!itemNameTF.getText().isEmpty())
				gePriceTF.setText(String.valueOf(Lib.getItemGEPrice(itemNameTF.getText(), gePriceTF.getText().isEmpty() ? -1 : Integer.valueOf(gePriceTF.getText()), 0)));
		}
	}

	private void clear() {
		identifierTF.setText("");
		itemNameTF.setText("");
		initPriceTF.setText("");
		gePriceTF.setText("");
		storeStockTF.setText("");
		sellSpeedTF.setText("Unknown");
		stackableCB.getModel().setSelected(false);
		locations.clearSelection();
	}

	private class VisibilityAdapter extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			clear();
		}
	}

	private class CanAddDataListener implements DocumentListener, ListSelectionListener {
		void checkButton() {
			String[] locs = new String[locations.getSelectedValuesList().size()];
			int index = 0;
			for (String loc : locations.getSelectedValuesList())
				locs[index++] = loc;
			boolean validData = Database.isValidIdentifier(identifierTF.getText()) && Database.isValidData(identifierTF.getText(), itemNameTF.getText(), Integer.parseInt(initPriceTF.getText().isEmpty() ? "-1" : initPriceTF.getText()), Integer.parseInt(gePriceTF.getText().isEmpty() ? "-1" : gePriceTF.getText()), Integer.parseInt(storeStockTF.getText().isEmpty() ? "-1" : storeStockTF.getText()), locs);
			if (!addB.isEnabled() && validData)
				addB.setEnabled(true);
			else if (addB.isEnabled() && !validData)
				addB.setEnabled(false);
			if (!gePriceB.isEnabled() && !itemNameTF.getText().isEmpty())
				gePriceB.setEnabled(true);
			else if (gePriceB.isEnabled() && itemNameTF.getText().isEmpty())
				gePriceB.setEnabled(false);
		}

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
}
