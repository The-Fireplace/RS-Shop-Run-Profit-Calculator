package the_fireplace.shoprun;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewLocationDataRenderer extends DefaultListCellRenderer {

	GuiViewData dataGui;

	ViewLocationDataRenderer(GuiViewData dataGui){
		this.dataGui = dataGui;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		LocationData data = (LocationData)value;
		setText(data.locationName);
		HashMap<Pair<Integer, Integer>, ArrayList<LocationData>> pairMap = GuiViewData.locationGroups.get(dataGui.entries.getSelectedValue());
		Object[] pairs = pairMap.keySet().toArray();
		for(int i=0;i<pairs.length;i++){
			//noinspection SuspiciousMethodCalls
			if(pairMap.get(pairs[i]).contains(data))
				setIcon(new ImageIcon(getClass().getResource("/images/loc" + String.valueOf(i+1) + ".png")));
		}
		return this;
	}
}
