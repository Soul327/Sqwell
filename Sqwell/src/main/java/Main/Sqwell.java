package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Events.MouseWheelMovedEvent;
import Events.SimpleWindowEvent;
import Misc.KeyManager;
import Misc.MouseManager;
import Misc.SimpleWindow;
import Rendering.Graphics;
import SQL.SQLClientManager;
import SQL.SQLDatabase;
import SQL.SQLTable;

public class Sqwell implements SimpleWindowEvent, MouseWheelMovedEvent{
	
	SimpleWindow window;
	SQLClientManager cm = new SQLClientManager();
	Viewer viewer = new Viewer(cm);
//	public static int bufferedImageType = BufferedImage.TYPE_BYTE_BINARY;
	public static int bufferedImageType = 0;
	public static Font font = new Font("Monospaced", Font.PLAIN, 15);
	
	public Sqwell() {
		// Setup window
		window = new SimpleWindow();
		window.addSimpleWindowEvent(this);
		window.name = "Sqwell - An SQL client";
		MouseManager.addMouseWheelEvent(this);
		window.maxFPS = 30;
		window.start();
		
		loadConfig();
	}
	
	public void tar(Graphics g) {
		g.setFont( Sqwell.font );
		if(KeyManager.keyRelease(KeyEvent.VK_F5)) loadConfig();
		viewer.render(g, 0, 0);
		
		g.setColor(255,255,255);
		g.drawLine(viewer.width, 0, viewer.width, g.height);
	}
	
	static double scrollAmount = 5;
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		viewer.mouseWheelMoved(e);
	}
	
	/* Load config file, this may be a lot of code */
	public void loadConfig() {
		// Set all values to default
//		cm = new SQLClientManager();
		cm.dump();
		
		// Setup databases
	//	cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_main") );
	//	cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_updawg") );
	//	cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_generators") );
	//	cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_arayna") );
		cm.add( new SQLDatabase("162.144.12.171", "everying_idiot", "password", "everying_omegaball") );
		
		cm.openConnection();
	}
	
	public static void main(String args[]) { new Sqwell(); }
}
