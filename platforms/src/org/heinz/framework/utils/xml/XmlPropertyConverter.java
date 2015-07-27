package org.heinz.framework.utils.xml;

public interface XmlPropertyConverter {
	String formatValue(Object o);
	Object parseValue(String s);
}
