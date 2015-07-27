/**
 * 
 */
package org.heinz.eda.schem.model.xml;

import java.util.StringTokenizer;

import org.heinz.eda.schem.model.SheetSize;
import org.heinz.framework.utils.xml.XmlPropertyConverter;

public class XmlPropertyConverterSheetSize implements XmlPropertyConverter {
	public String formatValue(Object o) {
		SheetSize c = (SheetSize) o;
		return "" + c.key + "," + c.width + "," + c.height;
	}

	public Object parseValue(String s) {
		StringTokenizer st = new StringTokenizer(s, ",");
		int i = Integer.parseInt(st.nextToken());
		if(i >= 0)
			return SheetSize.SIZES[i];
		int w = Integer.parseInt(st.nextToken());
		int h = Integer.parseInt(st.nextToken());
		return new SheetSize(w, h);
	}
}