package org.heinz.framework.utils.xml;

import java.util.Iterator;
import java.util.Map;

public class XmlObjectWriter extends XmlObjectManager {
	public XmlObjectWriter(Map properties) {
		super(properties);
	}

	public String toXml(Object o, boolean start) throws Exception {
		return toXml(o, start, "");
	}
	
	public String toXml(Object o, boolean start, String additionalAttributes) throws Exception {
		Class clazz = o.getClass();
		StringBuffer sb = new StringBuffer();
		String className = getSimpleClassName(clazz);

		sb.append("<");
		if (!start)
			sb.append("/");
		sb.append(className);
		
		if(start) {
			Map props = getPropertiesForClass(clazz);

			for (Iterator it = props.values().iterator(); it.hasNext();) {
				XmlProperty prop = (XmlProperty) it.next();
				String s = prop.accessor.getValue(o);
				if(s != null)
					sb.append(buildAttribute(prop.name, s));
			}
		}
		
		if((additionalAttributes.length() > 0) && !additionalAttributes.startsWith(" "))
			sb.append(" ");
		sb.append(additionalAttributes);
		sb.append(">");

		return sb.toString();
	}
	
	protected String buildAttribute(String name, String value) {
		StringBuffer sb = new StringBuffer();
		value = xmlise(value);
		sb.append(" ");
		sb.append(name);
		sb.append("=\"");
		sb.append(value);
		sb.append("\"");
		
		return sb.toString();
	}

	public String getTag(Object o, boolean closing) {
		String className = getSimpleClassName(o.getClass());

		StringBuffer sb = new StringBuffer();
		sb.append("<");
		sb.append(className);
		if (closing)
			sb.append("/");
		sb.append(">");

		return sb.toString();
	}

	private String xmlise(String s) {
		s = replaceAll(s, '&', "&amp;");
		s = replaceAll(s, '\"', "&quot;");
		return s;
	}

	private String replaceAll(String s, char tok, String repl) {
		int start = 0;
		while (true) {
			int idx = s.indexOf(tok, start);
			if (idx < 0)
				return s;
			s = s.substring(0, idx) + repl + s.substring(idx + 1);
			start += repl.length();
		}
	}
}
