package dev.the_fireplace.shoprun.render;

import dev.the_fireplace.shoprun.LocationData;
import dev.the_fireplace.shoprun.gui.GuiViewData;
import dev.the_fireplace.shoprun.tools.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewLocationDataRenderer extends DefaultListCellRenderer
{
    GuiViewData dataGui;

    public ViewLocationDataRenderer(GuiViewData dataGui) {
        this.dataGui = dataGui;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        LocationData data = (LocationData) value;
        setText(data.locationName);
        HashMap<Pair<Integer, Integer>, ArrayList<LocationData>> pairMap = GuiViewData.locationGroups.get(dataGui.entries.getSelectedValue());
        Object[] pairs = pairMap.keySet().toArray();
        for (int i = 0; i < pairs.length; i++) {
            //noinspection SuspiciousMethodCalls
            if (pairMap.get(pairs[i]).contains(data)) {
                setIcon(new ImageIcon(getClass().getResource("/images/loc" + (i + 1) + ".png")));
            }
        }
        if (isSelected) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 150, 0)));
        } else {
            setBorder(null);
        }
        return this;
    }
}
