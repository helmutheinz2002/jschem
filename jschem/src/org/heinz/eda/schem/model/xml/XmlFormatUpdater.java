package org.heinz.eda.schem.model.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;

public abstract class XmlFormatUpdater {
	public final int inVersion;
	
	public XmlFormatUpdater(int inVersion) {
		this.inVersion = inVersion;
	}
	
	public void updateSchematics(Schematics schematics) {
		List objects = getAllAbstractComponents(schematics);
		
		for(Iterator it=objects.iterator(); it.hasNext();) {
			AbstractComponent ac = (AbstractComponent) it.next();
			
			updateComponent(ac);
		}
	}
	
	public abstract void updateComponent(AbstractComponent ac);
	
	public static List getAllAbstractComponents(Schematics schematics) {
		List objects = new ArrayList();
		
		for(Iterator sit=schematics.sheets(); sit.hasNext();) {
			Sheet sheet = (Sheet) sit.next();
			
			for(Iterator cit=sheet.components(); cit.hasNext();) {
				AbstractComponent ac = (AbstractComponent) cit.next();
				objects.add(ac);
				if(ac.hasElements())
					addObjects(ac, objects);
			}
		}
		
		return objects;
	}

	private static void addObjects(AbstractComponent parent, List objects) {
		for(Iterator it=parent.elements(); it.hasNext();) {
			AbstractComponent ac = (AbstractComponent) it.next();
			objects.add(ac);
			if(ac.hasElements())
				addObjects(ac, objects);
		}
	}
}
