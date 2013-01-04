package gameboy.timer;

/**
 * Clock
 */
public class Clock {

	public long main;

	public long sub;
	
	public long div;
	
	public void reset() {
		main = 0;
		sub = 0;
		div = 0;
	}

}
