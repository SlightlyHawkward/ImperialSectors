package ISectors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import ISectors.GameManager.GameType;
import ISectors.ships.*;
import ISectors.ships.Ship.Orders;

public class BattleMap extends JPanel implements MouseListener, ActionListener {
	public static BattleMap Instance;
	
	private static final long serialVersionUID = -8876256816008699262L;
	private Color backgroundColor = Color.white;
	private Location[][] _grid = null;
	private int _numRows = 25;
	private int _numCols = 25;
	private PopupMenuHandler popupHandler;
	
	static boolean displayMap = true;
	static Location selectedLoc = null;
	static Ship selectedShip = null;
//	static Orders selectedOrder = Orders.STANDBY;
	
	public BattleMap() {
		addMouseListener(this);
		popupHandler = new PopupMenuHandler(this);
		setLayout(new GridLayout(_numCols, _numRows));
		if(Instance == null)
			Instance = this;
	}
	
	public BattleMap(int nRows, int nCols) {
		_numRows = nRows;
		_numCols = nCols;
		setLayout(new GridLayout(_numCols, _numRows));
		addMouseListener(this);
		popupHandler = new PopupMenuHandler(this);
		
		if(Instance == null) 
			Instance = this;
	}
	
	public void loadBattleMap(int nRows, int nCols) {
		this.removeAll();
		_numRows = nRows;
		_numCols = nCols;
		this.setLayout(new GridLayout(_numCols, _numRows));
		_grid = GameManager.Instance.getGrid();
		
		for(int x = 0; x < _numRows; x++) {
			for(int y = 0; y < _numCols; y++) {
				_grid[x][y].addMouseListener(this);
				this.add(_grid[x][y]);
			}
		}
		this.validate();

		repaint();
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		g.setColor(backgroundColor);
		Rectangle bounds = this.getBounds();
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
//	    int cellWidth = bounds.width / _numRows;
//	    int cellHeight = bounds.height / _numCols;
		
		if(_grid != null) {
			for(int x = 0; x < _numRows; x++) {
				for(int y = 0; y < _numCols; y++) {
					_grid[x][y].paint(g);
				}
			}//*/
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(_grid == null) return;
		if(e.getComponent() instanceof Location) {
			Location l = (Location) e.getComponent();
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(e.isShiftDown()) {
					if(selectedLoc != null) {
						selectedLoc.assignOrder(Orders.MOVE, l);
						selectedLoc.selected = false;
						selectedLoc = null;
					} else if(selectedShip != null) {
						selectedShip.assignOrder(Orders.MOVE, l);
						selectedShip.getLoc().selected = false;
						selectedShip = null;
					}
				} else {
					if(selectedLoc != null) {
						selectedLoc.selected = false;
						selectedLoc = null;
					}
					if(selectedShip != null) {
						selectedShip.getLoc().selected = false;
						selectedShip = null;
					}
					if(!l.isEmptyOrInvisible() && l.Allegiance() == TurnManager.currentPlayer) {
						selectedLoc = l;
						l.selected = true;
					}
				}
			}
			else if(e.getButton() == MouseEvent.BUTTON3) {
				// Send order to ships at selectedLoc to move to l
				if(e.isShiftDown()) {
					if(selectedLoc != null) {
						selectedLoc.assignOrder(Orders.MOVE, l);
						selectedLoc.selected = false;
						selectedLoc = null;
					} else if(selectedShip != null) {
						selectedShip.assignOrder(Orders.MOVE, l);
						selectedShip.getLoc().selected = false;
						selectedShip = null;
					}
				} else {
					popupHandler.generatePopUp(l).show(e.getComponent(), e.getX(), e.getY());
				}
			}
		} else {
			System.out.println("Clicked on " + e.getComponent().toString());
		}
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(GameManager.Instance.getGameType() == GameType.LOCAL) {
			TurnManager.nextTurn();
			displayMap = false;
			repaint();
			JOptionPane.showMessageDialog(this, "Player " + TurnManager.currentPlayer + "'s turn.");
			displayMap = true;
		}
		repaint();
	}
		
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}

class PopupMenuHandler implements ActionListener {
	private BattleMap parent;
	private JPopupMenu popup;
	private Hashtable<JMenuItem, Orders> orderItems;
	private Hashtable<JMenuItem, Ship> shipItems;
	private Hashtable<JMenuItem, Ship> upgradeItems;
	private JMenuItem locItem;
	private Location associatedLoc;
	
	public PopupMenuHandler(BattleMap par){
		orderItems = new Hashtable<JMenuItem, Orders>();
		shipItems = new Hashtable<JMenuItem, Ship>();
		upgradeItems = new Hashtable<JMenuItem, Ship>();
		parent = par;
	}
	
