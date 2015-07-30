
package org.heinz.eda.schem.util;


public class IntegerDocumentFilter extends SimpleDocumentFilter {

	@Override
	protected boolean accept(String s) {
		for(int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if(!Character.isDigit(ch)) {
				return false;
			}
		}

		return true;
	}

}
