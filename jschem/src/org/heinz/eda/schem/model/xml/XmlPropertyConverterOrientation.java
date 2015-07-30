
package org.heinz.eda.schem.model.xml;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.framework.utils.xml.XmlPropertyConverter;

public class XmlPropertyConverterOrientation implements XmlPropertyConverter {

	protected static XmlPropertyConverter instance;

	@Override
	public String formatValue(Object o) {
		Orientation orientation = (Orientation) o;
		return Integer.toString(orientation.key);
	}

	@Override
	public Object parseValue(String s) {
		int key = Integer.parseInt(s);
		return Orientation.getOrientation(key);
	}

	public static XmlPropertyConverter instance() {
		if(instance == null) {
			instance = new XmlPropertyConverterOrientation();
		}
		return instance;
	}

}
