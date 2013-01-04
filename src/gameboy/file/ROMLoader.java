package gameboy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ROMLoader {

	public byte[] load(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		byte[] b = new byte[(int) f.length()];

		try {
			FileInputStream fis = new FileInputStream(f);
			fis.read(b);
			return b;
		} catch (Exception e) {
			return null;
		}
	}

}
