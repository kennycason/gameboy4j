package gameboy.mmu;

import gameboy.GameBoy;
import gameboy.file.ROMLoader;

import java.io.FileNotFoundException;

public class MMU {
	
	private GameBoy gb = GameBoy.getInstance();

	public BIOS bios;

	public ROM rom;
	
	public int cartType = 0;
	
	public int romBank = 0;
	
	public int ramBank = 0;
	
	public int ramOn = 0;
	
	public int mode = 0;

	public int romOffs = 0x4000;
	
	public int ramOffs = 0x00;
	
	public ERAM eram;

	public WRAM wram;

	public ZRAM zram;

	public int inBios = 1;

	public int IE = 0;

	public int IF = 0;	// Interrupt flags

	/**
	 * [0000-3FFF] Cartridge ROM, bank 0: The first 16,384 bytes of the
	 * cartridge program are always available at this point in the memory map.
	 * 
	 * Special circumstances apply: [0000-00FF] BIOS: When the CPU starts up, PC
	 * starts at 0000h, which is the start of the 256-byte GameBoy BIOS code.
	 * Once the BIOS has run, it is removed from the memory map, and this area
	 * of the cartridge rom becomes addressable. [0100-014F] Cartridge header:
	 * This section of the cartridge contains data about its name and
	 * manufacturer, and must be written in a specific format.
	 */
	public static final int ROM_BANK_0_OFFSET = 0x0000;
	public static final int ROM_BANK_0_END = 0x3FFF;

	public static final int BIOS_OFFSET = 0x0000;
	public static final int BIOS_END = 0x00FF;

	public static final int CARTRIDGE_HEADER_OFFSET = 0x0100;
	public static final int CARTRIDGE_HEADER_END = 0x014F;

	/**
	 * [4000-7FFF] Cartridge ROM, other banks: Any subsequent 16k "banks" of the
	 * cartridge program can be made available to the CPU here, one by one; a
	 * chip on the cartridge is generally used to switch between banks, and make
	 * a particular area accessible. The smallest programs are 32k, which means
	 * that no bank-selection chip is required.
	 */
	public static final int ROM_BANK_1_OFFSET = 0x4000;
	public static final int ROM_BANK_1_END = 0x7FFF;

	/**
	 * [8000-9FFF] Graphics RAM: Data required for the backgrounds and sprites
	 * used by the graphics subsystem is held here, and can be changed by the
	 * cartridge program. This region will be examined in further detail in part
	 * 3 of this series.
	 * 
	 * [8000-87FF] Tile set #1: tiles 0-127 [8800-8FFF] Tile set #1: tiles
	 * 128-255 Tile set #0: tiles -1 to -128 [9000-97FF] Tile set #0: tiles
	 * 0-127 [9800-9BFF] Tile map #0 [9C00-9FFF] Tile map #1
	 */
	public static final int GPU_VRAM_OFFSET = 0x8000;
	public static final int GPU_VRAM_END = 0x9FFF;

	/**
	 * [A000-BFFF] Cartridge (External) RAM: There is a small amount of
	 * writeable memory available in the GameBoy; if a game is produced that
	 * requires more RAM than is available in the hardware, additional 8k chunks
	 * of RAM can be made addressable here.
	 */
	public static final int EXT_RAM_OFFSET = 0xA000;
	public static final int EXT_RAM_END = 0xBFFF;

	/**
	 * [C000-DFFF] Working RAM: The GameBoy's internal 8k of RAM, which can be
	 * read from or written to by the CPU.
	 */
	public static final int WORKING_RAM_OFFSET = 0xC000;
	public static final int WORKING_RAM_END = 0xDFFF;

	/**
	 * [E000-FDFF] Working RAM (shadow): Due to the wiring of the GameBoy
	 * hardware, an exact copy of the working RAM is available 8k higher in the
	 * memory map. This copy is available up until the last 512 bytes of the
	 * map, where other areas are brought into access.
	 */
	public static final int WORKING_RAM_SHADOW_OFFSET = 0xE000;
	public static final int WORKING_RAM_SHADOW_END = 0xFDFF;

	/**
	 * [FE00-FE9F] Graphics: sprite information: Data about the sprites rendered
	 * by the graphics chip are held here, including the sprites' positions and
	 * attributes.
	 */
	public static final int GRAPHICS_SPRITE_OFFSET = 0xFE00;
	public static final int GRAPHICS_SPRITE_END = 0xFE9F;

