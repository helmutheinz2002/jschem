
package org.heinz.eda.schem.model.xml;

import java.util.Iterator;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.utils.xml.XmlObjectManager;
import org.heinz.framework.utils.xml.XmlObjectWriter;

public class XmlSchematicsWriter {

	public static String toXml(Schematics schematics) throws Exception {
		StringBuilder sb = new StringBuilder(XmlObjectWriter.XML_HEADER);

		sb.append("<").append(XmlObjectManager.getSimpleClassName(schematics.getClass())).append(" " + XmlFormatVersion.VERSION_ATTRIBUTE + "=\"").append(XmlFormatVersion.VERSION).append("\">\n");
		for(Iterator it = schematics.sheets(); it.hasNext();) {
			Sheet sheet = (Sheet) it.next();
			sb.append(XmlSheetWriter.toXml(XmlObjectManager.DEFAULT_INDENT, sheet));
		}
		sb.append("</").append(XmlObjectManager.getSimpleClassName(schematics.getClass())).append(">\n");
		return sb.toString();
	}

}
