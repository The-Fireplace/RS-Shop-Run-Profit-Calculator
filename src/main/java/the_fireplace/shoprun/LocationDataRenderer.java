package the_fireplace.shoprun;

import javax.swing.*;
import java.awt.*;

public class LocationDataRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(((LocationData)value).locationName);
		return this;
	}
}
