package Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Misc.KeyManager;
import Misc.Mat;
import Misc.MouseManager;
import Rendering.Graphics;
import SQL.SQLClientManager;
import SQL.SQLDatabase;
import SQL.SQLTable;

public class Viewer {
	
	public int width = 0;
	BufferedImage img = null;
	SQLClientManager cm = null;
	
	public Viewer() {}
	public Viewer(SQLClientManager cm) { this.cm = cm; }
	
	int mx = 0, my = 0;
	double dx = 0, dy = 0;
	int explorer_verticalScroll = 0, explorer_horizontalScroll = 0;
	int verticalScroll = 0, horizontalScroll = 0;
	
	SQLDatabase selectedDatabase = null;
	String selectedTable = null;
	
	// This will render the explore
	public void render(Graphics g, int x, int y) {
		mx = MouseManager.mouseX;
		my = MouseManager.mouseY;
		
		g.setFont( Sqwell.font );
		
		// Let this render first so that it is under the explorer
		if(selectedDatabase != null && selectedTable != null) renderTableData(g, selectedDatabase, selectedTable);
		
		g.setColor( new Color(0,0,0) );
		g.fillRect(x, y, width, g.height);
		
		int z = 1, indent = 10;
		
		int databaseNameLength = 0;
		for(SQLDatabase db:cm.databases)
			if(databaseNameLength < g.getStringLength( db.database ))
				databaseNameLength = g.getStringLength( db.database );
		
		for(int b=0;b<cm.databases.size();b++) {
			SQLDatabase db = cm.databases.get(b);
			
			// Draw database with color based on connection
			g.drawOutlinedString(
					db.database, 0 + explorer_horizontalScroll,
					z*g.fontSize + explorer_verticalScroll, 
					(db.isConnected())? new Color(0, 200, 0) : new Color(200, 0, 0), // Inline if statement
					new Color(0,0,0)
			);
			
			if(width < g.getStringLength(db.database))
				width = g.getStringLength(db.database) + 5;
			z++;
			
			for(String key:db.tables.keySet()) {
				// Set values to temp values to easly use in the calculations for the mouse click and the draw location
				dx = indent*2 + explorer_horizontalScroll;
				dy = z++*g.fontSize + explorer_verticalScroll;
				
				// Check if a table is clicked
				if(MouseManager.leftPressed)
					if( Mat.isInRange(mx, x, width ) && Mat.isInRange(my, dy-g.fontSize, dy) ) {
						// Load a show table data, with quick edits
						selectedDatabase = db;
						selectedTable = key;
						verticalScroll = 0;
						horizontalScroll = 0;
					}
				
				
				g.drawOutlinedString(key, dx, dy);
			}
		}
		
	}
	
	public void renderTableData(Graphics g, SQLDatabase db, String tbn) {
		int z = 1; // This value stores the amount of rows printed to the screen, this is used for drawing reasons
		double headNameHeight = g.fontSize * 1.5;
		// Load and show tables data
		SQLTable table = db.tables.get(tbn);
		ArrayList<String> cn = table.getColumnNames();
		
		// Generate space amount for columns
		int[] spaceForColumns = new int[table.getAmountOfColumns()];
		
		for(int r=0;r<table.getAmountOfRows();r++)
			for(int c=0;c<table.getAmountOfColumns();c++) {
				// Store value to prevent calculating twice
				int len = table.getElement(r, c).length() + 2; // Add extra for space between, so the columns are not touching
				if( spaceForColumns[c] <  len ) spaceForColumns[c] = len;
			}
		
		for(int x=0;x<cn.size();x++) {
			int len = cn.get(x).length() + 2; // Add extra for space between, so the columns are not touching
			if( spaceForColumns[x] <  len ) spaceForColumns[x] = len;
		}
		
		for(int c=0;c<table.getAmountOfRows();c++) {
			ArrayList<String> row = table.getRow(c);
			String str = "";
			for(int a=0;a<row.size();a++)
				str += String.format("%-"+spaceForColumns[a]+"s", table.getRow(c).get(a));
			
			dx = horizontalScroll + width + 5;
			dy = z++*g.fontSize + verticalScroll + headNameHeight;
			if(dy > 0 && dy - g.fontSize < g.height) // Check if it's rendering on screen
				g.drawOutlinedString(str, dx, dy);
		}
		
		// Draw head names
		String str = "";
		for(int x=0;x<cn.size();x++)
			str += String.format("%-"+spaceForColumns[x]+"s", cn.get(x));
		dx = horizontalScroll + width + 5;
		dy = g.fontSize;
		
		g.setColor(255,255,255);
		g.drawLine(width, headNameHeight, g.width, headNameHeight);
		
		g.setColor( 0, 0, 0);
		g.fillRect(width, 0, g.width, headNameHeight);
		g.drawOutlinedString(str, dx, dy);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(mx <= width) {
			
		} else {
			if(KeyManager.getKey(KeyEvent.VK_SHIFT)) {
				horizontalScroll -= e.getWheelRotation() * Sqwell.scrollAmount;
				if(horizontalScroll > 0) horizontalScroll = 0;
			} else {
				verticalScroll -= e.getWheelRotation() * Sqwell.scrollAmount;
				if(verticalScroll > 0) verticalScroll = 0;
			}
		}
		
	}
}