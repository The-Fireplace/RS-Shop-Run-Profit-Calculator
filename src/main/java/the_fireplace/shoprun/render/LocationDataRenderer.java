package the_fireplace.shoprun.render;

import the_fireplace.shoprun.LocationData;

import javax.swing.*;
import java.awt.*;

public class LocationDataRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(((LocationData)value).locationName);
		if(isSelected)
			setBorder(BorderFactory.createLineBorder(new Color(200, 150, 0)));
		else
			setBorder(null);
		return this;
	}
}
