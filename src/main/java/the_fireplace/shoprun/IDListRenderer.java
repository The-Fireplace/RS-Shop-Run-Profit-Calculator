package the_fireplace.shoprun;

import javax.swing.*;
import java.awt.*;

public class IDListRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(Database.getItemName((int) value));
		if(isSelected)
			setBorder(BorderFactory.createLineBorder(new Color(200, 150, 0)));
		else
			setBorder(null);
		return this;
	}
}
