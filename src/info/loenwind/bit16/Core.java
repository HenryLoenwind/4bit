package info.loenwind.bit16;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Core {

	private static final int ACC = 0;
	private static final int R01 = 1;
	private static final int R02 = 2;
	private static final int R03 = 3;
	private static final int R04 = 4;
	private static final int R05 = 5;
	private static final int R06 = 6;
	private static final int AD0 = 7;
	private static final int AD1 = 8;
	private static final int LOS = ACC;
	private static final int STS = 9;
	private static final int _RS0 = 10;
	private static final int RUP = 10;
	private static final int RDN = 11;
	private static final int RNO = 12;
	private static final int RSO = 13;
	private static final int RWE = 14;
	private static final int REA = 15;

	private final int[] sreg = new int[16];
	private final IActiveRegister[] areg = new IActiveRegister[16];
	
	private final int STS_OV = 0b1000;
	private final int STS_EQ = 0b0100;
	private final int STS_UN = 0b0010;
	private final int STS_ZR = 0b0001;
	private final int STS_GT = STS_OV;
	private final int STS_LT = STS_UN;
	
	private int ip = 0;
	
	private String stsToString() {
		return
				((rreg(STS) & STS_UN) != 0 ? "<" : "_" ) +
				((rreg(STS) & STS_EQ) != 0 ? "=" : "_" ) +
				((rreg(STS) & STS_OV) != 0 ? ">" : "_" ) +
				((rreg(STS) & STS_ZR) != 0 ? "Z" : "_" ) ;
	}
	
	private String regToString() {
		List<String> result = new ArrayList<String>();
		for (Registers r : Registers.values()) {
			result.add(r + "=" + sreg[r.getNo()]);
		}
		return result + "";
	}
	
	@Override
	public String toString() {
		return "Core [ip=" + ip + ", STS=" + stsToString()
				+ ", reg=" + regToString() + "]";
	}

	private int rreg(int param) {
		if (areg[param] != null) {
			return areg[param].read();
		} else {
			return sreg[param];
		}
	}
	
	private void rreg(int param, int value) {
		int sts = rreg(STS) & ~(STS_OV | STS_UN | STS_ZR);
		if (value < 0) {
			sts |= STS_UN;
		} else if ((value & 0xF) != 0) {
			sts |= STS_OV;
		}
		value &= 0xF;
		if (value == 0) {
			sts |= STS_ZR;
		}
		_rreg(param, value);
		_rreg(STS, sts);
	}

	private void _rreg(int param, int value) {
		if (areg[param] != null) {
			areg[param].write(value);
		} else {
			sreg[param] = value;
		}
	}

	private static interface IOp {
		void exc(int param);
	}
	
	private class OpZro implements IOp {
		public void exc(int param) {
			rreg(param, 0);
		}
	}
	
	private class OpAdd implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) + rreg(param));
		}
	}
	
	private class OpSub implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) - rreg(param));
		}
	}
	
	private class OpMul implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) * rreg(param));
		}
	}
	
	private class OpDiv implements IOp {
		public void exc(int param) {
			int val = rreg(param);
			if (val == 0) {
				rreg(ACC, -1);
			} else {
				rreg(ACC, rreg(ACC) / val);
			}
		}
	}
	
	private class OpAnd implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) & rreg(param));
		}
	}
	
	private class OpOrr implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) | rreg(param));
		}
	}
	
	private class OpXor implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(ACC) ^ rreg(param));
		}
	}
	
	private class OpCmp implements IOp {
		public void exc(int param) {
			int val = rreg(param);
			int acc = rreg(ACC);
			int sts;
			if (acc == val) {
				sts = STS_EQ;
			} else if (acc > val) {
				sts = STS_LT;
			} else {
				sts = STS_GT;
			}
			if (acc == 0) {
				sts |= STS_ZR;
			}
			_rreg(STS, sts);
		}
	}

	private class OpLdn implements IOp {
		public void exc(int param) {
			rreg(LOS, param);
		}
	}

	private class OpLdm implements IOp {
		public void exc(int param) {
			int ptr = adr(param);
			int ptrHi = ptr >>> 1;
			int ptrLo = ptr & 0x1;
			rreg(LOS, ((ptrLo == 0) ? data[ptrHi] : (data[ptrHi] >>> 4)) & 0xF); 
		}
	}

	private class OpSto implements IOp {
		public void exc(int param) {
			int ptr = adr(param);
			int ptrHi = ptr >>> 1;
			int ptrLo = ptr & 0x1;
			if (ptrLo == 0) {
				data[ptrHi] = (byte) ((data[ptrHi] & 0xF0) | rreg(LOS));
			} else {
				data[ptrHi] = (byte) ((data[ptrHi] & 0x0F) | (rreg(LOS) << 4));
			}
		}
	}

	private class OpMvr implements IOp {
		public void exc(int param) {
			rreg(ACC, rreg(param));
		}
	}

	private class OpMva implements IOp {
		public void exc(int param) {
			rreg(param, rreg(ACC));
		}
	}

	private int adr() {
		return adr(0);
	}
	
	private int adr(int offset) {
		return ((rreg(AD0) | (rreg(AD1) << 4)) + offset) & 0xFF;
	}
	
	private class OpJmp implements IOp {
		public void exc(int param) {
			if ((param & rreg(STS)) != 0) {
				ip = adr();
			}
		}
	}

	private class OpRet implements IOp {
		public void exc(int param) {
			throw new ReturnException(param);
		}
	}

	public static class ReturnException extends RuntimeException {
		private static final long serialVersionUID = 132619482256018788L;
		private final int returnID;
		public ReturnException(int returnID) {
			this.returnID = returnID;
		}
		public int getReturnID() {
			return returnID;
		}
	}
	
	private final IOp[] op = {
			new OpRet(), // 0
			new OpAdd(), // 1
			new OpSub(), // 2
			new OpMul(), // 3
			new OpDiv(), // 4
			new OpAnd(), // 5
			new OpOrr(), // 6
			new OpXor(), // 7
			new OpCmp(), // 8
			new OpLdn(), // 9
			new OpLdm(), // A
			new OpSto(), // B
			new OpMvr(), // C
			new OpMva(), // D
			new OpJmp(), // E
			new OpZro()  // F
		};
	
	private byte[] data;
	private byte[] code;
	
	private void execStep() {
		int step = code[ip++];
		ip &= 0xFF;
		int ins = (step >>> 4) & 0xF;
		int param = step & 0xF;
		op[ins].exc(param);
	}

	public int exec(int limit) {
		for (int i = 0; i < limit; i++) {
			try {
				execStep();
			} catch (ReturnException e) {
				ip = 0;
				return e.getReturnID();
			}
		}
		return -1;
	}

	public Core() {
		reset();
	}

	public void reset() {
		data = new byte[128];
		code = new byte[256];
	}
	
	public Core(byte[] data, byte[] code) {
		this.data = data;
		this.code = code;
	}

	public void register(int register, IActiveRegister callback) {
		areg[register] = callback;
	}
	
}
