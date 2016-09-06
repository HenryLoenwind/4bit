package info.loenwind.bit16.compiler;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElemLabel implements Elem {

	private static final String SYNTAX = "^L(\\d+)(\\.\\d+)?:$";
	private static final Pattern P1 = Pattern.compile("^L(\\d+):$");
	private static final Pattern P2 = Pattern.compile("^L(\\d+)\\.(\\d+):$");
	
	@Override
	public boolean accepts(String s) {
		return s.matches(SYNTAX);
	}

	@Override
	public Elem[] next() {
		return null;
	}

	@Override
	public List<ParsedToken> finish(List<Elem> elems, List<String> tokens) throws Exception {
		Matcher matcher = P2.matcher(tokens.get(0));
		if (matcher.matches()) {
			return Collections.singletonList((ParsedToken)new TokenLabel(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));
		}
		matcher = P1.matcher(tokens.get(0));
		if (matcher.matches()) {
			return Collections.singletonList((ParsedToken)new TokenLabel(Integer.parseInt(matcher.group(1)), 0));
		}
		throw new Exception();
	}

	@Override
	public int value(String s) {
		return 0;
	}

}
