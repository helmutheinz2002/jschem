
package org.heinz.framework.utils.xml;

public class XmlPropertyConverterBoolean extends XmlPropertyConverterPrimitive {

	protected static XmlPropertyConverter instance;

	@Override
	public Object parseValue(String s) {
		if("false".equals(s)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public static XmlPropertyConverter instance() {
		if(instance == null) {
			instance = new XmlPropertyConverterBoolean();
		}
		return instance;
	}

}
