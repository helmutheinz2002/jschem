
package org.heinz.framework.utils.xml;

public class XmlPropertyConverterDouble extends XmlPropertyConverterPrimitive {

	protected static XmlPropertyConverter instance;

	@Override
	public Object parseValue(String s) {
		return new Double(s);
	}

	public static XmlPropertyConverter instance() {
		if(instance == null) {
			instance = new XmlPropertyConverterDouble();
		}
		return instance;
	}

}
