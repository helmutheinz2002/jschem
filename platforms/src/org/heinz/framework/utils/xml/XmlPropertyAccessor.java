
package org.heinz.framework.utils.xml;

public interface XmlPropertyAccessor {

	String formatValue(Object o);

	String getValue(Object o) throws Exception;

	void setValue(Object o, String value) throws Exception;

}
