package org.heinz.eda.schem.model.xml;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Symbol;
import org.heinz.eda.schem.model.components.Text;

public class XmlFormatUpdater1 extends XmlFormatUpdater {
	public XmlFormatUpdater1() {
		super(1);
	}

	public void updateComponent(AbstractComponent ac) {
		if(ac instanceof Symbol) {
			Text t = (Text) ac.elementAt(0);
			t.setKey(Symbol.KEY_NET_NAME);
		} else if(ac instanceof Component) {
			Text t1 = (Text) ac.elementAt(0);
			t1.setKey(Component.KEY_PART_ID);
			Text t2 = (Text) ac.elementAt(1);
			t2.setKey(Component.KEY_PART_NAME);
			Text t3 = (Text) ac.elementAt(2);
			t3.setKey(Component.KEY_ORDER_NO);
			ac.addAttributeText(Component.KEY_MODEL_NAME, "", 0, false);
		} else if(ac instanceof Pin) {
			Text t1 = (Text) ac.elementAt(0);
			t1.setKey(Pin.KEY_PIN_NO);
			Text t2 = (Text) ac.elementAt(1);
			t2.setKey(Pin.KEY_PIN_NAME);
		}
	}
}
