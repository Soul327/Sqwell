package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLDatabase {
	String address, username, password;
	public String database = "everying_main";
	Statement stmt = null;
	Connection con = null;
	
	public Map<String, SQLTable> tables = new HashMap<String, SQLTable>();
	
	public SQLDatabase(String address, String username, String password, String database) {
		this.address = address;
		this.username = username;
		this.password = password;
		this.database = database;
	}
	
	String openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection( "jdbc:mysql://"+address+"/"+database, username, password);
			
			stmt = con.createStatement();
			loadTables();
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		return null;
	}
	
	void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		if(stmt == null || con == null)
			return false;
		return true;
	}
	
	public void loadTables() {
		try {
			ResultSet rs = stmt.executeQuery( "SHOW TABLES" );
			while (rs.next()) {
				tables.put(rs.getString(1), new SQLTable(rs.getString(1)));
			}
			for(String key:tables.keySet()) loadTable( key );
		} catch (SQLException e) {}
	}
	public void loadTable(String tableName) {
		
		try {
			System.out.println( tableName );
			SQLTable table = new SQLTable(tableName);
			
			String query = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery( query );
			while (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();

				int columnsNumber = rsmd.getColumnCount();
				ArrayList<String> li = new ArrayList<String>();
				int x = 1;
				for(;columnsNumber > 0;columnsNumber--) { // Loop runs until an error is reached
					String str = rs.getString( x );
					li.add(str);
					x++;
				}
				table.data.add(li);
			}
			getHeadNames( table );
			tables.put(tableName, table);
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	//This will get the names of each column and return them as an array list
	void getHeadNames(SQLTable table) {
		ArrayList<String> names = new ArrayList<String>();
		try {
			String query = "DESCRIBE  "+table.name;
			ResultSet rs = stmt.executeQuery( query );
			while (rs.next())
				try {
					names.add( rs.getString( 1 ) );
				} catch(Exception e) { break; }
		} catch (SQLException e1) {}
		table.columnNames = names;
	}
}
