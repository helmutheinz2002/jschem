package org.heinz.eda.schem.model.xml;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Symbol;

public class XmlFormatUpdater2 extends XmlFormatUpdater {
	public XmlFormatUpdater2() {
		super(2);
	}

	public void updateComponent(AbstractComponent ac) {
		if((ac instanceof Component) && !(ac instanceof Symbol)) {
			ac.addAttributeText(Component.KEY_SUB_CIRCUIT, "", 0, false);
		}
	}
}
