package gameboy.mmu;


public abstract class AbstractMemoryBank {

	/**
	 * The actual memory
	 */
	protected int[] buffer;
	
	/**
	 * In a memory model, if the starting address is 0xFF, then the addressOffset should be set to 0xFF
	 */
	private int addressOffset;
	
	protected AbstractMemoryBank(int size) {
		this(size, 0x00);
	}
	
	protected AbstractMemoryBank(int size, int addressOffset) {
		try {
		buffer = new int[size];
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.addressOffset = addressOffset;
	}
	
	/**
	 * write 16 bit word
	 * @param address
	 * @param value
	 * @throws AddressOutOfBoundsException
	 */
	public void writeWord(int address, int value) {
		int effectiveAddress = address - addressOffset;
		writeByte(effectiveAddress, value & 0xFF);
		writeByte(effectiveAddress + 1, (value & 0xFF) >> 8);
	}
	
	/**
	 * read 16 bit word
	 * @param address
	 * @return
	 * @throws AddressOutOfBoundsException
	 */
	public int readWord(int address) {
		int effectiveAddress = address - addressOffset;
		return readByte(effectiveAddress) + (readByte(effectiveAddress + 1) << 8);
	}
	
	/**
	 * write byte
	 * @param address
	 * @param value
	 * @throws AddressOutOfBoundsException
	 */
	public void writeByte(int address, int value) {
		int effectiveAddress = address - addressOffset;
		
		//System.err.println(this.getClass());
		if(effectiveAddress < 0 || effectiveAddress >= size()) {
			System.out.println("Address out of range: address: 0x" + Integer.toHexString(effectiveAddress).toUpperCase() + ", range (0x" + Integer.toHexString(addressOffset).toUpperCase() + " - 0x" + Integer.toHexString(addressOffset + size()).toUpperCase()  + ")");
			System.out.println(this.getClass());
			System.exit(1);
		}
		// System.out.println(MMUUtils.memoryToHexString(effectiveAddress, value));
		buffer[effectiveAddress] = value & 0xFF;
	}
	
	/**
	 * read byte
	 * @param address
	 * @return
	 * @throws AddressOutOfBoundsException
	 */
	public int readByte(int address) {
		int effectiveAddress = address - addressOffset;
		//System.err.println(this.getClass());
		if(effectiveAddress < 0 || effectiveAddress >= size()) {
			System.out.println("Address out of range: address: 0x" + Integer.toHexString(effectiveAddress).toUpperCase() + ", range (0x" + Integer.toHexString(addressOffset).toUpperCase() + " - 0x" + Integer.toHexString(addressOffset + size()).toUpperCase()  + ")");
			System.out.println(this.getClass());
			System.exit(1);
		}
		//System.out.println(MMUUtils.memoryToHexString(effectiveAddress, buffer[effectiveAddress] & 0xFF));
		return buffer[effectiveAddress] & 0xFF;
	}
	
	public int size() {
		return buffer.length;
	}
	
	public void reset() {
		for(int i = 0; i < buffer.length; i++) {
			buffer[i] = 0;
		}
	}
	
}
