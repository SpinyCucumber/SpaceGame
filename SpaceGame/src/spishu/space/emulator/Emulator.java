package spishu.space.emulator;

@SuppressWarnings("unused")
public class Emulator {

	/**
	 * Memory. Size and position of bios and program depend on processor.
	 */
	private char[] memory;
	/**
	 * Registers. Size and purpose depend on processor.
	 */
	private char[] V;
	/**
	 * Memory pointer; 16-bit
	 */
	private char I;
	/**
	 * Program counter, points to current operation. 16-bit
	 */
	private char pc;

	/**
	 * Subroutine callstack<br/>
	 * Attributes defined by processor.
	 */
	private char stack[];
	/**
	 * Points to the next free slot int the stack
	 */
	private int stackPointer;

}
