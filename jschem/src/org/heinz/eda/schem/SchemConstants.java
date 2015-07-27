package org.heinz.eda.schem;

import java.text.DecimalFormat;

public class SchemConstants {
	public static String DATE = "COMPILATION_DATE";
	public static final String PROGRAM_NAME = "JSchem";
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 9;
	public static final int PATCH_LEVEL = 35;
	public static final String PROGRAM_VERSION = getVersionString();
	public static final String PROGRAM_DESCRIPTION = PROGRAM_NAME + " " + PROGRAM_VERSION;
	public static final String GPL_HEADER = PROGRAM_NAME + " version " + PROGRAM_VERSION + ", Copyright (C) " + getCompilationYear() + " Bernhard Walter (aka Heinz)\n" +
		PROGRAM_NAME + " comes with ABSOLUTELY NO WARRANTY.\n" +
		"This is free software, and you are welcome to redistribute it under certain conditions\n" +
		"See http://www.fsf.org/licensing/licenses/gpl.html for details";
	public static final int LAUNCH_MGR_PORT_NR = 12032;
	
	public static String getCompilationYear() {
		int idx = DATE.indexOf('-');
		if(idx < 0)
			return DATE;
		return DATE.substring(0, idx);
	}
	
	private static String getVersionString() {
		DecimalFormat f = new DecimalFormat("00");
		return "" + MAJOR_VERSION + "." + f.format(MINOR_VERSION) + "." + f.format(PATCH_LEVEL);
	}
}
