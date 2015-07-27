package org.heinz.framework.utils.xml;

public abstract class XmlPropertyConverterPrimitive implements XmlPropertyConverter {
	public String formatValue(Object o) {
		return "" + o;
	}
}