	/**
	 * [FF00-FF7F] Memory-mapped I/O: Each of the GameBoy's subsystems
	 * (graphics, sound, etc.) has control values, to allow programs to create
	 * effects and use the hardware. These values are available to the CPU
	 * directly on the address bus, in this area.
	 */
	public static final int MEM_MAPPED_IO_OFFSET = 0xFF00;
	public static final int MEM_MAPPED_IO_END = 0xFF7F;

	/**
	 * [FF80-FFFF] Zero-page RAM: A high-speed area of 128 bytes of RAM is
	 * available at the top of memory. Oddly, though this is "page" 255 of the
	 * memory, it is referred to as page zero, since most of the interaction
	 * between the program and the GameBoy hardware occurs through use of this
	 * page of memory.
	 */
	public static final int ZERO_PAGE_RAM_OFFSET = 0xFF80;
	public static final int ZERO_PAGE_RAM_END = 0xFFFF;

	public MMU() {
		inBios = 0;
		bios = new BIOS();
		rom = new ROM();
		eram = new ERAM();
		wram = new WRAM();
		zram = new ZRAM();
	}

	public void reset() {
		inBios = 1;
		IE = 0;
		IF = 0;
		
		cartType = 0;
		romBank = 0;
		ramBank = 0;
		ramOn = 0;
		mode = 0;
		romOffs = 0x4000;
		ramOffs = 0;
		
		bios.reset();
		rom.reset();
		eram.reset();
		wram.reset();
		zram.reset();
		System.out.println("MMU Reset");
	}

	public int readByte(int address) {
		//System.out.println("read: " + Integer.toHexString(address).toUpperCase());
		switch (address & 0xF000) {
		// ROM Bank 0
		case 0x0000:
			if (inBios == 1) {
				if (address < 0x0100) {
					return bios.readByte(address);
				} else if (gb.z80.r.PC == 0x0100) {
					inBios = 0;
					System.out.println("MMU Leaving BIOS");
				}
			} else {
				return rom.readByte(address);
			}
		case 0x1000:
		case 0x2000:
		case 0x3000:
			return rom.readByte(address);

			// ROM bank 1
		case 0x4000:
		case 0x5000:
		case 0x6000:
		case 0x7000:
			 // return rom.readByte(address); // TODO which is correct
			return rom.readByte(romOffs + (address & 0x3FFF));

			// VRAM
		case 0x8000:
		case 0x9000:
			return gb.gpu.vram.readByte(address & 0x1FFF);

			// External RAM
		case 0xA000:
		case 0xB000:
			 // return eram.readByte(address & 0x1FFF); // TODO which is correct
			return eram.readByte(ramOffs + (address & 0x1FFF));

			// Work RAM and echo
		case 0xC000:
		case 0xD000:
		case 0xE000:
			return wram.readByte(address & 0x1FFF);

			// Everything else
		case 0xF000:
			switch (address & 0x0F00) {
				// Echo RAM
				case 0x000:
				case 0x100:
				case 0x200:
				case 0x300:
				case 0x400:
				case 0x500:
				case 0x600:
				case 0x700:
				case 0x800:
				case 0x900:
				case 0xA00:
				case 0xB00:
				case 0xC00:
				case 0xD00:
					return wram.readByte(address & 0x1FFF);

				// OAM
				case 0xE00:
					return ((address & 0xFF) < 0xA0) ? gb.gpu.oram.readByte(address & 0xFF) : 0;

				// Zeropage RAM, I/O, interrupts
				case 0xF00:
					if(address == 0xFFFF) {
						return IE;
					}else if (address > 0xFF7F) {
						return zram.readByte(address & 0x7F);
					} else {
						switch (address & 0xF0) {
							case 0x00:
								switch (address & 0xF) {
									case 0:
										return gb.keyboard.readByte(); // JOYP
									case 4:
									case 5:
									case 6:
									case 7:
										return gb.timer.readByte(address);
										
									case 15: 
										return IF;	// interrupt flags
									default:
										return 0;
								}
							case 0x10:
							case 0x20:
							case 0x30:
								return 0;
								
							case 0x40:
							case 0x50:
							case 0x60:
							case 0x70:
								return gb.gpu.readByte(address);
						}
					}
			}
		}
		return -1;
	}
	
