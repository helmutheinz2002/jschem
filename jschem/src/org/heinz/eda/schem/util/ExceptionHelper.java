package org.heinz.eda.schem.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionHelper {
	public static String getStacktrace(Throwable t) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream pw = new PrintStream(bos);
		t.printStackTrace(pw);
		pw.close();
		byte[] bytes = bos.toByteArray();
		String s = new String(bytes);
		return s;
	}
}
