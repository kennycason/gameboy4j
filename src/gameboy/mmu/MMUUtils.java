package gameboy.mmu;

public class MMUUtils {

	public static String memoryToHexString(int address, int value) {
		return Integer.toHexString(address).toUpperCase() + "\t" + Integer.toHexString(value).toUpperCase() ;
	}
}
