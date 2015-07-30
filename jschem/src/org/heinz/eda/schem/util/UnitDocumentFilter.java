
package org.heinz.eda.schem.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class UnitDocumentFilter extends SimpleDocumentFilter {

	private final String accepted = "0123456789";

	@Override
	protected boolean accept(String s) {
		char dotChar = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();

		int dots = 0;
		int start = 0;

		if(s.length() == 0) {
			return true;
		}

		if(s.charAt(0) == '-') {
			start++;
		}

		int limit = s.length();
		if(s.endsWith("m")) {
			limit--;
		}
		if(s.endsWith("mm")) {
			limit--;
		}

		for(int i = start; i < limit; i++) {
			char c = s.charAt(i);
			if(accepted.indexOf(c) >= 0) {
				continue;
			}
			if(c == dotChar) {
				dots++;
				if(dots <= 1) {
					continue;
				}
			}
			return false;
		}

		int dot = s.indexOf('.');
		if(dot >= 0) {
			if((limit - dot) > 3) {
				return false;
			}
		}
		return true;
	}

}
