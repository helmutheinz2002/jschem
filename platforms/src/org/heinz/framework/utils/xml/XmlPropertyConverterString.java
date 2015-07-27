package org.heinz.framework.utils.xml;


public class XmlPropertyConverterString implements XmlPropertyConverter {
	protected static XmlPropertyConverter instance;
	
	public String formatValue(Object o) {
		return (String) o;
	}

	public Object parseValue(String s) {
		return (String) s;
	}

	public static XmlPropertyConverter instance() {
		if(instance == null)
			instance = new XmlPropertyConverterString();
		return instance;
	}
}
