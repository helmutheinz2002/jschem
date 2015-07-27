package org.heinz.eda.schem.ui.tools;

import java.awt.Point;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Wire;

public class WireTool extends LineTool {
	public WireTool() {
		super("wiretool.png");
		stopOnHandle = true;
	}
	
	protected AbstractComponent createComponent(int x, int y) {
		return new Wire(x, y, 1, 1);
	}

	protected void done() {
		Wire wire = (Wire) getNewComponent();
		super.done();
		
		if(cancel)
			return;
		
		if((wire != null) && (!wire.isReleased()))
			sheetPanel.getSheet().checkSplit(wire);
	}
	
	protected boolean isUnacceptable(AbstractComponent newComponent, Point p) {
		Wire w = (Wire) newComponent;
		Point o = w.getOffset();
		boolean zero = (o.x == 0) && (o.y == 0);
		return zero;
	}
}
