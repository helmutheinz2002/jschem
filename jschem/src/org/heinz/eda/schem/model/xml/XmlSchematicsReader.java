package org.heinz.eda.schem.model.xml;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.framework.utils.xml.XmlObjectManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSchematicsReader {
	private Schematics schematics;
	private DefaultHandler handler;
	private XmlSheetReader sheetReader;
	private int version;
	
	public XmlSchematicsReader() {
		final String schematicsClassName = XmlObjectManager.getSimpleClassName(Schematics.class);

		handler = new DefaultHandler() {
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if(schematics == null) {
					String vs = attributes.getValue(XmlFormatVersion.VERSION_ATTRIBUTE);
					try {
						version = new Integer(vs).intValue();
					} catch(Exception ex) {
					}
					schematics = new Schematics();
				} else {
					if(sheetReader == null)
						sheetReader = new XmlSheetReader();
					sheetReader.getHandler().startElement(uri, localName, qName, attributes);
				}
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				if(qName.equals(schematicsClassName)) {
					schematics.addSheets(sheetReader.getSheets());
					XmlFormatVersionUpdate.upgradeToCurrentVersion(version, schematics);
				} else {
					sheetReader.getHandler().endElement(uri, localName, qName);
				}
			}
		};
	}
	
	public DefaultHandler getHandler() {
		return handler;
	}
	
	public Schematics getSchematics() {
		return schematics;
	}
}
