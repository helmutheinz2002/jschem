
package org.heinz.eda.schem.model.xml;

import java.util.HashMap;
import java.util.Map;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.components.AbstractComponent;

public class XmlFormatVersionUpdate {

	private static final Map updaters = createUpdaters();

	public static void upgradeToCurrentVersion(int inVersion, Schematics schematics) {
		while(true) {
			if(inVersion >= XmlFormatVersion.VERSION) {
				break;
			}
			inVersion = upgradeToNextVersion(inVersion, schematics);
		}
	}

	public static void upgradeToCurrentVersion(int inVersion, AbstractComponent ac) {
		while(true) {
			if(inVersion >= XmlFormatVersion.VERSION) {
				break;
			}
			inVersion = upgradeToNextVersion(inVersion, ac);
		}
	}

	private static Map createUpdaters() {
		Map updaterMap = new HashMap();
		for(int i = 0; i < XmlFormatVersion.FORMAT_UPDATERS.length; i++) {
			updaterMap.put(i, XmlFormatVersion.FORMAT_UPDATERS[i]);
		}
		return updaterMap;
	}

	private static int upgradeToNextVersion(int inVersion, Schematics schematics) {
		XmlFormatUpdater updater = (XmlFormatUpdater) updaters.get(new Integer(inVersion));
		updater.updateSchematics(schematics);
		return inVersion + 1;
	}

	private static int upgradeToNextVersion(int inVersion, AbstractComponent ac) {
		XmlFormatUpdater updater = (XmlFormatUpdater) updaters.get(new Integer(inVersion));
		updater.updateComponent(ac);
		return inVersion + 1;
	}

}
