package info.loenwind.bit16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Compiler {

	private static class Opcode {
		Opcodes opcode;
		Registers register;
		int param;
		byte toByte() {
			if (opcode.takeRegister) {
				return (byte) (opcode.getNo() << 4 | register.getNo());
			} else {
				return (byte) (opcode.getNo() << 4 | param);
			}
		}
		@Override
		public String toString() {
			return "Opcode [" + opcode + ( opcode.takeRegister ? " " + register
					: " " + param ) + "]";
		}
	}
	
	private static class Label {
		int addr;
		int id;
		int offset = 0;
		@Override
		public String toString() {
			return "Label [addr=" + addr + ", id=" + id + ", offset=" + offset
					+ "]";
		}
	}
	private static class SubLabel extends Label {
		int subid;
		Label parent;
		@Override
		public String toString() {
			return "SubLabel [subid=" + subid + ", addr=" + addr + ", id=" + id
					+ ", offset=" + offset + "]";
		}
	}
	
	private List<Opcode> code = new ArrayList<Opcode>();
	private Map<String, Label> labels = new HashMap<String, Label>();
	
	private Opcode parse(String op, String prm) {
		Opcode result = new Opcode();
		result.opcode = Opcodes.valueOf(op);
		if (result.opcode.takeRegister) {
			result.register = Registers.valueOf(prm);
		} else {
			result.param = parseParam(prm);
		}
		return result;
	}

	private final Pattern LABEL = Pattern.compile("^L(\\d+):$");
	private final Pattern SUBLABEL = Pattern.compile("^L(\\d+)\\.(\\d+):$");

	private final Pattern TARGET1 = Pattern.compile("^L(\\d+)(H|L)$");
	private final Pattern TARGET2 = Pattern.compile("^L(\\d+)$");
	private final Pattern TARGET3 = Pattern.compile("^L(\\d+)\\.(\\d+)$");

	private int parseParam(String param) {
		Matcher matcher1 = TARGET1.matcher(param);
		if (matcher1.matches()) {
			String id = matcher1.group(1);
			String hl = matcher1.group(2);
			Label label = labels.get(id);
			return (hl.equals("H") ? (label.addr >>> 4) : label.addr) & 0xf;
		}

		Matcher matcher2 = TARGET2.matcher(param);
		if (matcher2.matches()) {
			return 0;
		}

		Matcher matcher3 = TARGET3.matcher(param);
		if (matcher3.matches()) {
			String id = matcher3.group(1);
			String sub = matcher3.group(2);
			Label label = labels.get(id + "." + sub);
			return label.offset & 0xf;
		}
		
		return Integer.valueOf(param);
	}
	
	private Opcode parse(String line) {
		String[] split = line.split("\\s+");
		if (split.length == 3) {
			Matcher matcher1 = SUBLABEL.matcher(split[0]);
			Matcher matcher2 = LABEL.matcher(split[0]);
			if (matcher1.matches()) {
				SubLabel subLabel2 = new SubLabel();
				subLabel2.id = Integer.parseInt(matcher1.group(1));
				subLabel2.subid = Integer.parseInt(matcher1.group(2));
				subLabel2.parent = labels.get(subLabel2.id + "");
				subLabel2.addr = code.size();
				subLabel2.offset = subLabel2.addr - subLabel2.parent.addr;
				labels.put(subLabel2.id + "." + subLabel2.subid, subLabel2);
			} else if (matcher2.matches()) {
				Label label2 = new Label();
				label2.id = Integer.parseInt(matcher2.group(1));
				label2.addr = code.size();
				labels.put(label2.id + "", label2);
			} else {
				return null; // error
			}
			return parse(split[1].trim(), split[2].trim());
		} else if (split.length == 2) {
			return parse(split[0].trim(), split[1].trim());
		} else {
			return null; // error
		}
	}
	
	public void parse(List<String> input) {
		for (String string : input) {
			code.add(parse(string.toUpperCase().trim()));
		}
	}
	
	public byte[] toBytes() {
		byte[] result = new byte[256];
		for (int i = 0; i < code.size(); i++) {
			result[i] = code.get(i).toByte();
		}
		return result;
	}

	@Override
	public String toString() {
		return "Compiler [code=" + code + ", labels=" + labels + "]";
	}
}
