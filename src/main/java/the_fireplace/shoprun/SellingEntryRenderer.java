package the_fireplace.shoprun;

import javax.swing.*;
import java.awt.*;

public class SellingEntryRenderer extends JLabel implements ListCellRenderer<String>{
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		int profitPerStoreRun = Database.getProfitPerStorePerRun(value);
		String iconCode = "";
		if(profitPerStoreRun <= 0)
			iconCode = "x";
		else if(profitPerStoreRun < 1000)
			iconCode = "1k";
		else if(profitPerStoreRun < 5000)
			iconCode = "5k";
		else if(profitPerStoreRun < 10000)
			iconCode = "10k";
		else if(profitPerStoreRun >= 100000)
			iconCode = "100k";
		else if(profitPerStoreRun >= 50000)
			iconCode = "50k";
		else if(profitPerStoreRun >= 40000)
			iconCode = "40k";
		else if(profitPerStoreRun >= 30000)
			iconCode = "30k";
		else if(profitPerStoreRun >= 20000)
			iconCode = "20k";
		else
			iconCode = "10kp";
		if(!iconCode.isEmpty()) {
			ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/" + iconCode + ".png"));
			setIcon(imageIcon);
		}
		setText(value);
		return this;
	}
}