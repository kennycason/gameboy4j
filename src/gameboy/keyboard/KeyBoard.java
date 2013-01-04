package gameboy.keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoard extends KeyAdapter {

	public int keys[] = new int[] { 0x0F, 0x0F };

	public int colidX = 0;

	public KeyBoard() {
	}

	public void reset() {
		keys[0] = 0x0F;
		keys[1] = 0x0F;
		colidX = 0;
	}

	public void writeByte(int value) {
		colidX = value & 0x30;
		
	}

	public int readByte() {
		 switch(colidX) {
		      case 0x00: 
		    	  return 0x00; 
		      case 0x10: 
		    	  return keys[0]; 
		      case 0x20: 
		    	  return keys[1]; 
		      default: 
		    	  return 0x00; 
		 }
	}

	/**
	 * Notification from AWT that a key has been pressed. Note that a key being
	 * pressed is equal to being pushed down but *NOT* released. Thats where
	 * keyTyped() comes in.
	 * 
	 * @param e
	 *            The details of the key that was pressed
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 39:
				keys[1] &= 0xE;
				break;
			case 37:
				keys[1] &= 0xD;
				break;
			case 38:
				keys[1] &= 0xB;
				break;
			case 40:
				keys[1] &= 0x7;
				break;
			case 90:
				keys[0] &= 0xE;
				break;
			case 88:
				keys[0] &= 0xD;
				break;
			case 32:
				keys[0] &= 0xB;
				break;
			case 13:
				keys[0] &= 0x7;
				break;
		}
	}

	/**
	 * Notification from AWT that a key has been released.
	 * 
	 * @param e
	 *            The details of the key that was released
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 39:
				keys[1] |= 0x1;
				break;
			case 37:
				keys[1] |= 0x2;
				break;
			case 38:
				keys[1] |= 0x4;
				break;
			case 40:
				keys[1] |= 0x8;
				break;
			case 90:
				keys[0] |= 0x1;
				break;
			case 88:
				keys[0] |= 0x2;
				break;
			case 32:
				keys[0] |= 0x5;
				break;
			case 13:
				keys[0] |= 0x8;
				break;
		}

	}

	/**
	 * Notification from AWT that a key has been typed. Note that typing a key
	 * means to both press and then release it.
	 * 
	 * @param e
	 *            The details of the key that was typed.
	 */
	public void keyTyped(KeyEvent e) {
	}

}
