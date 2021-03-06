package ISectors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import ISectors.GameManager.GameType;

public class BattleWindow extends JFrame implements ActionListener {	
	private static final long serialVersionUID = 5149958171543488559L;

	/*
	 * This class will eventually contain other information, such as info panels, and data panels.
	 * This will also control switching between different menus...Maybe.
	 */
	
	public BattleWindow() {
		setSize(480, 480);
		GameManager.Initialize();
		initComponents();
	}
	
	public void initComponents() {
		BattleMap m = new BattleMap();
		m.setPreferredSize(new Dimension(750, 750));
		add(m, BorderLayout.CENTER);
		
		JButton b = new JButton("End Turn");
		b.addActionListener(m);
		add(b, BorderLayout.SOUTH);
		
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		JMenu fileMenu = new JMenu("File");
		JMenu OptionMenu = new JMenu("Options");
		bar.add(fileMenu);
		bar.add(OptionMenu);
		
		JMenuItem itmNewGame = new JMenuItem("New Game");
		JMenuItem itmExit = new JMenuItem("Exit");
		fileMenu.add(itmNewGame);
		fileMenu.add(itmExit);
		
		itmNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generateNewGameMenu();
			}
		});
		
		itmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(DISPOSE_ON_CLOSE);
			}
		});
		
		pack();
	}
	
	private JDialog dialog;
	private JPanel displayPane;
	private JComboBox<String> modeSelect;
	private JLabel[] labels;
	private JComboBox<Integer> playerSelect;
	private JTextField[] sizes;
	private JButton[] buttons;
	private JTable gameList;
	
	private void generateNewGameMenu() {
		dialog = new JDialog(this, "New Game");
		dialog.setSize(200, 80);
		dialog.setModal(true);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		//Pre-generated, since these don't change.
		buttons = new JButton[4];
		buttons[0] = new JButton("Done");
		buttons[1] = new JButton("Cancel");
		buttons[0].addActionListener(this);
		buttons[1].addActionListener(this);
		
		JLabel modeLabel = new JLabel("Mode:");
		String[] options = {"LOCAL", "ONLINE"};
		modeSelect = new JComboBox<String>(options);
		modeSelect.setSelectedIndex(0);
		modeSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Swap out Local and Online display.
				displayPane.removeAll();
				if(modeSelect.getSelectedItem() == "LOCAL") {
					createLocalData(displayPane);
				} else if(modeSelect.getSelectedItem() == "ONLINE") {
					createOnlineData(displayPane);
				}
				displayPane.validate();
				dialog.validate();
				dialog.pack();
			}
		});

		JPanel modePanel = new JPanel();
		modePanel.add(modeLabel);
		modePanel.add(modeSelect);
		dialog.add(modePanel);
		
		displayPane = new JPanel();
		displayPane.setLayout(new BoxLayout(displayPane, BoxLayout.PAGE_AXIS));
		dialog.add(displayPane);
		
		createLocalData(displayPane);
		
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private void createLocalData(JPanel pane) {
		labels = new JLabel[4];
		labels[0] = new JLabel("Number of Players:");
		labels[1] = new JLabel("Size of Map:");
		labels[2] = new JLabel("Width:");
		labels[3] = new JLabel("Height:");
		
		Integer[] choices = {2, 3, 4, 5, 6, 7, 8, 9, 10};
		playerSelect = new JComboBox<Integer>(choices);
		playerSelect.setSelectedIndex(0);
		
		sizes = new JTextField[2];
		sizes[0] = new JTextField(GameManager.DEFAULT_ROWS + "", 6);
		sizes[1] = new JTextField(GameManager.DEFAULT_COLS + "", 6);
		
		
		JPanel p = new JPanel();
		p.add(labels[0]);
		p.add(playerSelect);
		pane.add(p);
		labels[1].setAlignmentX(Component.RIGHT_ALIGNMENT);
		pane.add(labels[1]);
		JPanel wPanel = new JPanel();
		wPanel.add(labels[2]);
		wPanel.add(sizes[0]);
		pane.add(wPanel);
		JPanel hPanel = new JPanel();
		hPanel.add(labels[3]);
		hPanel.add(sizes[1]);
		pane.add(hPanel);
		JPanel btnPanel = new JPanel();
		btnPanel.add(buttons[0]);
		btnPanel.add(buttons[1]);
		pane.add(btnPanel);
	}

	private void createOnlineData(JPanel pane) {
		String[] columnNames = {"Lobby Name", "Current #", "# of Players", "Password?"};
		Object[][] data = {{"Game 1", new Integer(1), new Integer(2), "Yes"}, 
				{"Game 2", new Integer(3), new Integer(3), "No"}
		};
		// Need to create a table model to store data
		gameList = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(gameList);
		gameList.setFillsViewportHeight(true);
		
		buttons[2] = new JButton("Refresh");
		buttons[2].setAlignmentX(LEFT_ALIGNMENT);
		buttons[3] = new JButton("Create");
		buttons[3].setAlignmentX(RIGHT_ALIGNMENT);
		buttons[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Request data refresh from server.
				//Update table data
				
			}
		});
		buttons[3].addActionListener(this);
		
		JPanel tablePane = new JPanel();
		tablePane.add(buttons[2]);
		tablePane.add(buttons[3]);
		pane.add(tablePane);
		
		pane.add(scrollPane);

		JPanel btnPanel = new JPanel();
		btnPanel.add(buttons[0]);
		btnPanel.add(buttons[1]);
		pane.add(btnPanel);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttons[0]) {
			if(modeSelect.getSelectedItem() == "LOCAL") {
				int nRows = Integer.parseInt(sizes[0].getText());
				int nCols = Integer.parseInt(sizes[1].getText());
				int nPlayers = (Integer)(playerSelect.getSelectedItem());
				GameManager.NewGame(nRows, nCols, GameType.LOCAL, nPlayers, null);
			} else if(modeSelect.getSelectedItem() == "ONLINE") {
				System.out.println("Connect to online game?");
			}
			dialog.setVisible(false);
			dialog.dispose();
		} else if(e.getSource() == buttons[1]) {
			dialog.setVisible(false);
			dialog.dispose();
		} else if(e.getSource() == buttons[3]) {
			System.out.println("Not implemented yet");
		} else {
			System.out.println("Clicked on " + e.getSource());
		}
	}
}