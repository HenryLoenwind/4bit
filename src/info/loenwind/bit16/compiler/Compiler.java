package info.loenwind.bit16.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Compiler {

	private Elem current = null;
	private List<Elem> running = new ArrayList<Elem>();
	private List<String> tokens = new ArrayList<String>();
	private Elem[] next = OUTER.next();
	
	public static final Elem OUTER = null;
	
	public List<ParsedToken> exec(int baseaddr, String input) throws Exception {
		List<ParsedToken> result = new ArrayList<ParsedToken>();
		StringTokenizer tok = new StringTokenizer(input);
		TOKEN: while (tok.hasMoreTokens()) {
			String nextToken = tok.nextToken();
			for (Elem elem : next) {
				if (elem.accepts(nextToken)) {
					if (current == null) {
						current = elem;
					}
					running.add(elem);
					tokens.add(nextToken);
					next = elem.next();
					if (next == null) {
						next = OUTER.next();
					}
					List<ParsedToken> finish = current.finish(running, tokens);
					if (finish != null) {
						result.addAll(finish);
						current = null;
						running.clear();
						tokens.clear();
					}
					continue TOKEN;
				}
				throw new Exception();
			}
		}
		if (current != null) {
			throw new Exception();
		}
		
		int addr = baseaddr;
		for (ParsedToken parsedToken : result) {
			addr = parsedToken.advanceAddress(addr);
			if (addr > 256) {
				throw new Exception();
			}
		}
		
		Map<String, ParsedToken> mapping = new HashMap<String, ParsedToken>();
		for (ParsedToken parsedToken : result) {
			String id = parsedToken.map(mapping);
			if (id != null) {
				mapping.put(id, parsedToken);
			}
		}

		for (ParsedToken parsedToken : result) {
			parsedToken.resolve(mapping);
		}
		
		return result;
	}

	public byte[] toBytes(List<ParsedToken> tokens) {
		byte[] bytes = new byte[256];
		for (ParsedToken parsedToken : tokens) {
			parsedToken.compile(bytes);
		}
		return bytes;
	}

}
