package gameboy;

import gameboy.cpu.Z80;
import gameboy.gpu.GPU;
import gameboy.graphics.Screen;
import gameboy.keyboard.KeyBoard;
import gameboy.mmu.MMU;
import gameboy.timer.Timer;

public class GameBoy {
	
	private static GameBoy INSTANCE = null;
	
	public Z80 z80;
	
	public MMU mmu;
	
	public GPU gpu;
	
	public KeyBoard keyboard;
	
	public Timer timer;
	
	private String romFileName;
	
	private boolean romLoaded = false;
	
	private Screen screen;
	
	public static void main(String[] args) {
		GameBoy gb = GameBoy.getInstance();
		gb.loadCartridge("src/gameboy/roms/pokemon_red.gb");
		gb.start();
	}
	
	private GameBoy() {
	}
	
	public static GameBoy getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new GameBoy();
			INSTANCE.mmu = new MMU();
			INSTANCE.gpu = new GPU();
			INSTANCE.z80 = new Z80();
			INSTANCE.keyboard = new KeyBoard();
			INSTANCE.timer = new Timer();
		}
		return INSTANCE;
	}
	
	public void reset() {
		mmu.reset();
		gpu.reset();
		z80.reset();
		keyboard.reset();
		timer.reset();
	}
	
	public void loadCartridge(String romFileName) {
		this.romFileName = romFileName;
		this.romLoaded = true;
		mmu.loadROM(romFileName);
	}
	
	public void start() {
		if(romLoaded) {
			screen = new Screen();
			screen.init();
			z80.dispatch();
		} else {
			System.err.println("ROM Not loaded!");
		}
		System.out.println("execution finished");
	}
	
	public String getLoadedRom() {
		if(!romLoaded) {
			return "No ROM Loaded!";
		}
		return romFileName;
	}
	
}
