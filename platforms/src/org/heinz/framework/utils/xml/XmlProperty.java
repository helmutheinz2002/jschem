
package org.heinz.framework.utils.xml;


public class XmlProperty {

	public final String name;

	public final XmlPropertyAccessor accessor;

	private XmlProperty(String name, XmlPropertyAccessor accessor) {
		this.name = name;
		this.accessor = accessor;
	}

	public XmlProperty(String name, XmlPropertyConverter converter) {
		this(name, new DefaultXmlPropertyAccessor(name, converter));
	}

}