	public int readWord(int address) {
		return readByte(address) + readByte(address + 1) << 8;
	}

	public void writeByte(int address, int value) {
		// System.out.println(MMUUtils.memoryToHexString(address, value));
		switch (address & 0xF000) {
	     	// ROM bank 0
	      	// MBC1: Turn external RAM on
			case 0x0000:
			case 0x1000:
				switch(cartType) {
					case 1:
						ramOn = (value & 0xF) == 0xA ? 1 : 0;
						break;
				}
				break;
			case 0x2000:
			case 0x3000:
				switch(cartType) {
					case 1:
						romBank &= 0x60;
						value &= 0x1F;
						if(value == 0) {
							value = 1;
						}
						romBank |= value;
						romOffs = romBank*0x4000;
						break;
					}
					break;
	
			// ROM bank 1
			// MBC1: RAM bank switch
			case 0x4000:
			case 0x5000:
				switch(cartType) {
				case 1:
					if(mode > 0) {
						ramBank = value & 3;
						ramOffs = ramBank * 0x2000;
					} else {
						romBank &= 0x1F;
						romBank |= ((value & 3) << 5);
						romOffs = romBank * 0x4000;
					}
					break;
				}
				break;

			case 0x6000:
			case 0x7000:
				switch(cartType) {
				case 1:
					mode = value & 1;
					break;
				}
				break;
	
			// VRAM
			case 0x8000:
			case 0x9000:
				gb.gpu.vram.writeByte(ramOffs + (address & 0x1FFF), value);
				//gb.gpu.updateTile(address & 0x1FFF, value);
				break;
	
			// External RAM
			case 0xA000:
			case 0xB000:
				eram.writeByte(address & 0x1FFF, value);
				break;
	
			// Work RAM and echo
			case 0xC000:
			case 0xD000:
			case 0xE000:
				wram.writeByte(address & 0x1FFF, value);
				break;
	
			// Everything else
			case 0xF000:
				switch (address & 0x0F00) {
					// Echo RAM
					case 0x000:
					case 0x100:
					case 0x200:
					case 0x300:
					case 0x400:
					case 0x500:
					case 0x600:
					case 0x700:
					case 0x800:
					case 0x900:
					case 0xA00:
					case 0xB00:
					case 0xC00:
					case 0xD00:
						wram.writeByte(address & 0x1FFF, value);
						break;
		
					// OAM
					case 0xE00:
						if ((address & 0xFF) < 0xA0)
							gb.gpu.oram.writeByte(address & 0xFF, value);
							gb.gpu.updateORAM(address, value);
						break;
		
					// Zeropage RAM, I/O
					case 0xF00:
						if(address == 0xFFFF) {
							IE = value;
						} else if (address > 0xFF7F) {
							zram.writeByte(address & 0x7F, value);
						} else {
							switch (address & 0xF0) {
								case 0x00:
									switch(address & 0xF) {
										case 0:
											gb.keyboard.writeByte(value);
											break;
										case 4: 
										case 5:
										case 6: 
										case 7: 
											gb.timer.writeByte(address, value);
											break;
										case 15:
											IF = value;
											break;
									}
									break;
									
								case 0x10: 
								case 0x20: 
								case 0x30:
									break;

								case 0x40: 
								case 0x50: 
								case 0x60: 
								case 0x70:
									gb.gpu.writeByte(address, value);
									break;
							}
						}
				}
				break;
		}
	}
	
	public void writeWord(int address, int value) {
		writeByte(address, value & 0xFF);
		writeByte(address + 1, (value & 0xFF) >> 8);
	}

	public void loadROM(String romFileName) {
		ROMLoader loader = new ROMLoader();
		try {
			byte[] bytes = loader.load(romFileName);
			System.out.println("ROM File " + bytes.length + " bytes");
			for (int i = 0; i < bytes.length; i++) {
				rom.writeByte(i, bytes[i]);
			}
			cartType = rom.readByte(0x0147);
			System.out.println("ROM loaded: " + bytes.length +  " bytes.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("ROM file not found, stopping");
			System.exit(1);
		}
	}

}
