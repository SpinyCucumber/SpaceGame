package spishu.space.emulator;

public class Emulator {

	/**
	 * Memory. Size and position of bios and program depend on processor.
	 */
	protected char[] memory;
	/**
	 * Registers. Size and purpose depend on processor.
	 */
	protected char[] V;
	/**
	 * Memory pointer; 16-bit
	 */
	protected char I;
	/**
	 * Program counter, points to current operation. 16-bit
	 */
	protected char pc;

	/**
	 * Subroutine callstack<br/>
	 * Attributes defined by processor.
	 */
	protected char[] stack;
	/**
	 * Points to the next free slot int the stack
	 */
	protected int stackPointer;

}
