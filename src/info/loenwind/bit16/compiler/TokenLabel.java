package info.loenwind.bit16.compiler;

import java.util.Map;

public class TokenLabel implements ParsedToken {

	private final int id, subid;
	private int addr, offset = 0;
	
	public TokenLabel(int id, int subid) {
		this.id = id;
		this.subid = subid;
	}

	@Override
	public int advanceAddress(int addr) {
		this.addr = addr;
		return addr;
	}

	@Override
	public String map(Map<String, ParsedToken> mapping) {
		return id + "." + subid;
	}

	@Override
	public void resolve(Map<String, ParsedToken> mapping) throws Exception {
		if (subid != 0) {
			ParsedToken parent = mapping.get(id + ".0");
			if (parent instanceof TokenLabel) {
				int parentaddr = ((TokenLabel) parent).addr;
				offset = addr - parentaddr;
				if (offset <= 15) {
					return;
				}
			}
			throw new Exception();
		}
	}

	@Override
	public void compile(byte[] bytes) {
		return;
	}

}
