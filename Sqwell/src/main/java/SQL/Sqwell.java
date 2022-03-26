package SQL;

import java.awt.*;  
import java.awt.event.*;  
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import Events.SimpleWindowEvent;
import Misc.KeyManager;
import Misc.Mat;
import Misc.MouseManager;
import Misc.SimpleWindow;
import Rendering.Graphics;

// THIS IS THE OLD VERSION, DO NOT RUN THIS ONE
// ONLY KEPT FOR REFRENCE 
public class Sqwell implements SimpleWindowEvent, ActionListener {
	// SQL Information
	
	// TODO Move to config file on GitHub ignore
	String address = "162.144.12.171";
	String username = "everying_idiot";
	String password = "password";
	String database = "";
	Statement stmt = null;
	Connection con = null;
	
	SimpleWindow window;
	
	Map<String, JMenuItem> menuItemMap = new HashMap<String, JMenuItem>();
	
	int selectedTable = -1, mode = 0;
	
	// TODO Consolidate all information in to an object table
	ArrayList<String> table = new ArrayList<String>();
	ArrayList<String> tables = new ArrayList<String>();
	
	File configFile = new File("C:\\Sol\\Sqwell");
	
	Editor editor = new Editor();
	
	public Sqwell() {
		if(configFile.exists()) {
			
		} else {
			configFile.getParentFile().mkdirs();
			try { configFile.createNewFile(); } catch (IOException e) {}
		}
		
		// Create and start the window
		window = new SimpleWindow();
		window.addSimpleWindowEvent(this);
		window.name = "Sqwell - An SQL client";
		
		Editor.editor.add("");
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenuItem menuItem;
		menuItem = new JMenuItem("Change Database");
		menuItem.addActionListener(this);
		menuBar.add(menuItem);
		menuItemMap.put(menuItem.getText(), menuItem);
		
		menuItem = new JMenuItem("Run Query");
		menuItem.addActionListener(this);
		menuBar.add(menuItem);
		menuItemMap.put(menuItem.getText(), menuItem);
		
		menuItem = new JMenuItem("Exit");
		menuItem.setToolTipText("Exit application");
		menuItem.addActionListener(this);
		menuBar.add(menuItem);
		menuItemMap.put(menuItem.getText(), menuItem);
		
		menuItem = new JMenuItem("Switch");
		menuItem.addActionListener(this);
		menuBar.add(menuItem);
		menuItemMap.put(menuItem.getText(), menuItem);
		
		window.setJMenuBar(menuBar);
		
		window.start();
		
		updateDatabase();
	}
	
	public String updateDatabase() {
		if(!(stmt == null || con == null))
			closeConnection();
		String str = openConnection(database);
		if(str != null || stmt == null || con == null)
			return str;
		
		tables = getTables();
		
		update();
		return null;
	}
	
	public void update() {
		// Check is connection is still alive, if not, reconnect
		if(stmt == null || con == null) openConnection(database);
		
		// Load selected table
		getSelectedTable();
	}
	
	// Images of the tabs/windows
	BufferedImage tableExplorer = null;
	BufferedImage tableViewer = null;
	
	Font font = new Font("Monospaced", Font.PLAIN, 20); // Sets global font as it need to be used in every graphics instances
	int tableView_width = 100; // This will be used to adjust how wide the table explorer needs to be based off the render width of the table names
	int mx, my;
	int x, y, width, height;
	
	// Tick and Render, drawing and mouse stuff goes here
	public void tar(Graphics g) {
		// Get mouse location and store locally
		mx = MouseManager.mouseX; my = MouseManager.mouseY;
		width = g.width; height = g.height;
		g.setFont( font );
		
		switch(mode) {
			case 0:renderViewer(g);break;
			case 1:editor.renderEditor( g );;break;
		}
	}
	
	public void renderViewer(Graphics g) {
		// Table Explorer
		x = 0;
		y = 0;
		width = tableView_width;
		
		if(drawTab(tableExplorer, x, y, width, height))
			tableExplorer = tar_TableExplorer(tableView_width, g.height, mx, my);
		g.drawImage( tableExplorer );
		
		// Table Viewer
		x = tableView_width;
		y = 0;
		width = g.width - x;
		
		if(drawTab(tableViewer, x, y, width, height))
			tableViewer = tar_TableViewer(width, g.height, mx, my);
		g.drawImage( tableViewer, x, y );
	}
	
	public boolean drawTab(BufferedImage image, int x, int y, int width, int height) {
		if(image == null) return true;
		if(image.getWidth() != width || image.getHeight() != height) return true;
		if(Mat.isInRange(mx, x, width) && Mat.isInRange(my, y, height)) return true;
		return false;
	}
	
	// This is the tab that will view the table and display the data
	public BufferedImage tar_TableViewer(int width, int height, int mx, int my) {
		if(width <= 0 || height <= 0) return null;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = new Graphics( img.getGraphics() );
		
		g.setFont( font );
		
		for(int z=0;z<table.size();z++)
			g.drawOutlinedString(table.get(z), 5, (z+1)*g.fontSize);
		
		g.g.dispose();
		return img;
	}
	
