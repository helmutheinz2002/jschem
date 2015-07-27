package org.heinz.framework.utils.xml;

import java.util.Map;

import org.xml.sax.Attributes;

public class XmlObjectReader extends XmlObjectManager {
	private String defaultClassPrefix;
	
	public XmlObjectReader(Map properties, String defaultClassPrefix) {
		super(properties);
		this.defaultClassPrefix = defaultClassPrefix;
	}
	
	public Object createObject(String className, Attributes attributes) throws Exception {
		String fullClassName = defaultClassPrefix + "." + className;
		Class objectClass = Class.forName(fullClassName);
		Object o = objectClass.newInstance();
		
		Map props = getPropertiesForClass(objectClass);
		for(int i=0; i<attributes.getLength(); i++) {
			String attr = attributes.getQName(i);
			
			XmlProperty prop = (XmlProperty) props.get(attr);
			if(prop != null) {
				String val = attributes.getValue(prop.name);
				prop.accessor.setValue(o, val);
			}
		}
		
		return o;
	}
}
