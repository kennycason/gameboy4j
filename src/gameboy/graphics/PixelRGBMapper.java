package gameboy.graphics;

public class PixelRGBMapper {
	
	private RGB OFF = new RGB(255, 255, 255);
	
	private RGB LO = new RGB(192, 192, 192);
	
	private RGB HI = new RGB(96, 96, 96);
	
	private RGB ON = new RGB(0, 0, 0);

	public RGB map(int percent) {
		if(percent == 0) {
			return OFF;
		} else if(percent == 33) {
			return LO;
		} else if(percent == 66) {
			return HI;
		} else if(percent == 1) {
			return ON;
		} else {
			return OFF;
		}
	}
	
}
