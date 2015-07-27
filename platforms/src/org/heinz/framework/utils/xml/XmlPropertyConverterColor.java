package org.heinz.framework.utils.xml;

import java.awt.Color;

public class XmlPropertyConverterColor implements  XmlPropertyConverter {
	protected static XmlPropertyConverter instance;
	
	public String formatValue(Object o) {
		Color c = (Color) o;
		int rgb = c.getRGB();
		return "" + rgb;
	}

	public Object parseValue(String s) {
		int i = Integer.parseInt(s);
		return new Color(i);
	}

	public static XmlPropertyConverter instance() {
		if(instance == null)
			instance = new XmlPropertyConverterColor();
		return instance;
	}
}
