package info.loenwind.bit16;

public enum Registers {
	R00,
	R01,
	R02,
	R03,
	R04,
	R05,
	R06,
	R07,
	R08,
	R09,
	R10,
	R11,
	R12,
	R13,
	R14,
	R15,

	R0A(10),
	R0B(11),
	R0C(12),
	R0D(13),
	R0E(14),
	R0F(15),

	ACC(0),
	LOS(0),
	AD0(7),
	AD1(8),
	STS(9),
	RUP(10),
	RDN(11),
	RNO(12),
	RSO(13),
	RWE(14),
	REA(15),
	;
	
	private final int no;

	private Registers() {
		this.no = ordinal();
	}

	private Registers(int no) {
		this.no = no;
	}

	public int getNo() {
		return no;
	}
}