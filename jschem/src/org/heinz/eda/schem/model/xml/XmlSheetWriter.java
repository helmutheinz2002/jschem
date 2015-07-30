
package org.heinz.eda.schem.model.xml;

import java.util.Iterator;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.utils.xml.XmlObjectManager;
import org.heinz.framework.utils.xml.XmlObjectWriter;

public class XmlSheetWriter {

	public static String toXml(String indent, Sheet sheet) throws Exception {
		StringBuilder sb = new StringBuilder();

		XmlObjectWriter ow = new XmlObjectWriter(Sheet.PROPERTIES);
		sb.append(indent);
		sb.append(ow.toXml(sheet, true));
		sb.append("\n");

		String nindent = indent + XmlObjectManager.DEFAULT_INDENT;
		XmlComponentWriter cw = new XmlComponentWriter();
		for(Iterator it = sheet.components(); it.hasNext();) {
			AbstractComponent comp = (AbstractComponent) it.next();
			sb.append(cw.toXml(comp, nindent));

		}

		sb.append(indent);
		sb.append(ow.toXml(sheet, false));
		sb.append("\n");

		return sb.toString();
	}


}
