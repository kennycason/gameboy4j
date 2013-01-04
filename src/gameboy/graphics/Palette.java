package gameboy.graphics;

public class Palette {

	public int[] bg;
	
	public int[] obj0;
	
	public int[] obj1;
	
	public Palette() {
		bg = new int[4] ;
		obj0 = new int[4];
		obj1 = new int[4];
		reset();
	}
	
	public void reset() {
		for(int i = 0; i < 4; i++) {
			bg[i] = obj0[i] = obj1[i] = 0xFF;
		}
	}
	
}
