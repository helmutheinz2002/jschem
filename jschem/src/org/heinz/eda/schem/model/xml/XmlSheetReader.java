package org.heinz.eda.schem.model.xml;

import java.util.ArrayList;
import java.util.List;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.utils.xml.XmlObjectReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSheetReader extends XmlObjectReader {
	private DefaultHandler handler;
	private List sheets = new ArrayList();
	private Sheet sheet;
	private XmlComponentReader componentReader;
	
	public XmlSheetReader() {
		super(Sheet.PROPERTIES, Sheet.class.getPackage().getName());
		
		final String sheetClassName = getSimpleClassName(Sheet.class);
		componentReader = new XmlComponentReader(false);
		
		handler = new DefaultHandler() {
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if(sheet == null) {
					try {
						sheet = (Sheet) createObject(qName, attributes);
						sheets.add(sheet);
					} catch (Exception e) {
						sheet = null;
						e.printStackTrace();
						throw new SAXException(e);
					}
				} else {
					componentReader.getHandler().startElement(uri, localName, qName, attributes);
				}
			}
			
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if(qName.equals(sheetClassName)) {
					sheet.addComponents(componentReader.getComponents());
					componentReader.reset();
					sheet = null;
				} else {
					componentReader.getHandler().endElement(uri, localName, qName);
				}
			}
		};
	}
	
	public DefaultHandler getHandler() {
		return handler;
	}
	
	public List getSheets() {
		return sheets;
	}
}
