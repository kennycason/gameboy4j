package gameboy.gpu;

import gameboy.GameBoy;
import gameboy.graphics.Palette;
import gameboy.graphics.PixelRGBMapper;
import gameboy.graphics.RGB;
import gameboy.graphics.Screen;
import gameboy.mmu.GPUVRAM;
import gameboy.mmu.MMU;
import gameboy.mmu.SPRITEINFORAM;

public class GPU {
	
	private GameBoy gb = GameBoy.getInstance();

	private int mode = 0;

	private long modeClock = 0;

	private int line = 0;
	
	private int scx = 0;
	
	private int scy = 0;
	
	private int[][][] tilemap;
	
	private int bgmap = 0;

	private int bgtile = 0;
	
	private Screen screen;
	
	private PixelRGBMapper rgbMapper;
	
	public GPUVRAM vram;
	
	public SPRITEINFORAM oram;
	
	public Palette palette;

	public GPU() {
		vram = new GPUVRAM();
		oram = new SPRITEINFORAM();
		screen = new Screen();
		rgbMapper = new PixelRGBMapper();
		palette = new Palette();
		tilemap = new int[512][8][8];
		reset();
	}

	public void reset() {
		vram.reset();
		oram.reset();
		palette.reset();
		for(int i = 0; i < 512; i++) {
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					tilemap[i][y][x] = 0;
				}
			}
		}
	}

	public void step() {
		modeClock += gb.z80.r.T;
		switch (mode) {
		// OAM read mode, scanline active
		case 2:
			if (modeClock >= 80) {
				modeClock = 0;
				mode = 3;
			}
			break;
		// VRAM read mode, scanline active // Treat end of mode 3 as end of
		// scanline
		case 3:
			if (modeClock >= 172) {

				// Enter hblank
				modeClock = 0;
				mode = 0;

				// Write a scanline to the framebuffer
				renderScan();
			}
			break;
		// Hblank // After the last hblank, push the screen data to canvas
		case 0:
			if (modeClock >= 204) {

				// Enter hblank
				modeClock = 0;
				line++;
				if (line == 143) {
					// Enter vblank
					mode = 1;
					putImageData(screen, 0, 0);
				} else {
					mode = 2;
				}
				// Write a scanline to the framebuffer
				renderScan();
			}
			break;
		// Vblank (10 lines)
		case 1:
			if (modeClock >= 456) {
				modeClock = 0;
				line++;
				if (line > 153) {
					// Restart scanning modes
					mode = 2;
					line = 0;
				}
			}
			break;
		}

	}

	public void updateTile(int address, int val) {
		// Get the "base addressess" for this tile row 
		// TODO is this necessary?
		address &= 0x1FFE;
		
		int tile = (address >> 4) & 0x1FF;
		int y = (address >> 1) & 0xF;
		
		int sx;
		for(int x = 0; x < 8; x++) {
			// Find bit index for this pixel 
			sx = 1 << (7-x);
			System.out.println("tile: " + tile + " x: " + x + " y: " + y);
			// Update tile set
			tilemap[tile][y][x] = 
					(((gb.mmu.readByte(address) & sx) > 0) ? 1 : 0) + 
					(((gb.mmu.readByte(address + 1) & sx) > 0) ? 2 : 0);
		}
	}
	
	private void putImageData(Screen screen, int x, int y) {

	}

	private void renderScan() {
		// VRAM offset for the tile map 
		int mapoffs = MMU.GPU_VRAM_OFFSET + ((bgmap > 0) ? 0x1C00 : 0x1800); 
		
		// Which line of tiles to use in the map 
		mapoffs += ((line + scy) & 255) >> 3; 
		
		// Which tile to start with in the map line 
		int lineoffs = (scx >> 3); 
		
		// Which line of pixels to use in the tiles 
		int y = (line + scy) & 7; 
		
		// Where in the tileline to start 
		int x = scx & 7; 
		
		// Where to render on the canvas 
		int canvasoffs = line * 160 * 4; 
		
		// Read tile index from the background map 
		RGB color; 
		int tile = gb.mmu.readByte(mapoffs + lineoffs); 
		
		// If the tile data set in use is #1, the // indices are signed; calculate a real tile offset 
		if(bgtile == 1 && tile < 128) {
			tile += 256; 
			for(int i = 0; i < 160; i++) { 
				// Re-map the tile pixel through the palette 
				color = rgbMapper.map(tilemap[tile][y][x]); 
				
				// Plot the pixel to canvas 
				// data[canvasoffs + 0] = color.red(); 
				// data[canvasoffs + 1] = color.green();
				// data[canvasoffs + 2] = color.blue(); 
				// data[canvasoffs + 3] = color[4]; 
				canvasoffs += 4; // When this tile ends, read another 
				x++; 
				if(x == 8) { 
					x = 0; 
					lineoffs = (lineoffs + 1) & 31; 
					tile = gb.mmu.readByte(mapoffs + lineoffs); 
					if(bgtile == 1 && tile < 128) {
						tile += 256; 
					}
				}
			}
		}
	}

	public void updateORAM(int address, int value) {
		// TODO Auto-generated method stub
		
	}

	public int readByte(int address) {
		return 0;
	}

	public void writeByte(int address, int value) {
		// TODO Auto-generated method stub
		
	}

}
