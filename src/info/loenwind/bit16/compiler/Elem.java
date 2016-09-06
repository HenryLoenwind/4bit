package info.loenwind.bit16.compiler;

import java.util.List;

public interface Elem {
	boolean accepts(String s);
	Elem[] next();
	List<ParsedToken> finish(List<Elem> elems, List<String> tokens) throws Exception;
	/**
	 * Returns a element specific value for the data. May be meaningless.
	 * 
	 * @param s A value that has been accepted by this element
	 * @return An integer that represents this element
	 */
	int value(String s);
}