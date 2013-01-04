package gameboy.cpu;

import gameboy.GameBoy;

/**
 * emulation of the GameBoy's modified Zilog Z80
 * 
 * @author kenny Translated From http://imrannazar.com/content/files/jsgb.z80.js
 * 
 */
public class Z80 {
	
	private GameBoy gb = GameBoy.getInstance();
	
	public Z80Registers r;

	public Z80Registers rsv;

	public Z80Clock clock;

	public int HALT = 0;

	public int STOP = 0;
	
	private long lastTimeMeasured = System.currentTimeMillis();
	private long lastNumberOfCommandsExecuted = 0;
	private long totalCommandsExecuted = 0;

	public Z80() {
		r = new Z80Registers();
		rsv = new Z80Registers();
		clock = new Z80Clock();
		reset();
	}

	public void reset() {
		r.clear();
		rsv.clear();
		clock.reset();
	}

	public void exec() {
		// Run execute for this instruction
		//r.R = (r.R + 1) & 127;
		int op = gb.mmu.readByte(r.PC++);
		/*System.out.println("PC: " + r.PC + ":\t" + "SP: 0x"
				+ Integer.toHexString(r.SP).toUpperCase() + ":\t" + "OP: 0x"
				+ Integer.toHexString(op).toUpperCase());*/
		opMap(op); // Fetch instruction & Dispatch
		r.PC &= 0xFFFF; // Mask PC to 16 bits
		clock.M += r.M; // Add time to CPU clock
		
		// Update timer
		gb.timer.inc();
		r.M = 0;
		r.T = 0;
		
		// If IME is on, and some interrupts are enabled in IE, and 
		// an interrupt flag is set, handle the interrupt
		if(r.IME > 0 && gb.mmu.IE > 0&& gb.mmu.IF > 0) {
			// Mask off ints that aren't enabled
			int ifired = gb.mmu.IE & gb.mmu.IF;
			if((ifired & 0x01) > 0) {
				gb.mmu.IF &= 0xFE; // (0xFF - 0x01);
				RST40();
			}
		}
		clock.M += r.M;
		
		// Update timer again, in case a RST occurred
		gb.timer.inc();
		
		totalCommandsExecuted++;
		if(System.currentTimeMillis() - lastTimeMeasured > 1000) {
			lastTimeMeasured = System.currentTimeMillis();
			System.out.println((totalCommandsExecuted - lastNumberOfCommandsExecuted) + " ops/sec");
			lastNumberOfCommandsExecuted = totalCommandsExecuted;
		}
	}

	/**
	 * the main dispatch loop
	 */
	public void dispatch() {
		while (true) {
			exec();
			gb.gpu.step();
		}
	}

	/**
	 * Z80 Instructions
	 */

	/**
	 * Load/Store
	 */
	public void LDrr_bb() {
		r.B = r.B;
		r.M = 1;
	};

	public void LDrr_bc() {
		r.B = r.C;
		r.M = 1;
	}

	public void LDrr_bd() {
		r.B = r.D;
		r.M = 1;
	}

	public void LDrr_be() {
		r.B = r.E;
		r.M = 1;
	}

	public void LDrr_bh() {
		r.B = r.H;
		r.M = 1;
	}

	public void LDrr_bl() {
		r.B = r.L;
		r.M = 1;
	}

	public void LDrr_ba() {
		r.B = r.A;
		r.M = 1;
	}

	public void LDrr_cb() {
		r.C = r.B;
		r.M = 1;
	}

	public void LDrr_cc() {
		r.C = r.C;
		r.M = 1;
	}

	public void LDrr_cd() {
		r.C = r.D;
		r.M = 1;
	}

	public void LDrr_ce() {
		r.C = r.E;
		r.M = 1;
	}

	public void LDrr_ch() {
		r.C = r.H;
		r.M = 1;
	}

	public void LDrr_cl() {
		r.C = r.L;
		r.M = 1;
	}

	public void LDrr_ca() {
		r.C = r.A;
		r.M = 1;
	}

	public void LDrr_db() {
		r.D = r.B;
		r.M = 1;
	}

	public void LDrr_dc() {
		r.D = r.C;
		r.M = 1;
	}

	public void LDrr_dd() {
		r.D = r.D;
		r.M = 1;
	}

	public void LDrr_de() {
		r.D = r.E;
		r.M = 1;
	}

	public void LDrr_dh() {
		r.D = r.H;
		r.M = 1;
	}

	public void LDrr_dl() {
		r.D = r.L;
		r.M = 1;
	}

	public void LDrr_da() {
		r.D = r.A;
		r.M = 1;
	}

	public void LDrr_eb() {
		r.E = r.B;
		r.M = 1;
	}

	public void LDrr_ec() {
		r.E = r.C;
		r.M = 1;
	}

	public void LDrr_ed() {
		r.E = r.D;
		r.M = 1;
	}

	public void LDrr_ee() {
		r.E = r.E;
		r.M = 1;
	}

	public void LDrr_eh() {
		r.E = r.H;
		r.M = 1;
	}

	public void LDrr_el() {
		r.E = r.L;
		r.M = 1;
	}

	public void LDrr_ea() {
		r.E = r.A;
		r.M = 1;
	}

	public void LDrr_hb() {
		r.H = r.B;
		r.M = 1;
	}

	public void LDrr_hc() {
		r.H = r.C;
		r.M = 1;
	}

	public void LDrr_hd() {
		r.H = r.D;
		r.M = 1;
	}

	public void LDrr_he() {
		r.H = r.E;
		r.M = 1;
	}

	public void LDrr_hh() {
		r.H = r.H;
		r.M = 1;
	}

	public void LDrr_hl() {
		r.H = r.L;
		r.M = 1;
	}

	public void LDrr_ha() {
		r.H = r.A;
		r.M = 1;
	}

	public void LDrr_lb() {
		r.L = r.B;
		r.M = 1;
	}

	public void LDrr_lc() {
		r.L = r.C;
		r.M = 1;
	}

	public void LDrr_ld() {
		r.L = r.D;
		r.M = 1;
	}

	public void LDrr_le() {
		r.L = r.E;
		r.M = 1;
	}

	public void LDrr_lh() {
		r.L = r.H;
		r.M = 1;
	}

	public void LDrr_ll() {
		r.L = r.L;
		r.M = 1;
	}

	public void LDrr_la() {
		r.L = r.A;
		r.M = 1;
	}

	public void LDrr_ab() {
		r.A = r.B;
		r.M = 1;
	}

	public void LDrr_ac() {
		r.A = r.C;
		r.M = 1;
	}

	public void LDrr_ad() {
		r.A = r.D;
		r.M = 1;
	}

	public void LDrr_ae() {
		r.A = r.E;
		r.M = 1;
	}

	public void LDrr_ah() {
		r.A = r.H;
		r.M = 1;
	}

	public void LDrr_al() {
		r.A = r.L;
		r.M = 1;
	}

	public void LDrr_aa() {
		r.A = r.A;
		r.M = 1;
	}

	public void LDrHLm_b() {
		r.B = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_c() {
		r.C = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_d() {
		r.D = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_e() {
		r.E = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_h() {
		r.H = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_l() {
		r.L = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDrHLm_a() {
		r.A = gb.mmu.readByte((r.H << 8) + r.L);
		r.M = 2;
	}

	public void LDHLmr_b() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.B);
		r.M = 2;
	}

	public void LDHLmr_c() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.C);
		r.M = 2;
	}

	public void LDHLmr_d() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.D);
		r.M = 2;
	}