	// This tab will show you all of the tables in the selected database
	public BufferedImage tar_TableExplorer(int width, int height, int mx, int my) {
		if(width <= 0 || height <= 0) return null;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = new Graphics( img.getGraphics() );
		
		g.setFont( font );
		
		for(int z=0;z<tables.size();z++) {
			int sLength = g.getStringLength( tables.get(z));
			if(tableView_width < sLength)
				tableView_width = sLength + g.fontSize;
			if(MouseManager.leftPressed)
				if(Mat.isInRange(mx, 0, 100) && Mat.isInRange(my, z*g.fontSize, (z+1)*g.fontSize))
					if(selectedTable != z) {
						selectedTable = z; // Change selected table to table clicked
						update(); // Updates the SQL information
						tableViewer = null; // Sets the image to null to force the image to update on the next frame
					}
			
			// Highlight selected
			g.setColor(new Color(100,100,100));
			if(selectedTable == z)
				g.fillRect(0,z*g.fontSize, width, g.fontSize);
			
			g.drawOutlinedString(tables.get(z), 5, (z+1)*g.fontSize);
		}
		
		g.g.dispose();
		return img;
	}
	
	
	// Returns an ArrayList<String> of the tables available in the selected database
	public ArrayList<String> getTables() {
		ArrayList<String> tables = new ArrayList<String>();
		try {
			ResultSet rs = stmt.executeQuery( "SHOW TABLES" );
			while (rs.next())
				tables.add( rs.getString(1) );
		} catch (SQLException e) {}
		return tables;
	}
	
	// This will get the names of each collum and return them as an array list
	public ArrayList<String> getHeadNames() {
		ArrayList<String> names = new ArrayList<String>();
		try {
			String query = "DESCRIBE  "+tables.get(selectedTable);
			ResultSet rs = stmt.executeQuery( query );
			while (rs.next())
				try {
					names.add( rs.getString( 1 ) );
				} catch(Exception e) { break; }
		} catch (SQLException e1) {}
		return names;
	}
	
	// This function will load the selected table
	public void getSelectedTable() {
		// If table number is not found, exit
		if(selectedTable < 0 || selectedTable > tables.size()) return;
		
		table = new ArrayList<String>(); // Reset loaded table to empty
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		list.add(getHeadNames());// Adds head names
		
		try {
			String query = "SELECT * FROM "+tables.get(selectedTable);
			ResultSet rs = stmt.executeQuery( query );
			while (rs.next()) {
				ArrayList<String> li = new ArrayList<String>();
				int x = 1;
				while(true) // Loop runs until an error is reached
					try {
						String str = rs.getString( x );
						li.add(str);
						x++;
					} catch(Exception e) { break; }
				list.add(li);
			}
		} catch (SQLException e1) { e1.printStackTrace(); }
		
		if(list == null || list.size() <= 0) return;
		
		int[] s = new int[list.get(0).size()];
		for(int y=0;y<list.size();y++) {
			for(int x=0;x<list.get(y).size();x++) {
				if(list.get(y).get(x) != null && s[x] < list.get(y).get(x).length())
					s[x] = list.get(y).get(x).length();
			}
		}
		for(int y=0;y<list.size();y++) {
			String str = "";
			for(int x=0;x<list.get(y).size();x++) {
				if(s[x] <= 0) {
					str += list.get(y).get(x);
					continue;
				}
				str += String.format("%-"+s[x]+"s ", list.get(y).get(x));
			}
			
			table.add( str );
		}
	}
	
	// Just connecting code
	public String openConnection(String database) {
		System.out.println("Opening connection");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection( "jdbc:mysql://162.144.12.171/"+database, username, password);
			
			stmt = con.createStatement();
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		return null;
	}
	
	public void closeConnection() {
		System.out.println("Closing SQL conection");
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) { new Sqwell(); }

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuItemMap.get("Exit")) {
			System.exit(0);
		}
		if(e.getSource() == menuItemMap.get("Switch")) {
			mode += 1;
			if(mode >= 2) mode = 0;
		}
		
		if(e.getSource() == menuItemMap.get("Run Query")) {
			String query = JOptionPane.showInputDialog("Enter a query");
			if(query == null) { return; }
			if(query.length() <= 0) { JOptionPane.showMessageDialog(null,"Query can not be empty."); return; }
			
			System.out.println( query );
			try {
				ResultSet rs = stmt.executeQuery( query );
				while (rs.next()) {
					ArrayList<String> li = new ArrayList<String>();
					int x = 1;
					while(true) // Loop runs until an error is reached
						try {
							String str = rs.getString( x );
							li.add(str);
							x++;
						} catch(Exception ee) { break; }
					for(String str:li)
						System.out.print(str);
					System.out.println();
				}
			} catch (SQLException e2) { e2.printStackTrace(); }
		}
		
		if(e.getSource() == menuItemMap.get("Change Database")) {
			String in = JOptionPane.showInputDialog("Enter a database");
			if(in == null || in.length() <= 0) return;
			String storedDatabase = database;
			database = in;
			String str = updateDatabase();
			if(str != null) {
				JOptionPane.showMessageDialog(null,str);
				database = storedDatabase;
				updateDatabase();
			}
		}
	}
}
