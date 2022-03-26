package Main;

import java.text.SimpleDateFormat;
import java.util.Date;

import SQL.SQLClientManager;
import SQL.SQLDatabase;

public class SimpleSQL {
	public static void main(String args[]) throws InterruptedException {
		SQLClientManager cm = new SQLClientManager();
		cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_omegaball") );
		
		cm.executeQuery(cm.get(0), "INSERT INTO User(username, password, cookieValue) VALUES ('FireAtWill', '(RMLfAT;rS4q%5zN', 'sM,8c.[9*xR!=7WzwGYhCJmF4/Jzx~<7');");

	}
}