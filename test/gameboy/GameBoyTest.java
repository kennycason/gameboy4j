package gameboy;

import org.junit.Test;

public class GameBoyTest {

	@Test
	public void test() {
		GameBoy gb = GameBoy.getInstance();
		gb.loadCartridge("src/gameboy/roms/ttt.gb");
		gb.start();
	}

}
