package info.loenwind.bit16.compiler;

import java.util.Map;

public interface ParsedToken {
	int advanceAddress(int addr);
	String map(Map<String, ParsedToken> mapping);
	void resolve(Map<String, ParsedToken> mapping) throws Exception;
	void compile(byte[] bytes);
}