
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Pin;

public class PinTool extends AbstractNewTool {

	public PinTool() {
		super("pintool.png", true);
		fillColorBean.setColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR));
	}

	@Override
	protected AbstractComponent createComponent(int x, int y) {
		return new Pin(x, y);
	}

	protected boolean acceptObject(AbstractComponent newComponent, Point p) {
		return true;
	}

}
