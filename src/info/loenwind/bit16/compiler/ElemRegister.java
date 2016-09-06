package info.loenwind.bit16.compiler;

import info.loenwind.bit16.Registers;

import java.util.List;

public abstract class ElemRegister implements Elem {

	@Override
	public boolean accepts(String s) {
		try {
			Registers.valueOf(s);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	@Override
	public List<ParsedToken> finish(List<Elem> elems, List<String> tokens) {
		return null;
	}

	@Override
	public int value(String s) {
		return Registers.valueOf(s).getNo();
	}
}
