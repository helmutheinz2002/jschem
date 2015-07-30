
package org.heinz.eda.schem.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


public class UnitConverter {

	private static final DecimalFormat format = new DecimalFormat("0.00");

	private static final char SYMBOL_POINT = ((DecimalFormat) (DecimalFormat.getInstance())).getDecimalFormatSymbols().getDecimalSeparator();

	private static final char SYMBOL_PLUS = '+';

	private static final char SYMBOL_MINUS = '-';

	private static final Unit[] UNITS = {
		new Unit("f", 0.000000000000001d),
		new Unit("p", 0.000000000001d),
		new Unit("n", 0.000000001d),
		new Unit("u", 0.000001d),
		new Unit("m", 0.001d),
		new Unit("k", 1000d),
		new Unit("meg", 1000000d),
		new Unit("G", 1000000000d),
		new Unit("T", 1000000000000d),
		new Unit("P", 1000000000000000d),};

	@SuppressWarnings("CallToPrintStackTrace")
	public static int getUnitValue(String s) {
		if((s == null) || (s.length() == 0)) {
			return 0;
		}

		while(!Character.isDigit(s.charAt(s.length() - 1))) {
			s = s.substring(0, s.length() - 1);
		}

		try {
			double d = format.parse(s).doubleValue();
			return (int) (d * 100);
		} catch(Exception nex) {
			nex.printStackTrace();
		}
		return 0;
	}

	public static String getStringValue(int units) {
		double d = (double) units / 100;
		return format.format(d);
	}

	/**
	 * Recognized Units:
	 * f femto 10^-15
	 * p piko 10^-12
	 * n nano 10^-9
	 * u mikro  10^-6
	 * m milli 10^-3
	 * K or k kilo 10^3
	 * M mega 10^6
	 * G giga 10^9
	 * T tera 10^12
	 * P peta 10^15
	 * @param valWithUnits
	 * @return
	 * @throws IllegalArgumentException, NumberFormatException
	 */
	public static double convertToDecimal(String valWithUnits) throws IllegalArgumentException, NumberFormatException {
		valWithUnits = getFirstValue(valWithUnits.trim());
		String unitKey = findUnitKey(valWithUnits);
		valWithUnits = processEngineeringNotation(valWithUnits, unitKey);

		double factor = getFactor(unitKey);
		String number = valWithUnits.substring(0, valWithUnits.length() - unitKey.length());
		double numVal = Double.parseDouble(number);
		return numVal * factor;
	}

	public static String convertToStandardNotation(String valWithUnits) throws IllegalArgumentException, NumberFormatException {
		valWithUnits = getFirstValue(valWithUnits.trim());
		String unitKey = findUnitKey(valWithUnits);
		valWithUnits = processEngineeringNotation(valWithUnits, unitKey);
		return valWithUnits;
	}

	private static boolean isSign(char c) {
		return (c == SYMBOL_MINUS) || (c == SYMBOL_PLUS);
	}

	private static String findUnitKey(String valWithUnits) {
		StringBuilder unitKey = new StringBuilder("");

		for(int i = 0; i < valWithUnits.length(); i++) {
			char c = valWithUnits.charAt(i);
			if(!Character.isDigit(c) && (c != SYMBOL_POINT) && !isSign(c)) {
				unitKey.append(c);
			}
		}

		return unitKey.toString();
	}

	private static String processEngineeringNotation(String valWithUnits, String unitKey) {
		if(unitKey.length() == 0) {
			return valWithUnits;
		}

		if(valWithUnits.endsWith(unitKey)) {
			return valWithUnits;
		}

		int idx = valWithUnits.indexOf(unitKey);
		valWithUnits = valWithUnits.substring(0, idx) + SYMBOL_POINT + valWithUnits.substring(idx + unitKey.length()) + unitKey;

		return valWithUnits;
	}

	private static double getFactor(String unitKey) throws IllegalArgumentException {
		if(unitKey.length() == 0) {
			return 1.0;
		}

		for(int i = 0; i < UNITS.length; i++) {
			Unit unit = UNITS[i];
			if(unit.hasKey(unitKey)) {
				return unit.factor;
			}
		}

		throw new IllegalArgumentException("'" + unitKey + "' is not a recognized unit");
	}

	public static String getFirstValue(String rawValue) {
		int slash = rawValue.indexOf('/');
		if(slash >= 0) {
			rawValue = rawValue.substring(0, slash);
		}

		return rawValue;
	}

	static class Unit {

		double factor;

		List keys = new ArrayList();

		public Unit(String chars, double factor) {
			this.factor = factor;

			StringTokenizer st = new StringTokenizer(chars, " ");
			while(st.hasMoreTokens()) {
				String key = st.nextToken();
				keys.add(key);
			}
		}

		public boolean hasKey(String key) {
			for(Iterator it = keys.iterator(); it.hasNext();) {
				String k = (String) it.next();
				if(k.equals(key)) {
					return true;
				}
			}
			return false;
		}

	}

}
