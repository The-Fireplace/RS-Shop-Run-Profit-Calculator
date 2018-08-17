package the_fireplace.shoprun;

import the_fireplace.shoprun.gui.GuiActionSelector;
import the_fireplace.shoprun.gui.GuiAddData;
import the_fireplace.shoprun.gui.GuiEditData;
import the_fireplace.shoprun.gui.GuiViewData;
import the_fireplace.shoprun.tools.Lib;

import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ShopRunData extends JFrame {
	private static final String HEADER = "Runescape Shop Run Database pre1.0-b4";
	public static final int DEFAULT_WIDTH = 1280;
	public static final int DEFAULT_HEIGHT = 720;

	private JMenuBar menuBar;
	private JMenu fileM, editM, helpM;
	public JCheckBoxMenuItem apiWait;
	JCheckBoxMenuItem exportLog;
	public static final Logger LOGGER = Logger.getLogger("Runescape Shop Run Database");

	private CardLayout layout;
	public static ShopRunData container;
	private FileHandler fh;

	public ShopRunData() {
		container = this;
		setTitle(HEADER);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//Center the application
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();
		fileM = new JMenu("File");
		exportLog = new JCheckBoxMenuItem("Create log");
		exportLog.getModel().addActionListener(e -> {
			if (exportLog.getModel().isSelected()) {
				try {
					// This block configures the logger with handler and formatter
					fh = new FileHandler("shoprun.log");
					LOGGER.addHandler(fh);
					LOGGER.setLevel(Level.ALL);
					SimpleFormatter formatter = new SimpleFormatter();
					fh.setFormatter(formatter);
					Database.setLoggerEnabled(true);
					LOGGER.info("Logger enabled");

				} catch (SecurityException | IOException ex) {
					ex.printStackTrace();
				}
			} else {
				Database.setLoggerEnabled(false);
				LOGGER.info("Logging disabled");
				LOGGER.removeHandler(fh);
				fh.close();
			}
		});
		exportLog.getModel().setSelected(Database.isLoggerEnabled());
		fileM.add(exportLog);
		editM = new JMenu("Settings");
		apiWait = new JCheckBoxMenuItem("Delay between API Usage");
		apiWait.setSelected(true);
		editM.add(apiWait);
		helpM = new JMenu("About");
		menuBar.add(fileM);
		menuBar.add(editM);
		menuBar.add(helpM);
		setJMenuBar(menuBar);

		Container pane = getContentPane();
		layout = new CardLayout(1, 1);
		pane.setLayout(layout);
		pane.add(new GuiActionSelector(), "action_selector");
		pane.add(new GuiAddData(), "add_data");
		pane.add(new GuiEditData(), "edit_data");
		pane.add(new GuiViewData(), "view_data");

		actionSelectScreen();

		Lib.loadItemIDs();
	}

	public static void actionSelectScreen() {
		container.layout.show(container.getContentPane(), "action_selector");
	}

	public static void addDataScreen() {
		container.layout.show(container.getContentPane(), "add_data");
	}

	public static void editDataScreen() {
		container.layout.show(container.getContentPane(), "edit_data");
	}

	public static void viewDataScreen() {
		container.layout.show(container.getContentPane(), "view_data");
	}

	public static void main(String[] args) throws IOException {
		Database.init();
		customizeUI();
		new ShopRunData();
	}

	private static void customizeUI(){
		Object grad = UIManager.get("Button.gradient");
		List gradient;
		if (grad instanceof List) {
			gradient = (List) grad;
			gradient.set(2, new ColorUIResource(228, 204, 109));
			gradient.set(3, new ColorUIResource(208, 153, 41));
			gradient.set(4, new ColorUIResource(130, 113, 35));
		}
		UIManager.put("Button.select", new Color(208, 184, 89));//The color of the selected button
		UIManager.put("Button.border", BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(188, 164, 89), new Color(110, 113, 35)));//The yellow is kind of bright, but it works.
		UIManager.put("ProgressBar.selectionBackground", new Color(208, 153, 41));//The percent, when shown.
		UIManager.put("ProgressBar.foreground", new Color(248, 193, 81));//The bar itself
	}
}
