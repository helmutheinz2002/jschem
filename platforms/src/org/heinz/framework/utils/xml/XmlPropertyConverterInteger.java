
package org.heinz.framework.utils.xml;

public class XmlPropertyConverterInteger extends XmlPropertyConverterPrimitive {

	protected static XmlPropertyConverter instance;

	@Override
	public Object parseValue(String s) {
		return new Integer(s);
	}

	public static XmlPropertyConverter instance() {
		if(instance == null) {
			instance = new XmlPropertyConverterInteger();
		}
		return instance;
	}

}
