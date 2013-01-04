package gameboy.timer;

import gameboy.GameBoy;

public class Timer {

	private GameBoy gb = GameBoy.getInstance();

	public int div;

	public int sdiv;

	public int tma;

	public int tima;

	public int tac;

	public Clock clock;
	
	public Timer() {
		clock = new Clock();
	}

	public void reset() {
		div = 0;
		sdiv = 0;
		tma = 0;
		tima = 0;
		tac = 0;
		clock.reset();
	}
	
//	public void check() {
//		if((tac & 4) > 0) {
//			switch(tac & 3) {
//				case 0:
//					threshold = 64;	// 4K
//					break;
//					
//				case 1:
//					threshold = 1;	// 256K
//					break;
//					
//				case 2:
//					threshold = 4;	// 64K
//					break;
//					
//				case 3:
//					threshold = 16;	// 16K
//					break;
//					
//			}
//		}
//	}

	public void step() {
		tima++;
		clock.main = 0;
		if (tima > 0xFF) {
			tima = tma;
			gb.mmu.IF |= 4;
		}
	}

	public void inc() {
		long oldClock = clock.main;
		clock.sub += gb.z80.r.M;
		if (clock.sub > 3) {
			clock.main++;
			clock.sub -= 4;

			clock.div++;
			if (clock.div == 16) {
				clock.div = 0;
				div++;
				div &= 0xFF;
			}
		}

		if ((tac & 4) > 0) {
			switch (tac & 3) {
				case 0:
					if (clock.main >= 64) {
						step();
					}
					break;
				case 1:
					if (clock.main >= 1) {
						step();
					}
					break;
				case 2:
					if (clock.main >= 4) {
						step();
					}
					break;
				case 3:
					if (clock.main >= 16) {
						step();
					}
					break;
			}
		}
	}

	public int readByte(int address) {
		switch (address) {
			case 0xFF04:
				return div;
			case 0xFF05:
				return tima;
			case 0xFF06:
				return tma;
			case 0xFF07:
				return tac;
		}
		return 0;
	}

	public void writeByte(int address, int value) {
		switch (address) {
			case 0xFF04:
				div = 0;
				break;
			case 0xFF05:
				tima = value;
				break;
			case 0xFF06:
				tma = value;
				break;
			case 0xFF07:
				tac = value & 7;
				break;
		}
	}
}
