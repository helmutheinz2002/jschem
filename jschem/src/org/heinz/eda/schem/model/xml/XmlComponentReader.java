package org.heinz.eda.schem.model.xml;

import java.util.ArrayList;
import java.util.List;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.utils.xml.XmlObjectReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlComponentReader extends XmlObjectReader {
	private AbstractComponent parent;
	private List stack = new ArrayList();
	private List newComponents = new ArrayList();
	private DefaultHandler handler;
	private int version;
	
	public XmlComponentReader(final boolean autoUpdate) {
		super(AbstractComponent.PROPERTIES, AbstractComponent.class.getPackage().getName());
		
		handler = new DefaultHandler() {
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if(parent == null) {
					version = 0;
					if(autoUpdate) {
						String vs = attributes.getValue(XmlFormatVersion.VERSION_ATTRIBUTE);
						try {
							version = new Integer(vs).intValue();
						} catch(Exception ex) {
						}
					}
				}
				
				try {
					AbstractComponent c = (AbstractComponent) createObject(qName, attributes);
					
					stack.add(0, parent);
					if(parent != null)
						parent.addComponent(c);
					else
						newComponents.add(c);
					parent = c;
				} catch (Exception e) {
					e.printStackTrace();
					throw new SAXException(e);
				}
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				if(autoUpdate)
					XmlFormatVersionUpdate.upgradeToCurrentVersion(version, parent);
				parent = (AbstractComponent) stack.remove(0);
			}
		};
	}

	public DefaultHandler getHandler() {
		return handler;
	}
	
	public List getComponents() {
		return newComponents;
	}
	
	public void reset() {
		newComponents.clear();
	}
}
