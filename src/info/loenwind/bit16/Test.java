package info.loenwind.bit16;

import java.util.Arrays;

public class Test {

	private static String[] code = {
		"L0: ldn 3",
		"mva R01",
		"ldn 5",
		"L1: add R01",
		"mva R02",
		"ldn 6",
		"cmp R02",
		"ldn L1l",
		"mva AD0",
		"ldn L1h",
		"mva AD1",
		"jmp 15",
		
	};
	
	public static void main(String[] args) {
		Compiler compiler = new Compiler();
		compiler.parse(Arrays.asList(code));
		System.out.println(compiler);
		Core core = new Core(new byte[128], compiler.toBytes());
		for (int i = 0; i < 100; i++) {
			core.exec(1);
			System.out.println(core);
		}
	}

}
