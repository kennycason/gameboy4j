package gameboy.graphics;

public class RGB {

	private int rgb;
	
	public RGB(int red, int green, int blue) {
		rgb = red;
		rgb = (rgb << 8) + green;
		rgb = (rgb << 8) + blue;
	}
	
	public int red() {
		return (rgb >> 16) & 0xFF;
	}
	
	public int green() {
		return (rgb >> 8) & 0xFF;
	}
	
	public int blue() {
		return rgb & 0xFF;
	}
	
}
