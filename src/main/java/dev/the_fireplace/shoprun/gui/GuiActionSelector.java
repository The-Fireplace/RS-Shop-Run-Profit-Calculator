package dev.the_fireplace.shoprun.gui;

import dev.the_fireplace.shoprun.Database;
import dev.the_fireplace.shoprun.ShopRunData;
import dev.the_fireplace.shoprun.tools.EnumDataKey;
import dev.the_fireplace.shoprun.tools.Lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiActionSelector extends JPanel
{
    private final JButton viewB;
    private final JButton editB;
    private final JButton addB;
    private final JButton syncB;
    private final JButton exitB;
    private final JProgressBar syncProgress;
    private static boolean priceSyncing;

    public GuiActionSelector() {
        viewB = new JButton("View Data");
        viewB.addActionListener(new ViewButtonHandler());
        editB = new JButton("Edit Data");
        editB.addActionListener(new EditButtonHandler());
        addB = new JButton("Add Data");
        addB.addActionListener(new AddButtonHandler());
        syncB = new JButton("Sync GE Prices");
        syncB.addActionListener(new SyncButtonHandler());
        exitB = new JButton("Exit");
        exitB.addActionListener(new ExitButtonHandler());

        syncProgress = new JProgressBar();

        setLayout(new GridLayout(3, 2));

        add(viewB);
        add(editB);
        add(addB);
        add(syncB);
        add(exitB);
        add(syncProgress);
    }

    public class ViewButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            ShopRunData.viewDataScreen();
        }
    }

    public class EditButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            ShopRunData.editDataScreen();
        }
    }

    public class AddButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            ShopRunData.addDataScreen();
        }
    }

    public class SyncButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            ShopRunData.LOGGER.finest("Syncing GE Prices");
            if (!priceSyncing) {
                new Thread(() -> {
                    priceSyncing = true;
                    Integer[] idents = Database.getIdentifiers();
                    syncProgress.setValue(0);
                    syncProgress.setMaximum(idents.length - 1);
                    syncProgress.setStringPainted(true);
                    int index = 0;
                    for (int ident : idents) {
                        Database.setData(ident, EnumDataKey.GE_PRICE, Lib.getItemGEPrice(ident, Database.getGEPrice(ident)));
                        syncProgress.setValue(index++);
                        if (ShopRunData.container.apiWait.isSelected()) {
                            try {
                                Thread.sleep(5001);//This is necessary to keep the Runescape Website from feeding the user garbage
                            } catch (InterruptedException e1) {
                                ShopRunData.LOGGER.severe(e1.getLocalizedMessage());
                            }
                        }
                    }
                    priceSyncing = false;
                }, "GE Price Sync Thread").start();
            }
        }
    }

    public class ExitButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            Database.save();
            System.exit(0);
        }
    }
}
