package Main;

import java.text.SimpleDateFormat;
import java.util.Date;

import SQL.SQLClientManager;
import SQL.SQLDatabase;

public class SimpleSQL {
	public static void main(String args[]) throws InterruptedException {
		SQLClientManager cm = new SQLClientManager();
		cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_updawg") );
		
		cm.executeQuery(cm.get(0), "CREATE TABLE Temperature (PingingAddress varchar(255), Temp double, DT bigint);");
//		cm.executeQuery(cm.get(0), "DROP TABLE Temperature;");
		
		Date now = new Date();
		long ut3 = now.getTime() / 1000L;
		System.out.println(ut3);
		
		long unixSeconds = ut3;
		// convert seconds to milliseconds
		Date date = new java.util.Date(unixSeconds*1000L); 
		// the format of your date
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); 
		// give a timezone reference for formatting (see comment at the bottom)
//		sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
		String formattedDate = sdf.format(date);
		System.out.println(formattedDate);
	}
}