	public void LDHLmr_e() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.E);
		r.M = 2;
	}

	public void LDHLmr_h() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.H);
		r.M = 2;
	}

	public void LDHLmr_l() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.L);
		r.M = 2;
	}

	public void LDHLmr_a() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.A);
		r.M = 2;
	}

	public void LDrn_b() {
		r.B = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_c() {
		r.C = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_d() {
		r.D = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_e() {
		r.E = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_h() {
		r.H = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_l() {
		r.L = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDrn_a() {
		r.A = gb.mmu.readByte(r.PC);
		r.PC++;
		r.M = 2;
	}

	public void LDHLmn() {
		gb.mmu.writeByte((r.H << 8) + r.L, gb.mmu.readByte(r.PC));
		r.PC++;
		r.M = 3;
	}

	public void LDBCmA() {
		gb.mmu.writeByte((r.B << 8) + r.C, r.A);
		r.M = 2;
	}

	public void LDDEmA() {
		gb.mmu.writeByte((r.D << 8) + r.E, r.A);
		r.M = 2;
	}

	public void LDmmA() {
		gb.mmu.writeByte(gb.mmu.readWord(r.PC), r.A);
		r.PC += 2;
		r.M = 4;
	}
	
	public void LDmmSP() {
		gb.mmu.writeByte(gb.mmu.readWord(r.SP), r.A);
		r.PC += 2;
		r.M = 4;	
	}
	
	public void LDABCm() {
		r.A = gb.mmu.readByte((r.B << 8) + r.C);
		r.M = 2;
	}

	public void LDADEm() {
		r.A = gb.mmu.readByte((r.D << 8) + r.E);
		r.M = 2;
	}

	public void LDAmm() {
		r.A = gb.mmu.readByte(gb.mmu.readWord(r.PC));
		r.PC += 2;
		r.M = 4;
	}

	public void LDBCnn() {
		r.C = gb.mmu.readByte(r.PC);
		r.B = gb.mmu.readByte(r.PC + 1);
		r.PC += 2;
		r.M = 3;
	}

	public void LDDEnn() {
		r.E = gb.mmu.readByte(r.PC);
		r.D = gb.mmu.readByte(r.PC + 1);
		r.PC += 2;
		r.M = 3;
	}

	public void LDHLnn() {
		r.L = gb.mmu.readByte(r.PC);
		r.H = gb.mmu.readByte(r.PC + 1);
		r.PC += 2;
		r.M = 3;
	}

	public void LDSPnn() {
		r.SP = gb.mmu.readWord(r.PC);
		r.PC += 2;
		r.M = 3;
	}

	public void LDHLmm() {
		int i = gb.mmu.readWord(r.PC);
		r.PC += 2;
		r.L = gb.mmu.readByte(i);
		r.H = gb.mmu.readByte(i + 1);
		r.M = 5;
	}

	public void LDmmHL() {
		int i = gb.mmu.readWord(r.PC);
		r.PC += 2;
		gb.mmu.writeWord(i, (r.H << 8) + r.L);
		r.M = 5;
	}

	public void LDHLIA() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.A);
		r.L = (r.L + 1) & 255;
		if (r.L == 0)
			r.H = (r.H + 1) & 255;
		r.M = 2;
	}

	public void LDAHLI() {
		r.A = gb.mmu.readByte((r.H << 8) + r.L);
		r.L = (r.L + 1) & 255;
		if (r.L == 0)
			r.H = (r.H + 1) & 255;
		r.M = 2;
	}

	public void LDHLDA() {
		gb.mmu.writeByte((r.H << 8) + r.L, r.A);
		r.L = (r.L - 1) & 255;
		if (r.L == 255)
			r.H = (r.H - 1) & 255;
		r.M = 2;
	}

	public void LDAHLD() {
		r.A = gb.mmu.readByte((r.H << 8) + r.L);
		r.L = (r.L - 1) & 255;
		if (r.L == 255)
			r.H = (r.H - 1) & 255;
		r.M = 2;
	}

	public void LDAIOn() {
		r.A = gb.mmu.readByte(0xFF00 + gb.mmu.readByte(r.PC));
		r.PC++;
		r.M = 3;
	}

	public void LDIOnA() {
		gb.mmu.writeByte(0xFF00 + gb.mmu.readByte(r.PC), r.A);
		r.PC++;
		r.M = 3;
	}

	public void LDAIOC() {
		r.A = gb.mmu.readByte(0xFF00 + r.C);
		r.M = 2;
	}

	public void LDIOCA() {
		gb.mmu.writeByte(0xFF00 + r.C, r.A);
		r.M = 2;
	}

	public void LDHLSPn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		i += r.SP;
		r.H = (i >> 8) & 255;
		r.L = i & 255;
		r.M = 3;
	}

	public void SWAPr_b() {
		int tr = r.B;
		r.B = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.B > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_c() {
		int tr = r.C;
		r.C = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.C > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_d() {
		int tr = r.D;
		r.D = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.D > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_e() {
		int tr = r.E;
		r.E = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.E > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_h() {
		int tr = r.H;
		r.H = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.H > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_l() {
		int tr = r.L;
		r.L = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.L > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void SWAPr_a() {
		int tr = r.A;
		r.A = ((tr & 0xF) << 4) | ((tr & 0xF0) >> 4);
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	/*--- Data processing ---*/
	public void ADDr_b() {
		int a = r.A;
		r.A += r.B;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.B ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_c() {
		int a = r.A;
		r.A += r.C;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.C ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_d() {
		int a = r.A;
		r.A += r.D;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.D ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_e() {
		int a = r.A;
		r.A += r.E;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.E ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_h() {
		int a = r.A;
		r.A += r.H;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.H ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_l() {
		int a = r.A;
		r.A += r.L;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.L ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDr_a() {
		int a = r.A;
		r.A += r.A;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.A ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADDHL() {
		int a = r.A;
		int m = gb.mmu.readByte((r.H << 8) + r.L);
		r.A += m;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ a ^ m) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void ADDn() {
		int a = r.A;
		int m = gb.mmu.readByte(r.PC);
		r.A += m;
		r.PC++;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ a ^ m) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void ADDHLBC() {
		int hl = (r.H << 8) + r.L;
		hl += (r.B << 8) + r.C;
		if (hl > 65535)
			r.F |= 0x10;
		else
			r.F &= 0xEF;
		r.H = (hl >> 8) & 255;
		r.L = hl & 255;
		r.M = 3;
	}

	public void ADDHLDE() {
		int hl = (r.H << 8) + r.L;
		hl += (r.D << 8) + r.E;
		if (hl > 65535)
			r.F |= 0x10;
		else
			r.F &= 0xEF;
		r.H = (hl >> 8) & 255;
		r.L = hl & 255;
		r.M = 3;
	}

	public void ADDHLHL() {
		int hl = (r.H << 8) + r.L;
		hl += (r.H << 8) + r.L;
		if (hl > 65535)
			r.F |= 0x10;
		else
			r.F &= 0xEF;
		r.H = (hl >> 8) & 255;
		r.L = hl & 255;
		r.M = 3;
	}

	public void ADDHLSP() {
		int hl = (r.H << 8) + r.L;
		hl += r.SP;
		if (hl > 65535)
			r.F |= 0x10;
		else
			r.F &= 0xEF;
		r.H = (hl >> 8) & 255;
		r.L = hl & 255;
		r.M = 3;
	}

	public void ADDSPn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.SP += i;
		r.M = 4;
	}

	public void ADCr_b() {
		int a = r.A;
		r.A += r.B;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.B ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_c() {
		int a = r.A;
		r.A += r.C;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.C ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_d() {
		int a = r.A;
		r.A += r.D;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.D ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_e() {
		int a = r.A;
		r.A += r.E;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.E ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_h() {
		int a = r.A;
		r.A += r.H;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.H ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_l() {
		int a = r.A;
		r.A += r.L;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.L ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCr_a() {
		int a = r.A;
		r.A += r.A;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.A ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void ADCHL() {
		int a = r.A;
		int m = gb.mmu.readByte((r.H << 8) + r.L);
		r.A += m;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void ADCn() {
		int a = r.A;
		int m = gb.mmu.readByte(r.PC);
		r.A += m;
		r.PC++;
		r.A += ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A > 255) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void SUBr_b() {
		int a = r.A;
		r.A -= r.B;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.B ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_c() {
		int a = r.A;
		r.A -= r.C;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.C ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_d() {
		int a = r.A;
		r.A -= r.D;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.D ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_e() {
		int a = r.A;
		r.A -= r.E;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.E ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_h() {
		int a = r.A;
		r.A -= r.H;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.H ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_l() {
		int a = r.A;
		r.A -= r.L;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.L ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBr_a() {
		int a = r.A;
		r.A -= r.A;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.A ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SUBHL() {
		int a = r.A;
		int m = gb.mmu.readByte((r.H << 8) + r.L);
		r.A -= m;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void SUBn() {
		int a = r.A;
		int m = gb.mmu.readByte(r.PC);
		r.A -= m;
		r.PC++;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void SBCr_b() {
		int a = r.A;
		r.A -= r.B;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.B ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_c() {
		int a = r.A;
		r.A -= r.C;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.C ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_d() {
		int a = r.A;
		r.A -= r.D;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.D ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_e() {
		int a = r.A;
		r.A -= r.E;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.E ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_h() {
		int a = r.A;
		r.A -= r.H;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.H ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_l() {
		int a = r.A;
		r.A -= r.L;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.L ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCr_a() {
		int a = r.A;
		r.A -= r.A;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ r.A ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void SBCHL() {
		int a = r.A;
		int m = gb.mmu.readByte((r.H << 8) + r.L);
		r.A -= m;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void SBCn() {
		int a = r.A;
		int m = gb.mmu.readByte(r.PC);
		r.A -= m;
		r.PC++;
		r.A -= ((r.F & 0x10) > 0) ? 1 : 0;
		r.F = (r.A < 0) ? 0x50 : 0x40;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		if (((r.A ^ m ^ a) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void CPr_b() {
		int i = r.A;
		i -= r.B;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.B ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_c() {
		int i = r.A;
		i -= r.C;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.C ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_d() {
		int i = r.A;
		i -= r.D;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.D ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_e() {
		int i = r.A;
		i -= r.E;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.E ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_h() {
		int i = r.A;
		i -= r.H;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.H ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_l() {
		int i = r.A;
		i -= r.L;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.L ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPr_a() {
		int i = r.A;
		i -= r.A;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ r.A ^ i) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 1;
	}

	public void CPHL() {
		int i = r.A;
		int m = gb.mmu.readByte((r.H << 8) + r.L);
		i -= m;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ i ^ m) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void CPn() {
		int i = r.A;
		int m = gb.mmu.readByte(r.PC);
		i -= m;
		r.PC++;
		r.F = (i < 0) ? 0x50 : 0x40;
		i &= 255;
		if (i == 0)
			r.F |= 0x80;
		if (((r.A ^ i ^ m) & 0x10) > 0)
			r.F |= 0x20;
		r.M = 2;
	}

	public void DAA() {
		int a = r.A;
		if (((r.F & 0x20) > 0) || ((r.A & 15) > 9))
			r.A += 6;
		r.F &= 0xEF;
		if (((r.F & 0x20) > 0) || (a > 0x99)) {
			r.A += 0x60;
			r.F |= 0x10;
		}
		r.M = 1;
	}

	public void ANDr_b() {
		r.A &= r.B;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_c() {
		r.A &= r.C;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_d() {
		r.A &= r.D;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_e() {
		r.A &= r.E;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_h() {
		r.A &= r.H;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_l() {
		r.A &= r.L;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDr_a() {
		r.A &= r.A;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ANDHL() {
		r.A &= gb.mmu.readByte((r.H << 8) + r.L);
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void ANDn() {
		r.A &= gb.mmu.readByte(r.PC);
		r.PC++;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void ORr_b() {
		r.A |= r.B;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_c() {
		r.A |= r.C;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_d() {
		r.A |= r.D;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_e() {
		r.A |= r.E;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_h() {
		r.A |= r.H;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_l() {
		r.A |= r.L;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORr_a() {
		r.A |= r.A;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void ORHL() {
		r.A |= gb.mmu.readByte((r.H << 8) + r.L);
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void ORn() {
		r.A |= gb.mmu.readByte(r.PC);
		r.PC++;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void XORr_b() {
		r.A ^= r.B;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_c() {
		r.A ^= r.C;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_d() {
		r.A ^= r.D;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_e() {
		r.A ^= r.E;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_h() {
		r.A ^= r.H;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_l() {
		r.A ^= r.L;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORr_a() {
		r.A ^= r.A;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void XORHL() {
		r.A ^= gb.mmu.readByte((r.H << 8) + r.L);
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void XORn() {
		r.A ^= gb.mmu.readByte(r.PC);
		r.PC++;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void INCr_b() {
		r.B++;
		r.B &= 255;
		r.F = (r.B > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_c() {
		r.C++;
		r.C &= 255;
		r.F = (r.C > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_d() {
		r.D++;
		r.D &= 255;
		r.F = (r.D > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_e() {
		r.E++;
		r.E &= 255;
		r.F = (r.E > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_h() {
		r.H++;
		r.H &= 255;
		r.F = (r.H > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_l() {
		r.L++;
		r.L &= 255;
		r.F = (r.L > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCr_a() {
		r.A++;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void INCHLm() {
		int i = gb.mmu.readByte((r.H << 8) + r.L) + 1;
		i &= 255;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (i > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void DECr_b() {
		r.B--;
		r.B &= 255;
		r.F = (r.B > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_c() {
		r.C--;
		r.C &= 255;
		r.F = (r.C > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_d() {
		r.D--;
		r.D &= 255;
		r.F = (r.D > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_e() {
		r.E--;
		r.E &= 255;
		r.F = (r.E > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_h() {
		r.H--;
		r.H &= 255;
		r.F = (r.H > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_l() {
		r.L--;
		r.L &= 255;
		r.F = (r.L > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECr_a() {
		r.A--;
		r.A &= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void DECHLm() {
		int i = gb.mmu.readByte((r.H << 8) + r.L) - 1;
		i &= 255;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (i > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void INCBC() {
		r.C = (r.C + 1) & 255;
		if (r.C == 0)
			r.B = (r.B + 1) & 255;
		r.M = 1;
	}

	public void INCDE() {
		r.E = (r.E + 1) & 255;
		if (r.E == 0)
			r.D = (r.D + 1) & 255;
		r.M = 1;
	}

	public void INCHL() {
		r.L = (r.L + 1) & 255;
		if (r.L == 0)
			r.H = (r.H + 1) & 255;
		r.M = 1;
	}

	public void INCSP() {
		r.SP = (r.SP + 1) & 65535;
		r.M = 1;
	}

	public void DECBC() {
		r.C = (r.C - 1) & 255;
		if (r.C == 255)
			r.B = (r.B - 1) & 255;
		r.M = 1;
	}

	public void DECDE() {
		r.E = (r.E - 1) & 255;
		if (r.E == 255)
			r.D = (r.D - 1) & 255;
		r.M = 1;
	}

	public void DECHL() {
		r.L = (r.L - 1) & 255;
		if (r.L == 255)
			r.H = (r.H - 1) & 255;
		r.M = 1;
	}

	public void DECSP() {
		r.SP = (r.SP - 1) & 65535;
		r.M = 1;
	}

	/*--- Bit manipulation ---*/
	public void BIT0b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x01) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT0m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x01) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES0b() {
		r.B &= 0xFE;
		r.M = 2;
	}

	public void RES0c() {
		r.C &= 0xFE;
		r.M = 2;
	}

	public void RES0d() {
		r.D &= 0xFE;
		r.M = 2;
	}

	public void RES0e() {
		r.E &= 0xFE;
		r.M = 2;
	}

	public void RES0h() {
		r.H &= 0xFE;
		r.M = 2;
	}

	public void RES0l() {
		r.L &= 0xFE;
		r.M = 2;
	}

	public void RES0a() {
		r.A &= 0xFE;
		r.M = 2;
	}

	public void RES0m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xFE;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET0b() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0c() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0d() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0e() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0h() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0l() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0a() {
		r.B |= 0x01;
		r.M = 2;
	}

	public void SET0m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x01;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT1b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x02) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT1m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x02) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES1b() {
		r.B &= 0xFD;
		r.M = 2;
	}

	public void RES1c() {
		r.C &= 0xFD;
		r.M = 2;
	}

	public void RES1d() {
		r.D &= 0xFD;
		r.M = 2;
	}

	public void RES1e() {
		r.E &= 0xFD;
		r.M = 2;
	}

	public void RES1h() {
		r.H &= 0xFD;
		r.M = 2;
	}

	public void RES1l() {
		r.L &= 0xFD;
		r.M = 2;
	}

	public void RES1a() {
		r.A &= 0xFD;
		r.M = 2;
	}

	public void RES1m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xFD;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET1b() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1c() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1d() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1e() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1h() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1l() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1a() {
		r.B |= 0x02;
		r.M = 2;
	}

	public void SET1m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x02;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT2b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x04) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT2m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x04) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES2b() {
		r.B &= 0xFB;
		r.M = 2;
	}

	public void RES2c() {
		r.C &= 0xFB;
		r.M = 2;
	}

	public void RES2d() {
		r.D &= 0xFB;
		r.M = 2;
	}

	public void RES2e() {
		r.E &= 0xFB;
		r.M = 2;
	}

	public void RES2h() {
		r.H &= 0xFB;
		r.M = 2;
	}

	public void RES2l() {
		r.L &= 0xFB;
		r.M = 2;
	}

	public void RES2a() {
		r.A &= 0xFB;
		r.M = 2;
	}

	public void RES2m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xFB;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET2b() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2c() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2d() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2e() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2h() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2l() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2a() {
		r.B |= 0x04;
		r.M = 2;
	}

	public void SET2m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x04;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT3b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x08) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT3m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x08) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES3b() {
		r.B &= 0xF7;
		r.M = 2;
	}

	public void RES3c() {
		r.C &= 0xF7;
		r.M = 2;
	}

	public void RES3d() {
		r.D &= 0xF7;
		r.M = 2;
	}

	public void RES3e() {
		r.E &= 0xF7;
		r.M = 2;
	}

	public void RES3h() {
		r.H &= 0xF7;
		r.M = 2;
	}

	public void RES3l() {
		r.L &= 0xF7;
		r.M = 2;
	}

	public void RES3a() {
		r.A &= 0xF7;
		r.M = 2;
	}

	public void RES3m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xF7;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET3b() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3c() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3d() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3e() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3h() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3l() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3a() {
		r.B |= 0x08;
		r.M = 2;
	}

	public void SET3m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x08;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT4b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x10) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT4m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x10) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES4b() {
		r.B &= 0xEF;
		r.M = 2;
	}

	public void RES4c() {
		r.C &= 0xEF;
		r.M = 2;
	}

	public void RES4d() {
		r.D &= 0xEF;
		r.M = 2;
	}

	public void RES4e() {
		r.E &= 0xEF;
		r.M = 2;
	}

	public void RES4h() {
		r.H &= 0xEF;
		r.M = 2;
	}

	public void RES4l() {
		r.L &= 0xEF;
		r.M = 2;
	}

	public void RES4a() {
		r.A &= 0xEF;
		r.M = 2;
	}

	public void RES4m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xEF;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET4b() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4c() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4d() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4e() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4h() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4l() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4a() {
		r.B |= 0x10;
		r.M = 2;
	}

	public void SET4m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x10;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT5b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x20) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT5m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x20) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES5b() {
		r.B &= 0xDF;
		r.M = 2;
	}

	public void RES5c() {
		r.C &= 0xDF;
		r.M = 2;
	}

	public void RES5d() {
		r.D &= 0xDF;
		r.M = 2;
	}

	public void RES5e() {
		r.E &= 0xDF;
		r.M = 2;
	}

	public void RES5h() {
		r.H &= 0xDF;
		r.M = 2;
	}

	public void RES5l() {
		r.L &= 0xDF;
		r.M = 2;
	}

	public void RES5a() {
		r.A &= 0xDF;
		r.M = 2;
	}

	public void RES5m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xDF;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET5b() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5c() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5d() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5e() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5h() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5l() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5a() {
		r.B |= 0x20;
		r.M = 2;
	}

	public void SET5m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x20;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT6b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x40) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT6m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x40) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES6b() {
		r.B &= 0xBF;
		r.M = 2;
	}

	public void RES6c() {
		r.C &= 0xBF;
		r.M = 2;
	}

	public void RES6d() {
		r.D &= 0xBF;
		r.M = 2;
	}

	public void RES6e() {
		r.E &= 0xBF;
		r.M = 2;
	}

	public void RES6h() {
		r.H &= 0xBF;
		r.M = 2;
	}

	public void RES6l() {
		r.L &= 0xBF;
		r.M = 2;
	}

	public void RES6a() {
		r.A &= 0xBF;
		r.M = 2;
	}

	public void RES6m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0xBF;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET6b() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6c() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6d() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6e() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6h() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6l() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6a() {
		r.B |= 0x40;
		r.M = 2;
	}

	public void SET6m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x40;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void BIT7b() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.B & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7c() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.C & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7d() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.D & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7e() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.E & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7h() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.H & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7l() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.L & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7a() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((r.A & 0x80) > 0) ? 0 : 0x80;
		r.M = 2;
	}

	public void BIT7m() {
		r.F &= 0x1F;
		r.F |= 0x20;
		r.F = ((gb.mmu.readByte((r.H << 8) + r.L) & 0x80) > 0) ? 0 : 0x80;
		r.M = 3;
	}

	public void RES7b() {
		r.B &= 0x7F;
		r.M = 2;
	}

	public void RES7c() {
		r.C &= 0x7F;
		r.M = 2;
	}

	public void RES7d() {
		r.D &= 0x7F;
		r.M = 2;
	}

	public void RES7e() {
		r.E &= 0x7F;
		r.M = 2;
	}

	public void RES7h() {
		r.H &= 0x7F;
		r.M = 2;
	}

	public void RES7l() {
		r.L &= 0x7F;
		r.M = 2;
	}

	public void RES7a() {
		r.A &= 0x7F;
		r.M = 2;
	}

	public void RES7m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i &= 0x7F;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void SET7b() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7c() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7d() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7e() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7h() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7l() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7a() {
		r.B |= 0x80;
		r.M = 2;
	}

	public void SET7m() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		i |= 0x80;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.M = 4;
	}

	public void RLA() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) + ci;
		r.A &= 255;
		r.F = (r.F & 0xEF) + co;
		r.M = 1;
	}

	public void RLCA() {
		int ci = ((r.A & 0x80) > 0) ? 1 : 0;
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) + ci;
		r.A &= 255;
		r.F = (r.F & 0xEF) + co;
		r.M = 1;
	}

	public void RRA() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = (r.A >> 1) + ci;
		r.A &= 255;
		r.F = (r.F & 0xEF) + co;
		r.M = 1;
	}

	public void RRCA() {
		int ci = ((r.A & 1) > 0) ? 0x80 : 0;
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = (r.A >> 1) + ci;
		r.A &= 255;
		r.F = (r.F & 0xEF) + co;
		r.M = 1;
	}

	public void RLr_b() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.B & 0x80) > 0) ? 0x10 : 0;
		r.B = (r.B << 1) + ci;
		r.B &= 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_c() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.C & 0x80) > 0) ? 0x10 : 0;
		r.C = (r.C << 1) + ci;
		r.C &= 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_d() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.D & 0x80) > 0) ? 0x10 : 0;
		r.D = (r.D << 1) + ci;
		r.D &= 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_e() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.E & 0x80) > 0) ? 0x10 : 0;
		r.E = (r.E << 1) + ci;
		r.E &= 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_h() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.H & 0x80) > 0) ? 0x10 : 0;
		r.H = (r.H << 1) + ci;
		r.H &= 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_l() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.L & 0x80) > 0) ? 0x10 : 0;
		r.L = (r.L << 1) + ci;
		r.L &= 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLr_a() {
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) + ci;
		r.A &= 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLHL() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		int ci = ((r.F & 0x10) > 0) ? 1 : 0;
		int co = ((i & 0x80) > 0) ? 0x10 : 0;
		i = (i << 1) + ci;
		i &= 255;
		r.F = (i > 0) ? 0 : 0x80;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (r.F & 0xEF) + co;
		r.M = 4;
	}

	public void RLCr_b() {
		int ci = ((r.B & 0x80) > 0) ? 1 : 0;
		int co = ((r.B & 0x80) > 0) ? 0x10 : 0;
		r.B = (r.B << 1) + ci;
		r.B &= 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_c() {
		int ci = ((r.C & 0x80) > 0) ? 1 : 0;
		int co = ((r.C & 0x80) > 0) ? 0x10 : 0;
		r.C = (r.C << 1) + ci;
		r.C &= 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_d() {
		int ci = ((r.D & 0x80) > 0) ? 1 : 0;
		int co = ((r.D & 0x80) > 0) ? 0x10 : 0;
		r.D = (r.D << 1) + ci;
		r.D &= 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_e() {
		int ci = ((r.E & 0x80) > 0) ? 1 : 0;
		int co = ((r.E & 0x80) > 0) ? 0x10 : 0;
		r.E = (r.E << 1) + ci;
		r.E &= 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_h() {
		int ci = ((r.H & 0x80) > 0) ? 1 : 0;
		int co = ((r.H & 0x80) > 0) ? 0x10 : 0;
		r.H = (r.H << 1) + ci;
		r.H &= 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_l() {
		int ci = ((r.L & 0x80) > 0) ? 1 : 0;
		int co = ((r.L & 0x80) > 0) ? 0x10 : 0;
		r.L = (r.L << 1) + ci;
		r.L &= 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCr_a() {
		int ci = ((r.A & 0x80) > 0) ? 1 : 0;
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) + ci;
		r.A &= 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RLCHL() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		int ci = ((i & 0x80) > 0) ? 1 : 0;
		int co = ((i & 0x80) > 0) ? 0x10 : 0;
		i = (i << 1) + ci;
		i &= 255;
		r.F = (i > 0) ? 0 : 0x80;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (r.F & 0xEF) + co;
		r.M = 4;
	}

	public void RRr_b() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.B & 1) > 0) ? 0x10 : 0;
		r.B = (r.B >> 1) + ci;
		r.B &= 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_c() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.C & 1) > 0) ? 0x10 : 0;
		r.C = (r.C >> 1) + ci;
		r.C &= 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_d() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.D & 1) > 0) ? 0x10 : 0;
		r.D = (r.D >> 1) + ci;
		r.D &= 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_e() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.E & 1) > 0) ? 0x10 : 0;
		r.E = (r.E >> 1) + ci;
		r.E &= 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_h() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.H & 1) > 0) ? 0x10 : 0;
		r.H = (r.H >> 1) + ci;
		r.H &= 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_l() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.L & 1) > 0) ? 0x10 : 0;
		r.L = (r.L >> 1) + ci;
		r.L &= 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRr_a() {
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = (r.A >> 1) + ci;
		r.A &= 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRHL() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		int ci = ((r.F & 0x10) > 0) ? 0x80 : 0;
		int co = ((i & 1) > 0) ? 0x10 : 0;
		i = (i >> 1) + ci;
		i &= 255;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (i > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 4;
	}

	public void RRCr_b() {
		int ci = ((r.B & 1) > 0) ? 0x80 : 0;
		int co = ((r.B & 1) > 0) ? 0x10 : 0;
		r.B = (r.B >> 1) + ci;
		r.B &= 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_c() {
		int ci = ((r.C & 1) > 0) ? 0x80 : 0;
		int co = ((r.C & 1) > 0) ? 0x10 : 0;
		r.C = (r.C >> 1) + ci;
		r.C &= 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_d() {
		int ci = ((r.D & 1) > 0) ? 0x80 : 0;
		int co = ((r.D & 1) > 0) ? 0x10 : 0;
		r.D = (r.D >> 1) + ci;
		r.D &= 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_e() {
		int ci = ((r.E & 1) > 0) ? 0x80 : 0;
		int co = ((r.E & 1) > 0) ? 0x10 : 0;
		r.E = (r.E >> 1) + ci;
		r.E &= 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_h() {
		int ci = ((r.H & 1) > 0) ? 0x80 : 0;
		int co = ((r.H & 1) > 0) ? 0x10 : 0;
		r.H = (r.H >> 1) + ci;
		r.H &= 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_l() {
		int ci = ((r.L & 1) > 0) ? 0x80 : 0;
		int co = ((r.L & 1) > 0) ? 0x10 : 0;
		r.L = (r.L >> 1) + ci;
		r.L &= 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCr_a() {
		int ci = ((r.A & 1) > 0) ? 0x80 : 0;
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = (r.A >> 1) + ci;
		r.A &= 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void RRCHL() {
		int i = gb.mmu.readByte((r.H << 8) + r.L);
		int ci = ((i & 1) > 0) ? 0x80 : 0;
		int co = ((i & 1) > 0) ? 0x10 : 0;
		i = (i >> 1) + ci;
		i &= 255;
		gb.mmu.writeByte((r.H << 8) + r.L, i);
		r.F = (i > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 4;
	}

	public void SLAr_b() {
		int co = ((r.B & 0x80) > 0) ? 0x10 : 0;
		r.B = (r.B << 1) & 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_c() {
		int co = ((r.C & 0x80) > 0) ? 0x10 : 0;
		r.C = (r.C << 1) & 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_d() {
		int co = ((r.D & 0x80) > 0) ? 0x10 : 0;
		r.D = (r.D << 1) & 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_e() {
		int co = ((r.E & 0x80) > 0) ? 0x10 : 0;
		r.E = (r.E << 1) & 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_h() {
		int co = ((r.H & 0x80) > 0) ? 0x10 : 0;
		r.H = (r.H << 1) & 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_l() {
		int co = ((r.L & 0x80) > 0) ? 0x10 : 0;
		r.L = (r.L << 1) & 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLAr_a() {
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) & 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_b() {
		int co = ((r.B & 0x80) > 0) ? 0x10 : 0;
		r.B = (r.B << 1) & 255 + 1;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_c() {
		int co = ((r.C & 0x80) > 0) ? 0x10 : 0;
		r.C = (r.C << 1) & 255 + 1;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_d() {
		int co = ((r.D & 0x80) > 0) ? 0x10 : 0;
		r.D = (r.D << 1) & 255 + 1;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_e() {
		int co = ((r.E & 0x80) > 0) ? 0x10 : 0;
		r.E = (r.E << 1) & 255 + 1;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_h() {
		int co = ((r.H & 0x80) > 0) ? 0x10 : 0;
		r.H = (r.H << 1) & 255 + 1;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_l() {
		int co = ((r.L & 0x80) > 0) ? 0x10 : 0;
		r.L = (r.L << 1) & 255 + 1;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SLLr_a() {
		int co = ((r.A & 0x80) > 0) ? 0x10 : 0;
		r.A = (r.A << 1) & 255 + 1;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_b() {
		int ci = r.B & 0x80;
		int co = ((r.B & 1) > 0) ? 0x10 : 0;
		r.B = ((r.B >> 1) + ci) & 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_c() {
		int ci = r.C & 0x80;
		int co = ((r.C & 1) > 0) ? 0x10 : 0;
		r.C = ((r.C >> 1) + ci) & 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_d() {
		int ci = r.D & 0x80;
		int co = ((r.D & 1) > 0) ? 0x10 : 0;
		r.D = ((r.D >> 1) + ci) & 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_e() {
		int ci = r.E & 0x80;
		int co = ((r.E & 1) > 0) ? 0x10 : 0;
		r.E = ((r.E >> 1) + ci) & 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_h() {
		int ci = r.H & 0x80;
		int co = ((r.H & 1) > 0) ? 0x10 : 0;
		r.H = ((r.H >> 1) + ci) & 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_l() {
		int ci = r.L & 0x80;
		int co = ((r.L & 1) > 0) ? 0x10 : 0;
		r.L = ((r.L >> 1) + ci) & 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRAr_a() {
		int ci = r.A & 0x80;
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = ((r.A >> 1) + ci) & 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_b() {
		int co = ((r.B & 1) > 0) ? 0x10 : 0;
		r.B = (r.B >> 1) & 255;
		r.F = ((r.B) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_c() {
		int co = ((r.C & 1) > 0) ? 0x10 : 0;
		r.C = (r.C >> 1) & 255;
		r.F = ((r.C) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_d() {
		int co = ((r.D & 1) > 0) ? 0x10 : 0;
		r.D = (r.D >> 1) & 255;
		r.F = ((r.D) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_e() {
		int co = ((r.E & 1) > 0) ? 0x10 : 0;
		r.E = (r.E >> 1) & 255;
		r.F = ((r.E) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_h() {
		int co = ((r.H & 1) > 0) ? 0x10 : 0;
		r.H = (r.H >> 1) & 255;
		r.F = ((r.H) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_l() {
		int co = ((r.L & 1) > 0) ? 0x10 : 0;
		r.L = (r.L >> 1) & 255;
		r.F = ((r.L) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void SRLr_a() {
		int co = ((r.A & 1) > 0) ? 0x10 : 0;
		r.A = (r.A >> 1) & 255;
		r.F = ((r.A) > 0) ? 0 : 0x80;
		r.F = (r.F & 0xEF) + co;
		r.M = 2;
	}

	public void CPL() {
		r.A ^= 255;
		r.F = (r.A > 0) ? 0 : 0x80;
		r.M = 1;
	}

	public void NEG() {
		r.A = 0 - r.A;
		r.F = (r.A < 0) ? 0x10 : 0;
		r.A &= 255;
		if (r.A == 0)
			r.F |= 0x80;
		r.M = 2;
	}

	public void CCF() {
		int ci = ((r.F & 0x10) > 0) ? 0 : 0x10;
		r.F = (r.F & 0xEF) + ci;
		r.M = 1;
	}

	public void SCF() {
		r.F |= 0x10;
		r.M = 1;
	}

	/*--- Stack ---*/
	public void PUSHBC() {
		r.SP--;
		gb.mmu.writeByte(r.SP, r.B);
		r.SP--;
		gb.mmu.writeByte(r.SP, r.C);
		r.M = 3;
	}

	public void PUSHDE() {
		r.SP--;
		gb.mmu.writeByte(r.SP, r.D);
		r.SP--;
		gb.mmu.writeByte(r.SP, r.E);
		r.M = 3;
	}

	public void PUSHHL() {
		r.SP--;
		gb.mmu.writeByte(r.SP, r.H);
		r.SP--;
		gb.mmu.writeByte(r.SP, r.L);
		r.M = 3;
	}

	public void PUSHAF() {
		r.SP--;
		gb.mmu.writeByte(r.SP, r.A);
		r.SP--;
		gb.mmu.writeByte(r.SP, r.F);
		r.M = 3;
	}

	public void POPBC() {
		r.C = gb.mmu.readByte(r.SP);
		r.SP++;
		r.B = gb.mmu.readByte(r.SP);
		r.SP++;
		r.M = 3;
	}

	public void POPDE() {
		r.E = gb.mmu.readByte(r.SP);
		r.SP++;
		r.D = gb.mmu.readByte(r.SP);
		r.SP++;
		r.M = 3;
	}

	public void POPHL() {
		r.L = gb.mmu.readByte(r.SP);
		r.SP++;
		r.H = gb.mmu.readByte(r.SP);
		r.SP++;
		r.M = 3;
	}

	public void POPAF() {
		r.F = gb.mmu.readByte(r.SP);
		r.SP++;
		r.A = gb.mmu.readByte(r.SP);
		r.SP++;
		r.M = 3;
	}

	/*--- Jump ---*/
	public void JPnn() {
		r.PC = gb.mmu.readWord(r.PC);
		r.M = 3;
	}

	public void JPHL() {
		r.PC = (r.H << 8) + r.L;
		r.M = 1;
	}

	public void JPNZnn() {
		r.M = 3;
		if ((r.F & 0x80) == 0x00) {
			r.PC = gb.mmu.readWord(r.PC);
			r.M++;
		} else
			r.PC += 2;
	}

	public void JPZnn() {
		r.M = 3;
		if ((r.F & 0x80) == 0x80) {
			r.PC = gb.mmu.readWord(r.PC);
			r.M++;
		} else
			r.PC += 2;
	}

	public void JPNCnn() {
		r.M = 3;
		if ((r.F & 0x10) == 0x00) {
			r.PC = gb.mmu.readWord(r.PC);
			r.M++;
		} else
			r.PC += 2;
	}

	public void JPCnn() {
		r.M = 3;
		if ((r.F & 0x10) == 0x10) {
			r.PC = gb.mmu.readWord(r.PC);
			r.M++;
		} else
			r.PC += 2;
	}

	public void JRn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		r.PC += i;
		r.M++;
	}

	public void JRNZn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		if ((r.F & 0x80) == 0x00) {
			r.PC += i;
			r.M++;
		}
	}

	public void JRZn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		if ((r.F & 0x80) == 0x80) {
			r.PC += i;
			r.M++;
		}
	}

	public void JRNCn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		if ((r.F & 0x10) == 0x00) {
			r.PC += i;
			r.M++;
		}
	}

	public void JRCn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		if ((r.F & 0x10) == 0x10) {
			r.PC += i;
			r.M++;
		}
	}

	public void DJNZn() {
		int i = gb.mmu.readByte(r.PC);
		if (i > 127)
			i = -((~i + 1) & 255);
		r.PC++;
		r.M = 2;
		r.B--;
		if (r.B > 0) {
			r.PC += i;
			r.M++;
		}
	}

	public void CALLnn() {
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC + 2);
		r.PC = gb.mmu.readWord(r.PC);
		r.M = 5;
	}

	public void CALLNZnn() {
		r.M = 3;
		if ((r.F & 0x80) == 0x00) {
			r.SP -= 2;
			gb.mmu.writeWord(r.SP, r.PC + 2);
			r.PC = gb.mmu.readWord(r.PC);
			r.M += 2;
		} else
			r.PC += 2;
	}

	public void CALLZnn() {
		r.M = 3;
		if ((r.F & 0x80) == 0x80) {
			r.SP -= 2;
			gb.mmu.writeWord(r.SP, r.PC + 2);
			r.PC = gb.mmu.readWord(r.PC);
			r.M += 2;
		} else
			r.PC += 2;
	}

	public void CALLNCnn() {
		r.M = 3;
		if ((r.F & 0x10) == 0x00) {
			r.SP -= 2;
			gb.mmu.writeWord(r.SP, r.PC + 2);
			r.PC = gb.mmu.readWord(r.PC);
			r.M += 2;
		} else
			r.PC += 2;
	}

	public void CALLCnn() {
		r.M = 3;
		if ((r.F & 0x10) == 0x10) {
			r.SP -= 2;
			gb.mmu.writeWord(r.SP, r.PC + 2);
			r.PC = gb.mmu.readWord(r.PC);
			r.M += 2;
		} else
			r.PC += 2;
	}

	public void RET() {
		r.PC = gb.mmu.readWord(r.SP);
		r.SP += 2;
		r.M = 3;
	}

	public void RETI() {
		r.IME = 1;
		rrs();
		r.PC = gb.mmu.readWord(r.SP);
		r.SP += 2;
		r.M = 3;
	}

	public void RETNZ() {
		r.M = 1;
		if ((r.F & 0x80) == 0x00) {
			r.PC = gb.mmu.readWord(r.SP);
			r.SP += 2;
			r.M += 2;
		}
	}

	public void RETZ() {
		r.M = 1;
		if ((r.F & 0x80) == 0x80) {
			r.PC = gb.mmu.readWord(r.SP);
			r.SP += 2;
			r.M += 2;
		}
	}

	public void RETNC() {
		r.M = 1;
		if ((r.F & 0x10) == 0x00) {
			r.PC = gb.mmu.readWord(r.SP);
			r.SP += 2;
			r.M += 2;
		}
	}

	public void RETC() {
		r.M = 1;
		if ((r.F & 0x10) == 0x10) {
			r.PC = gb.mmu.readWord(r.SP);
			r.SP += 2;
			r.M += 2;
		}
	}

	public void RST00() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x00;
		r.M = 3;
	}

	public void RST08() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x08;
		r.M = 3;
	}

	public void RST10() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x10;
		r.M = 3;
	}

	public void RST18() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x18;
		r.M = 3;
	}

	public void RST20() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x20;
		r.M = 3;
	}

	public void RST28() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x28;
		r.M = 3;
	}

	public void RST30() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x30;
		r.M = 3;
	}

	public void RST38() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x38;
		r.M = 3;
	}

	public void RST40() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x40;
		r.M = 3;
	}

	public void RST48() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x48;
		r.M = 3;
	}

	public void RST50() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x50;
		r.M = 3;
	}

	public void RST58() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x58;
		r.M = 3;
	}

	public void RST60() {
		rsv();
		r.SP -= 2;
		gb.mmu.writeWord(r.SP, r.PC);
		r.PC = 0x60;
		r.M = 3;
	}

	public void NOP() {
		r.M = 1;
	}

	public void HALT() {
		HALT = 1;
		r.M = 1;
	}

	public void DI() {
		r.IME = 0;
		r.M = 1;
	}

	public void EI() {
		r.IME = 1;
		r.M = 1;
	}

	/*--- Helper functions ---*/
	public void rsv() {
		rsv.A = r.A;
		rsv.B = r.B;
		rsv.C = r.C;
		rsv.D = r.D;
		rsv.E = r.E;
		rsv.F = r.F;
		rsv.H = r.H;
		rsv.L = r.L;
	}

	public void rrs() {
		r.A = rsv.A;
		r.B = rsv.B;
		r.C = rsv.C;
		r.D = rsv.D;
		r.E = rsv.E;
		r.F = rsv.F;
		r.H = rsv.H;
		r.L = rsv.L;
	}

	public void MAPcb() {
		int i = gb.mmu.readByte(r.PC);
		r.PC++;
		r.PC &= 0xFFFF;
		cbMap(i);
	}

	public void XX() {
		/* Undefined map entry */
		int opc = r.PC - 1;
		//System.out.println("Unimplemented instruction at 0x" + Integer.toHexString(opc).toUpperCase() + ". stopping");
		STOP = 1;
	}

	private void opMap(int op) {
		switch (op) {
		case 0x00:
			NOP();
			break;
		case 0x01:
			LDBCnn();
			break;
		case 0x02:
			LDBCmA();
			break;
		case 0x03:
			INCBC();
			break;
		case 0x04:
			INCr_b();
			break;
		case 0x05:
			DECr_b();
			break;
		case 0x06:
			LDrn_b();
			break;
		case 0x07:
			RLCA();
			break;
		case 0x08:
			 LDmmSP();
			break;
		case 0x09:
			ADDHLBC();
			break;
		case 0x0A:
			LDABCm();
			break;
		case 0x0B:
			DECBC();
			break;
		case 0x0C:
			INCr_c();
			break;
		case 0x0D:
			DECr_c();
			break;
		case 0x0E:
			LDrn_c();
			break;
		case 0x0F:
			RRCA();
			break;

		// 10
		case 0x10:
			DJNZn();
			break;
		case 0x11:
			LDDEnn();
			break;
		case 0x12:
			LDDEmA();
			break;
		case 0x13:
			INCDE();
			break;
		case 0x14:
			INCr_d();
			break;
		case 0x15:
			DECr_d();
			break;
		case 0x16:
			LDrn_d();
			break;
		case 0x17:
			RLA();
			break;
		case 0x18:
			JRn();
			break;
		case 0x19:
			ADDHLDE();
			break;
		case 0x1A:
			LDADEm();
			break;
		case 0x1B:
			DECDE();
			break;
		case 0x1C:
			INCr_e();
			break;
		case 0x1D:
			DECr_e();
			break;
		case 0x1E:
			LDrn_e();
			break;
		case 0x1F:
			RRA();
			break;

		// 20
		case 0x20:
			JRNZn();
			break;
		case 0x21:
			LDHLnn();
			break;
		case 0x22:
			LDHLIA();
			break;
		case 0x23:
			INCHL();
			break;
		case 0x24:
			INCr_h();
			break;
		case 0x25:
			DECr_h();
			break;
		case 0x26:
			LDrn_h();
			break;
		case 0x27:
			XX();
			break;
		case 0x28:
			JRZn();
			break;
		case 0x29:
			ADDHLHL();
			break;
		case 0x2A:
			LDAHLI();
			break;
		case 0x2B:
			DECHL();
			break;
		case 0x2C:
			INCr_l();
			break;
		case 0x2D:
			DECr_l();
			break;
		case 0x2E:
			LDrn_l();
			break;
		case 0x2F:
			CPL();
			break;

		// 30
		case 0x30:
			JRNCn();
			break;
		case 0x31:
			LDSPnn();
			break;
		case 0x32:
			LDHLDA();
			break;
		case 0x33:
			INCSP();
			break;
		case 0x34:
			INCHLm();
			break;
		case 0x35:
			DECHLm();
			break;
		case 0x36:
			LDHLmn();
			break;
		case 0x37:
			SCF();
			break;
		case 0x38:
			JRCn();
			break;
		case 0x39:
			ADDHLSP();
			break;
		case 0x3A:
			LDAHLD();
			break;
		case 0x3B:
			DECSP();
			break;
		case 0x3C:
			INCr_a();
			break;
		case 0x3D:
			DECr_a();
			break;
		case 0x3E:
			LDrn_a();
			break;
		case 0x3F:
			CCF();
			break;

		// 40
		case 0x40:
			LDrr_bb();
			break;
		case 0x41:
			LDrr_bc();
			break;
		case 0x42:
			LDrr_bd();
			break;
		case 0x43:
			LDrr_be();
			break;
		case 0x44:
			LDrr_bh();
			break;
		case 0x45:
			LDrr_bl();
			break;
		case 0x46:
			LDrHLm_b();
			break;
		case 0x47:
			LDrr_ba();
			break;
		case 0x48:
			LDrr_cb();
			break;
		case 0x49:
			LDrr_cc();
			break;
		case 0x4A:
			LDrr_cd();
			break;
		case 0x4B:
			LDrr_ce();
			break;
		case 0x4C:
			LDrr_ch();
			break;
		case 0x4D:
			LDrr_cl();
			break;
		case 0x4E:
			LDrHLm_c();
			break;
		case 0x4F:
			LDrr_ca();
			break;

		// 50
		case 0x50:
			LDrr_db();
			break;
		case 0x51:
			LDrr_dc();
			break;
		case 0x52:
			LDrr_dd();
			break;
		case 0x53:
			LDrr_de();
			break;
		case 0x54:
			LDrr_dh();
			break;
		case 0x55:
			LDrr_dl();
			break;
		case 0x56:
			LDrHLm_d();
			break;
		case 0x57:
			LDrr_da();
			break;
		case 0x58:
			LDrr_eb();
			break;
		case 0x59:
			LDrr_ec();
			break;
		case 0x5A:
			LDrr_ed();
			break;
		case 0x5B:
			LDrr_ee();
			break;
		case 0x5C:
			LDrr_eh();
			break;
		case 0x5D:
			LDrr_el();
			break;
		case 0x5E:
			LDrHLm_e();
			break;
		case 0x5F:
			LDrr_ea();
			break;

		// 60
		case 0x60:
			LDrr_hb();
			break;
		case 0x61:
			LDrr_hc();
			break;
		case 0x62:
			LDrr_hd();
			break;
		case 0x63:
			LDrr_he();
			break;
		case 0x64:
			LDrr_hh();
			break;
		case 0x65:
			LDrr_hl();
			break;
		case 0x66:
			LDrHLm_h();
			break;
		case 0x67:
			LDrr_ha();
			break;
		case 0x68:
			LDrr_lb();
			break;
		case 0x69:
			LDrr_lc();
			break;
		case 0x6A:
			LDrr_ld();
			break;
		case 0x6B:
			LDrr_le();
			break;
		case 0x6C:
			LDrr_lh();
			break;
		case 0x6D:
			LDrr_ll();
			break;
		case 0x6E:
			LDrHLm_l();
			break;
		case 0x6F:
			LDrr_la();
			break;

		// 70
		case 0x70:
			LDHLmr_b();
			break;
		case 0x71:
			LDHLmr_c();
			break;
		case 0x72:
			LDHLmr_d();
			break;
		case 0x73:
			LDHLmr_e();
			break;
		case 0x74:
			LDHLmr_h();
			break;
		case 0x75:
			LDHLmr_l();
			break;
		case 0x76:
			HALT();
			break;
		case 0x77:
			LDHLmr_a();
			break;
		case 0x78:
			LDrr_ab();
			break;
		case 0x79:
			LDrr_ac();
			break;
		case 0x7A:
			LDrr_ad();
			break;
		case 0x7B:
			LDrr_ae();
			break;
		case 0x7C:
			LDrr_ah();
			break;
		case 0x7D:
			LDrr_al();
			break;
		case 0x7E:
			LDrHLm_a();
			break;
		case 0x7F:
			LDrr_aa();
			break;

		// 80
		case 0x80:
			ADDr_b();
			break;
		case 0x81:
			ADDr_c();
			break;
		case 0x82:
			ADDr_d();
			break;
		case 0x83:
			ADDr_e();
			break;
		case 0x84:
			ADDr_h();
			break;
		case 0x85:
			ADDr_l();
			break;
		case 0x86:
			ADDHL();
			break;
		case 0x87:
			ADDr_a();
			break;
		case 0x88:
			ADCr_b();
			break;
		case 0x89:
			ADCr_c();
			break;
		case 0x8A:
			ADCr_d();
			break;
		case 0x8B:
			ADCr_e();
			break;
		case 0x8C:
			ADCr_h();
			break;
		case 0x8D:
			ADCr_l();
			break;
		case 0x8E:
			ADCHL();
			break;
		case 0x8F:
			ADCr_a();
			break;

		// 90
		case 0x90:
			SUBr_b();
			break;
		case 0x91:
			SUBr_c();
			break;
		case 0x92:
			SUBr_d();
			break;
		case 0x93:
			SUBr_e();
			break;
		case 0x94:
			SUBr_h();
			break;
		case 0x95:
			SUBr_l();
			break;
		case 0x96:
			SUBHL();
			break;
		case 0x97:
			SUBr_a();
			break;
		case 0x98:
			SBCr_b();
			break;
		case 0x99:
			SBCr_c();
			break;
		case 0x9A:
			SBCr_d();
			break;
		case 0x9B:
			SBCr_e();
			break;
		case 0x9C:
			SBCr_h();
			break;
		case 0x9D:
			SBCr_l();
			break;
		case 0x9E:
			SBCHL();
			break;
		case 0x9F:
			SBCr_a();
			break;

		// A0
		case 0xA0:
			ANDr_b();
			break;
		case 0xA1:
			ANDr_c();
			break;
		case 0xA2:
			ANDr_d();
			break;
		case 0xA3:
			ANDr_e();
			break;
		case 0xA4:
			ANDr_h();
			break;
		case 0xA5:
			ANDr_l();
			break;
		case 0xA6:
			ANDHL();
			break;
		case 0xA7:
			ANDr_a();
			break;
		case 0xA8:
			XORr_b();
			break;
		case 0xA9:
			XORr_c();
			break;
		case 0xAA:
			XORr_d();
			break;
		case 0xAB:
			XORr_e();
			break;
		case 0xAC:
			XORr_h();
			break;
		case 0xAD:
			XORr_l();
			break;
		case 0xAE:
			XORHL();
			break;
		case 0xAF:
			XORr_a();
			break;

		// B0
		case 0xB0:
			ORr_b();
			break;
		case 0xB1:
			ORr_c();
			break;
		case 0xB2:
			ORr_d();
			break;
		case 0xB3:
			ORr_e();
			break;
		case 0xB4:
			ORr_h();
			break;
		case 0xB5:
			ORr_l();
			break;
		case 0xB6:
			ORHL();
			break;
		case 0xB7:
			ORr_a();
			break;
		case 0xB8:
			CPr_b();
			break;
		case 0xB9:
			CPr_c();
			break;
		case 0xBA:
			CPr_d();
			break;
		case 0xBB:
			CPr_e();
			break;
		case 0xBC:
			CPr_h();
			break;
		case 0xBD:
			CPr_l();
			break;
		case 0xBE:
			CPHL();
			break;
		case 0xBF:
			CPr_a();
			break;

		// C0
		case 0xC0:
			RETNZ();
			break;
		case 0xC1:
			POPBC();
			break;
		case 0xC2:
			JPNZnn();
			break;
		case 0xC3:
			JPnn();
			break;
		case 0xC4:
			CALLNZnn();
			break;
		case 0xC5:
			PUSHBC();
			break;
		case 0xC6:
			ADDn();
			break;
		case 0xC7:
			RST00();
			break;
		case 0xC8:
			RETZ();
			break;
		case 0xC9:
			RET();
			break;
		case 0xCA:
			JPZnn();
			break;
		case 0xCB:
			MAPcb();
			break;
		case 0xCC:
			CALLZnn();
			break;
		case 0xCD:
			CALLnn();
			break;
		case 0xCE:
			ADCn();
			break;
		case 0xCF:
			RST08();
			break;

		// D0
		case 0xD0:
			RETNC();
			break;
		case 0xD1:
			POPDE();
			break;
		case 0xD2:
			JPNCnn();
			break;
		case 0xD3:
			XX();
			break;
		case 0xD4:
			CALLNCnn();
			break;
		case 0xD5:
			PUSHDE();
			break;
		case 0xD6:
			SUBn();
			break;
		case 0xD7:
			RST10();
			break;
		case 0xD8:
			RETC();
			break;
		case 0xD9:
			RETI();
			break;
		case 0xDA:
			JPCnn();
			break;
		case 0xDB:
			XX();
			break;
		case 0xDC:
			CALLCnn();
			break;
		case 0xDD:
			XX();
			break;
		case 0xDE:
			SBCn();
			break;
		case 0xDF:
			RST18();
			break;

		// E0
		case 0xE0:
			LDIOnA();
			break;
		case 0xE1:
			POPHL();
			break;
		case 0xE2:
			LDIOCA();
			break;
		case 0xE3:
			XX();
			break;
		case 0xE4:
			XX();
			break;
		case 0xE5:
			PUSHHL();
			break;
		case 0xE6:
			ANDn();
			break;
		case 0xE7:
			RST20();
			break;
		case 0xE8:
			ADDSPn();
			break;
		case 0xE9:
			JPHL();
			break;
		case 0xEA:
			LDmmA();
			break;
		case 0xEB:
			XX();
			break;
		case 0xEC:
			XX();
			break;
		case 0xED:
			XX();
			break;
		case 0xEE:
			ORn();
			break;
		case 0xEF:
			RST28();
			break;

		// F0
		case 0xF0:
			LDAIOn();
			break;
		case 0xF1:
			POPAF();
			break;
		case 0xF2:
			LDAIOC();
			break;
		case 0xF3:
			DI();
			break;
		case 0xF4:
			XX();
			break;
		case 0xF5:
			PUSHAF();
			break;
		case 0xF6:
			XORn();
			break;
		case 0xF7:
			RST30();
			break;
		case 0xF8:
			LDHLSPn();
			break;
		case 0xF9:
			XX();
			break;
		case 0xFA:
			LDAmm();
			break;
		case 0xFB:
			EI();
			break;
		case 0xFC:
			XX();
			break;
		case 0xFD:
			XX();
			break;
		case 0xFE:
			CPn();
			break;
		case 0xFF:
			RST38();
		default:
			XX();
			break;
		}
	}

	private void cbMap(int op) {
		switch (op) {
		// CB00
		case 0xCB00:
			RLCr_b();
			break;
		case 0xCB01:
			RLCr_c();
			break;
		case 0xCB02:
			RLCr_d();
			break;
		case 0xCB03:
			RLCr_e();
			break;
		case 0xCB04:
			RLCr_h();
			break;
		case 0xCB05:
			RLCr_l();
			break;
		case 0xCB06:
			RLCHL();
			break;
		case 0xCB07:
			RLCr_a();
			break;
		case 0xCB08:
			RRCr_b();
			break;
		case 0xCB09:
			RRCr_c();
			break;
		case 0xCB0A:
			RRCr_d();
			break;
		case 0xCB0B:
			RRCr_e();
			break;
		case 0xCB0C:
			RRCr_h();
			break;
		case 0xCB0D:
			RRCr_l();
			break;
		case 0xCB0E:
			RRCHL();
			break;
		case 0xCB0F:
			RRCr_a();
			break;

		// CB10
		case 0xCB10:
			RLr_b();
			break;
		case 0xCB11:
			RLr_c();
			break;
		case 0xCB12:
			RLr_d();
			break;
		case 0xCB13:
			RLr_e();
			break;
		case 0xCB14:
			RLr_h();
			break;
		case 0xCB15:
			RLr_l();
			break;
		case 0xCB16:
			RLHL();
			break;
		case 0xCB17:
			RLr_a();
			break;
		case 0xCB18:
			RRr_b();
			break;
		case 0xCB19:
			RRr_c();
			break;
		case 0xCB1A:
			RRr_d();
			break;
		case 0xCB1B:
			RRr_e();
			break;
		case 0xCB1C:
			RRr_h();
			break;
		case 0xCB1D:
			RRr_l();
			break;
		case 0xCB1E:
			RRHL();
			break;
		case 0xCB1F:
			RRr_a();
			break;

		// CB20
		case 0xCB20:
			SLAr_b();
			break;
		case 0xCB21:
			SLAr_c();
			break;
		case 0xCB22:
			SLAr_d();
			break;
		case 0xCB23:
			SLAr_e();
			break;
		case 0xCB24:
			SLAr_h();
			break;
		case 0xCB25:
			SLAr_l();
			break;
		case 0xCB26:
			XX();
			break;
		case 0xCB27:
			SLAr_a();
			break;
		case 0xCB28:
			SRAr_b();
			break;
		case 0xCB29:
			SRAr_c();
			break;
		case 0xCB2A:
			SRAr_d();
			break;
		case 0xCB2B:
			SRAr_e();
			break;
		case 0xCB2C:
			SRAr_h();
			break;
		case 0xCB2D:
			SRAr_l();
			break;
		case 0xCB2E:
			XX();
			break;
		case 0xCB2F:
			SRAr_a();
			break;

		// CB30
		case 0xCB30:
			SWAPr_b();
			break;
		case 0xCB31:
			SWAPr_c();
			break;
		case 0xCB32:
			SWAPr_d();
			break;
		case 0xCB33:
			SWAPr_e();
			break;
		case 0xCB34:
			SWAPr_h();
			break;
		case 0xCB35:
			SWAPr_l();
			break;
		case 0xCB36:
			XX();
			break;
		case 0xCB37:
			SWAPr_a();
			break;
		case 0xCB38:
			SRLr_b();
			break;
		case 0xCB39:
			SRLr_c();
			break;
		case 0xCB3A:
			SRLr_d();
			break;
		case 0xCB3B:
			SRLr_e();
			break;
		case 0xCB3C:
			SRLr_h();
			break;
		case 0xCB3D:
			SRLr_l();
			break;
		case 0xCB3E:
			XX();
			break;
		case 0xCB3F:
			SRLr_a();
			break;

		// CB40
		case 0xCB40:
			BIT0b();
			break;
		case 0xCB41:
			BIT0c();
			break;
		case 0xCB42:
			BIT0d();
			break;
		case 0xCB43:
			BIT0e();
			break;
		case 0xCB44:
			BIT0h();
			break;
		case 0xCB45:
			BIT0l();
			break;
		case 0xCB46:
			BIT0m();
			break;
		case 0xCB47:
			BIT0a();
			break;
		case 0xCB48:
			BIT1b();
			break;
		case 0xCB49:
			BIT1c();
			break;
		case 0xCB4A:
			BIT1d();
			break;
		case 0xCB4B:
			BIT1e();
			break;
		case 0xCB4C:
			BIT1h();
			break;
		case 0xCB4D:
			BIT1l();
			break;
		case 0xCB4E:
			BIT1m();
			break;
		case 0xCB4F:
			BIT1a();
			break;

		// CB50
		case 0xCB50:
			BIT2b();
			break;
		case 0xCB51:
			BIT2c();
			break;
		case 0xCB52:
			BIT2d();
			break;
		case 0xCB53:
			BIT2e();
			break;
		case 0xCB54:
			BIT2h();
			break;
		case 0xCB55:
			BIT2l();
			break;
		case 0xCB56:
			BIT2m();
			break;
		case 0xCB57:
			BIT2a();
			break;
		case 0xCB58:
			BIT3b();
			break;
		case 0xCB59:
			BIT3c();
			break;
		case 0xCB5A:
			BIT3d();
			break;
		case 0xCB5B:
			BIT3e();
			break;
		case 0xCB5C:
			BIT3h();
			break;
		case 0xCB5D:
			BIT3l();
			break;
		case 0xCB5E:
			BIT3m();
			break;
		case 0xCB5F:
			BIT3a();
			break;

		// CB60
		case 0xCB60:
			BIT4b();
			break;
		case 0xCB61:
			BIT4c();
			break;
		case 0xCB62:
			BIT4d();
			break;
		case 0xCB63:
			BIT4e();
			break;
		case 0xCB64:
			BIT4h();
			break;
		case 0xCB65:
			BIT4l();
			break;
		case 0xCB66:
			BIT4m();
			break;
		case 0xCB67:
			BIT4a();
			break;
		case 0xCB68:
			BIT5b();
			break;
		case 0xCB69:
			BIT5c();
			break;
		case 0xCB6A:
			BIT5d();
			break;
		case 0xCB6B:
			BIT5e();
			break;
		case 0xCB6C:
			BIT5h();
			break;
		case 0xCB6D:
			BIT5l();
			break;
		case 0xCB6E:
			BIT5m();
			break;
		case 0xCB6F:
			BIT5a();
			break;

		// CB70
		case 0xCB70:
			BIT6b();
			break;
		case 0xCB71:
			BIT6c();
			break;
		case 0xCB72:
			BIT6d();
			break;
		case 0xCB73:
			BIT6e();
			break;
		case 0xCB74:
			BIT6h();
			break;
		case 0xCB75:
			BIT6l();
			break;
		case 0xCB76:
			BIT6m();
			break;
		case 0xCB77:
			BIT6a();
			break;
		case 0xCB78:
			BIT7b();
			break;
		case 0xCB79:
			BIT7c();
			break;
		case 0xCB7A:
			BIT7d();
			break;
		case 0xCB7B:
			BIT7e();
			break;
		case 0xCB7C:
			BIT7h();
			break;
		case 0xCB7D:
			BIT7l();
			break;
		case 0xCB7E:
			BIT7m();
			break;
		case 0xCB7F:
			BIT7a();
			break;
		// CB80
		// CB90
		// CBA0
		// CBB0
		// CBC0
		// CBD0
		// CBE0
		// CBF0
		default:
			XX();
			break;
		}
	}

}
