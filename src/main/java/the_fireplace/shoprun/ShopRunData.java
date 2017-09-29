package the_fireplace.shoprun;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ShopRunData extends JFrame {
	private static final String HEADER = "Runescape Shop Run Database pre1.0-b2";
	static final int DEFAULT_WIDTH = 1280;
	static final int DEFAULT_HEIGHT = 720;

	private JMenuBar menuBar;
	private JMenu fileM, editM, helpM;
	JCheckBoxMenuItem apiWait, exportLog;
	public static final Logger LOGGER = Logger.getLogger("Runescape Shop Run Database");

	private CardLayout layout;
	static ShopRunData container;
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

					LOGGER.info("Logger enabled");

				} catch (SecurityException | IOException ex) {
					ex.printStackTrace();
				}
			} else {
				LOGGER.info("Logging disabled");
				LOGGER.removeHandler(fh);
				fh.close();
			}
		});
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
		new ShopRunData();
	}
}
