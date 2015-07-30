
package org.heinz.eda.schem.model.xml;

import java.util.Iterator;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.utils.xml.XmlObjectWriter;

public class XmlComponentWriter extends XmlObjectWriter {

	public XmlComponentWriter() {
		super(AbstractComponent.PROPERTIES);
	}

	public String toXml(AbstractComponent c) throws Exception {
		StringBuilder sb = new StringBuilder(XML_HEADER);
		sb.append(toXml(c, "", true));
		return sb.toString();
	}

	public String toXml(AbstractComponent c, String indent) throws Exception {
		return toXml(c, indent, false);
	}

	public String toXml(AbstractComponent c, String indent, boolean withVersion) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		String versionAttribute = "";
		if(withVersion) {
			versionAttribute = buildAttribute(XmlFormatVersion.VERSION_ATTRIBUTE, "" + XmlFormatVersion.VERSION);
		}
		sb.append(toXml(c, true, versionAttribute));

		if(c.hasElements()) {
			sb.append("\n");
		}

		String nindent = indent + DEFAULT_INDENT;
		for(Iterator it = c.elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			String s = toXml(child, nindent);
			sb.append(s);
		}

		if(c.hasElements()) {
			sb.append(indent);
		}
		sb.append(toXml(c, false));
		sb.append("\n");

		return sb.toString();
	}

}