	public JPopupMenu generatePopUp(Location l) {
		orderItems.clear();
		shipItems.clear();
		upgradeItems.clear();
		JMenuItem menuItem;
		popup = new JPopupMenu();
		Ship[] s = l.getOccupants();

		/*** AVAILABLE ORDERS FOR SELECTED SHIPS ***/
		if(BattleMap.selectedLoc != null && !BattleMap.selectedLoc.isEmptyOrInvisible()) {
			Ship[] ships = BattleMap.selectedLoc.getOccupants();
			boolean upgradeMenuCreated = false;
			Orders[] orders;
			for(int i = 0; i < ships.length; i++) {
				orders = ships[i].getOrders();
				for(int j = 0; j < orders.length; j++) {
					if(!orderItems.containsValue(orders[j]) && (orders[j] != Orders.UPGRADE || !upgradeMenuCreated)) {
						if(orders[j] == Orders.UPGRADE) {
							// Add a submenu with a list of ships to upgrade
							JMenu upgradeMenu = new JMenu(Ship.OrderToString(orders[j]));
							if(!l.isEmptyOrInvisible())
							{
								for(int si = 0; si < s.length; si++) {
									if(s[si].canUpgrade()) {
										menuItem = new JMenuItem(s[si].getName());
										menuItem.addActionListener(this);
										upgradeMenu.add(menuItem);
										upgradeItems.put(menuItem, s[si]);
									}
								}
							}
							popup.add(upgradeMenu);
							upgradeMenuCreated = true;
						} else {
							menuItem = new JMenuItem(Ship.OrderToString(orders[j]));
							menuItem.addActionListener(this);
							popup.add(menuItem);
							orderItems.put(menuItem, orders[j]);
						}
					}
				}
			}
			popup.addSeparator();
		} /*** AVAILABLE ORDERS FOR SELECTED LOCATION ***/
		else if(BattleMap.selectedShip != null) {
			Orders[] orders = BattleMap.selectedShip.getOrders();
			for(int i = 0; i < orders.length; i++) {
				if(orders[i] == Orders.UPGRADE) {
					JMenu upgradeMenu = new JMenu(Ship.OrderToString(orders[i]));
					if(!l.isEmptyOrInvisible()) {
						for(int si = 0; si < s.length; si++) {
							if(s[si].getClass() != CapitalShip.class) {
								menuItem = new JMenuItem(s[si].getName());
								menuItem.addActionListener(this);
								upgradeMenu.add(menuItem);
								upgradeItems.put(menuItem, s[si]);
							}
						}
					}
					popup.add(upgradeMenu);
				} else {
					menuItem = new JMenuItem(Ship.OrderToString(orders[i]));
					menuItem.addActionListener(this);
					popup.add(menuItem);
					orderItems.put(menuItem, orders[i]);
				}
			}
			popup.addSeparator();
		}
		
		//Ships at location
		if(s != null && s.length > 0) {
			for(int i = 0; i < s.length; i++) {
				menuItem = new JMenuItem(s[i].getName());
				if(s[i].getLoyalty() != TurnManager.currentPlayer) {
					menuItem.setForeground(Color.red);
				}
				menuItem.addActionListener(this);
				popup.add(menuItem);
				shipItems.put(menuItem, s[i]);
			}
			popup.addSeparator();
		}
		
		//Location Information
		menuItem = new JMenuItem("Location " + l.toString());
		menuItem.addActionListener(this);
		popup.add(menuItem);
		locItem = menuItem;
		associatedLoc = l;
		return popup;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(orderItems.containsKey(e.getSource())) {
			Orders order = orderItems.get(e.getSource());
			if(BattleMap.selectedLoc != null) {
				BattleMap.selectedLoc.assignOrder(order, associatedLoc);
				BattleMap.selectedLoc.selected = false;
				BattleMap.selectedLoc = null;
			} else if(BattleMap.selectedShip != null) {
				BattleMap.selectedShip.assignOrder(order, associatedLoc);
				BattleMap.selectedShip.getLoc().selected = false;
				BattleMap.selectedShip = null;
			}
		} else if(upgradeItems.containsKey(e.getSource())) {
			if(BattleMap.selectedLoc != null) {
				// retrieve CapitalShip from location
				Ship[] ships = BattleMap.selectedLoc.getOccupants();
				for(int i = 0; i < ships.length; i++) {
					if(ships[i].getClass() == CapitalShip.class) {
						CapitalShip s = (CapitalShip) ships[i];
						s.assignOrder(Orders.UPGRADE, upgradeItems.get(e.getSource()));
						s.getLoc().selected = false;
						BattleMap.selectedLoc = null;
						break;
					}
				}
			} else if(BattleMap.selectedShip != null) {
				if(BattleMap.selectedShip.getClass() == CapitalShip.class) {
					CapitalShip s = (CapitalShip) BattleMap.selectedShip;
					s.assignOrder(Orders.UPGRADE, upgradeItems.get(e.getSource()));
					s.getLoc().selected = false;
					BattleMap.selectedShip = null;
				}
			}
		} else if(shipItems.containsKey(e.getSource())) {
			if(BattleMap.selectedLoc != null) {
				BattleMap.selectedLoc.selected = false;
				BattleMap.selectedLoc = null;
			}
			if(BattleMap.selectedShip != null) {
				BattleMap.selectedShip.getLoc().selected = false;
			}
			Ship s = shipItems.get(e.getSource());
			if(s.getLoyalty() == TurnManager.currentPlayer) {
				BattleMap.selectedShip = s;
				BattleMap.selectedShip.getLoc().selected = true;
			}
		} else if(locItem == e.getSource()) {
			if(BattleMap.selectedLoc != null) {
				BattleMap.selectedLoc.selected = false;
				BattleMap.selectedLoc = null;
			}
			if(BattleMap.selectedShip != null) {
				BattleMap.selectedShip.getLoc().selected = false;
				BattleMap.selectedShip = null;
			}
			if(!associatedLoc.isEmptyOrInvisible() && associatedLoc.Allegiance() == TurnManager.currentPlayer) {
				BattleMap.selectedLoc = associatedLoc;
				BattleMap.selectedLoc.selected = true;
			}
		}
		parent.repaint();
	}
}