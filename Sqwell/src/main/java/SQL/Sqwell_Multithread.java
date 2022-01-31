package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sqwell_Multithread {
	
	class Database {
		String address = "162.144.12.171";
		String username = "everying_idiot";
		String password = "password";
		String name = "everying_arayna";
		
		Statement stmt = null;
		Connection con = null;
		
		Map<String, Table> table_Map = new HashMap<String, Table>();
		
		public Database(String name, String address, String username, String password) {
			this.name = name;
			this.address = address;
			this.username = username;
			this.password = password;
		}
		
		// Start SQL Connection
		public String openConnection() {
			System.out.println("Opening connection");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection( "jdbc:mysql://"+address+"/"+name, username, password);
				
				stmt = con.createStatement();
			} catch (Exception e) { return e.getLocalizedMessage(); }
			return null;
		}
		
		// Closes SQL Connection
		public void closeConnection() {
			System.out.println("Closing SQL conection");
			try {
				con.close();
			} catch (SQLException e) { e.printStackTrace(); }
		}
	}
	class Table {
		ArrayList<Row> rows = new ArrayList<Row>();
	}
	class Row {
		ArrayList<String> items = new ArrayList<String>();
	}
	
	class Task {
		Database db;
		String query;
		public Task(Database db, String query) {
			this.db = db;
			this.query = query;
		}
	}
	
	// This will handle all database tasks on a diffrent thread to prevent slowdown on the other part of the system
	// TODO spin up more thread if multiple databases are being acesssed
	class DatabaseThread extends Thread {
		boolean running = true; // States if the program is running, this should never be false
		boolean idle = true;
		
		
		public void run() {
			while(running) {
				
				idle = true;
			}
		}
//		public ArrayList<String> executeQuery(Database db, String query) {
//			
//		}
		public void addDatabase(Database db) {
			databases.add(db);
		}
	}
	ArrayList<Database> databases = new ArrayList<Database>();
	
	public Sqwell_Multithread() {
		// Open Database
		DatabaseThread dbt = new DatabaseThread();
		dbt.start();
		
		// Add database
		databases.add( new Database("everying_arayna", "162.144.12.171", "everying", "password") );
		
//		dbt.executeQuery("everying_arayna","SHOW TABLES");
	}
	
	// Starts the program in a non static way
	public static void main(String[] args) { new Sqwell_Multithread(); }
}
