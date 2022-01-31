package SQL;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Events.KeyTypedEvent;
import Misc.KeyManager;
import Rendering.Graphics;

public class Editor implements KeyTypedEvent {
	static int typeFlicker = 0, typeRow = 0;
	static ArrayList<String> editor = new ArrayList<String>();
	
	public Editor() {
		KeyManager.addKeyTypedEvent(this);
		System.out.println( "Added" );
	}
	
	public void renderEditor(Graphics g) {
		int widthOfNums = 0;
		for(int z=1;z<=editor.size();z++) {
			String s = ""+z;
			if(widthOfNums < g.getStringLength(s))
				widthOfNums = g.getStringLength(s+" ");
			
			s = s+" "+editor.get(z-1);
			
			if(z-1 == typeRow) {
				if(typeFlicker < 30) s += "_";
				if(typeFlicker > 60) typeFlicker = 0;
				typeFlicker++;
			}
			
			g.drawOutlinedString(s, 0, g.fontSize*z);
		}
	}

	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		String line = editor.get(typeRow);
		
		if(c == 8) { // Backspace
			if(line.length() > 0)
				editor.set( typeRow, line.substring(0,line.length() - 1) );
			else if(typeRow > 0) {
				editor.remove(typeRow--);
			}
			return;
		}
		if(c == 10) { // \n
			editor.add("");
			typeRow += 1;
			return;
		}
		editor.set( typeRow, editor.get(typeRow) + c );
		System.out.println( (int)c );
	}
}
