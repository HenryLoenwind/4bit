package info.loenwind.bit16.compiler;

import info.loenwind.bit16.Opcodes;

public abstract class ElemOpcode implements Elem {

	@Override
	public boolean accepts(String s) {
		try {
			Opcodes.valueOf(s);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	@Override
	public Elem[] next() {
		return null;
	}

	@Override
	public int value(String s) {
		return Opcodes.valueOf(s).getNo();
	}

}
