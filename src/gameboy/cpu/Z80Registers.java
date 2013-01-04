package gameboy.cpu;

/**
 * Registers
 */
public class Z80Registers {
	/**
	 * General registers, 8-bit
	 */
	public int A;	// Accumulation Register

	public int B;

	public int C;

	public int D;

	public int E;

	public int H;

	public int L;
	

	/**
	 * Flag register, 8-bit
	 */
	public int F;

	/**
	 * Stack pointer, 16-bit
	 */
	public int SP;	// Stack Pointer

	/**
	 * Instruction pointer, 16-bit
	 */
	public int PC;	// Program Counter 
	
	public int I;
	
	public int R;

	/**
	 * The amount of time used to execute the last instruction
	 */
	public long M;

	/**
	 * The amount of time that the CPU has run in total
	 */
	public long T;
	
	public int IME;
	
	/**
	 * clear
	 */
	public void clear() {
		A = 0;
		B = 0;
		C = 0;
		D = 0;
		E = 0;
		H = 0;
		L = 0;
		F = 0;
		
		SP = 0;
		PC = 0;  // Start execution at 0
		I = 0;
		R = 0;
		
		M = 0;
		T = 0;
		
		IME = 1;
	}

}
