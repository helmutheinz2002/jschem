package org.heinz.framework.utils.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class XmlObjectManager {
	public static final String DEFAULT_INDENT = "   ";
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
	
	private static Map propertyCache = new HashMap();
	
	private Map properties;

	public XmlObjectManager(Map properties) {
		this.properties = properties;
	}

	public Map getPropertiesForClass(Class clazz) {
		Map props = (Map) propertyCache.get(clazz);
		if(props != null)
			return props;
		
		props = new HashMap();

		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			XmlProperty prop = (XmlProperty) it.next();
			Class c = (Class) properties.get(prop);
			if (c.isAssignableFrom(clazz))
				props.put(prop.name, prop);
		}
		
		propertyCache.put(clazz, props);
		return props;
	}
	
	public static String getSimpleClassName(Class clazz) {
		String className = clazz.getName();
		int idx = className.lastIndexOf('.');
		className = className.substring(idx + 1);
		
		return className;
	}
}
