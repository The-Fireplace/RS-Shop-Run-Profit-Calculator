package the_fireplace.shoprun;

import javax.swing.*;
import java.awt.*;

public class ViewLocationDataRenderer extends DefaultListCellRenderer {

	GuiViewData dataGui;

	ViewLocationDataRenderer(GuiViewData dataGui){
		this.dataGui = dataGui;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		LocationData data = (LocationData)value;
		setText(data.locationName);
		return this;
	}
}
