package ISectors;

import java.io.*;
import java.net.*;
import java.util.Random;

import javax.swing.JOptionPane;

import ISectors.ships.CapitalShip;

public class GameManager {
	public static final int port_num = 1717;
	public static GameManager Instance;
	public enum GameType { NETWORK, LOCAL };

	public static final int DEFAULT_ROWS = 25;
	public static final int DEFAULT_COLS = 25;
	
	private GameType gameType;
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private Location[][] _grid;
	private int numRows;
	private int numCols;

	
	public static void Initialize() {
		if(Instance == null) {
			Instance = new GameManager();
		}
	}
	
	public static void NewGame(GameType type, int nPlayers, InetAddress addr) {
		NewGame(DEFAULT_ROWS, DEFAULT_COLS, type, nPlayers, addr);
	}
	
	public static void NewGame(int nRows, int nCols, GameType type, int nPlayers, InetAddress addr)
	{
		Instance.gameType = type;
		if(type == GameType.NETWORK) {
			Instance.setUPConnection(addr);
			// read number of players from network game
			TurnManager.initManager(nPlayers);
		} else if (type == GameType.LOCAL) {
			TurnManager.initManager(nPlayers);
		}

		Instance.setUpMap(nRows, nCols);
		BattleMap.selectedLoc = null;
		BattleMap.selectedShip = null;
		BattleMap.Instance.loadBattleMap(nRows, nCols);
	}
	
	private GameManager() 
	{
	}
	
	private void setUpMap(int nRows, int nCols) {
		this.numCols = nCols;
		this.numRows = nRows;
		
		_grid = new Location[numRows][numCols];
		
		for(int x = 0; x < numRows; x++) {
			for(int y = 0; y < numCols; y++) {
				_grid[x][y] = new Location(x, y);
			}
		}
		
		float minDistance = Math.max(numRows, numCols) / TurnManager.numPlayers;
		Location[] startPoints = new Location[TurnManager.numPlayers];
		int maxAttempts = 5, attempts = 0; 
		boolean validLoc;
		Random r = new Random();
		int xPos = 0, yPos = 0;
		xPos = r.nextInt(numRows);
		yPos = r.nextInt(numCols);
		// Randomly place the first player.
		_grid[xPos][yPos].EnterSector(new CapitalShip(1));
		startPoints[0] = _grid[xPos][yPos];
		for(int i = 2; i <= TurnManager.numPlayers; i++) {
			validLoc = true;
			attempts = 0;
			do {
				// Randomly determine a direction and a distance from first player.
				xPos = r.nextInt(numRows);
				yPos = r.nextInt(numCols);
				// Analyze location as a valid location. i.e. not within minDistance of other players and still a valid location.
				for(int loc = 0; loc < i - 1; loc++) {
					if(Location.distance(startPoints[loc], _grid[xPos][yPos]) < minDistance) {
						validLoc = false;
						break;
					}
				}
				attempts++;
			} while(!validLoc && attempts < maxAttempts);
			// If valid, assign location as starting point and move on.
			_grid[xPos][yPos].EnterSector(new CapitalShip(i));
			startPoints[i - 1] = _grid[xPos][yPos];
		}
	}
	
	private void setUPConnection(InetAddress addr) {
		try{
			sock = new Socket(addr, port_num);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Could not connect");
			System.out.println("Could not connect.");
			e.printStackTrace();
		}
	}
	
	public Location[][] getGrid() {
		return _grid;
	}
	
	public GameType getGameType() {
		return gameType;
	}
}
