package SQL;

import java.util.ArrayList;

public class SQLTable {
	String name;
	ArrayList<String> columnNames = new ArrayList<String>();
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	public int getAmountOfRows() { return data.size(); }
	public int getAmountOfColumns() { 
//		return (data != null && data.size() > 0)? data.get(0).size() : 0;
		return columnNames.size();
	}
	
	public SQLTable(String name) {
		this.name = name;
	}
	
	public ArrayList<String> getRow(int x) {
		return data.get(x);
	}
	public String getElement(int row, int column) {
		if(data.get(row).get(column) == null) return "NULL";
		
		if(data == null || data.get(row) == null || data.get(row).get(column) == null) {
			System.err.println( "Row:"+row + " Column:"+ column );
			if( data == null ) System.err.println(" Data = null");
			if( data.get(row) == null ) System.err.println(" Data.get("+row+") = null");
			if( data.get(row).get(column) == null ) System.err.println(" Data.get("+row+").get("+column+") = null");
			return "";
		}
		return data.get(row).get(column);
	}
	
	
	
	public String getUniqueKey() {
		return "<UNIQUE KEY>";
	}
	
	public ArrayList<String> getColumnNames() {
		return columnNames;
	}
}
