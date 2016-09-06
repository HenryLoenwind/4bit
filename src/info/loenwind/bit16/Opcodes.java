package info.loenwind.bit16;

public enum Opcodes {
	RET(false),
	ADD(true),
	SUB(true),
	MUL(true),
	DIV(true),
	AND(true),
	ORR(true),
	XOR(true),
	CMP(true),
	LDN(false),
	LDM(false),
	STO(false),
	MVR(true),
	MVA(true),
	JMP(false),
	ZRO(true),
	;

	private final int no;
	final boolean takeRegister;
	
	private Opcodes(boolean takeRegister) {
		this.no = ordinal();
		this.takeRegister = takeRegister;
	}

	private Opcodes(int no, boolean takeRegister) {
		this.no = no;
		this.takeRegister = takeRegister;
	}
	
	public int getNo() {
		return no;
	}

}