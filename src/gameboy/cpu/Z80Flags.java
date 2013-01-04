package gameboy.cpu;

public class Z80Flags {
	


	/**
	 *  Set if the last operation produced a result of 0
	 */
	public static final short ZERO = 0x80;
	
	/**
	 * Set if the last operation was a subtraction
	 */
	public static final short SUBTRACTION = 0x40;
	
	/**
	 * Set if, in the result of the last operation, the lower half of the byte overflowed past 15
	 */
	public static final short HALF_CARRY = 0x20;
	
	/**
	 *  Set if the last operation produced a result over 255 (for additions) or under 0 (for subtractions).
	 */
	public static final short CARRY = 0x10;
	
	private Z80Flags() {
		
	}
}
