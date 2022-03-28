package SQL;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLClientManager {
	
	public ArrayList<SQLDatabase> databases = new ArrayList<SQLDatabase>();
	
	public void openConnection() {
		for(int z=0;z<databases.size();z++)
			openConnection(z);
	}
	public void openConnection(int x) { openConnection(databases.get(x)); }
	public void openConnection(final SQLDatabase database) {
//		(new Thread() {
//			public void run() {
//			}
//		}).start();
		database.openConnection();
	}
	public void executeQuery(SQLDatabase database, String query) {
		try {
			if(database.stmt == null)
				openConnection(database);
			database.stmt.execute(query);
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	public void dump() {
		databases = new ArrayList<SQLDatabase>();
	}
	
	public void add(SQLDatabase db) { databases.add( db ); }
	public SQLDatabase get(int x) { return databases.get( x ); }
}