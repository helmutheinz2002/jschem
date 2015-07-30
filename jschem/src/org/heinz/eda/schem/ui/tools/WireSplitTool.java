
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.heinz.eda.schem.model.components.AbstractComponent;

public class WireSplitTool extends SchemEditTool {

	private boolean splitted;

	public WireSplitTool() {
		super("newcornertool.png");
	}

	@Override
	protected void setup() {
		sheetPanel.clearSelection();
	}

	@Override
	protected boolean handleKey(KeyEvent e) {
		return false;
	}

	@Override
	protected void handleCancel() {
	}

	@Override
	protected void handleMouseDown(MouseEvent e) {
	}

	@Override
	protected void handleMouseDrag(MouseEvent e) {
	}

	@Override
	protected void handleMouseUp(MouseEvent e) {
		Point p = e.getPoint();
		AbstractComponent c = sheetPanel.findComponentOnScreen(p);
		if(c == null) {
			return;
		}

		try {
			splitted = sheetPanel.checkSplitOnScreen(p);
		} catch(Exception ex) {
		}

		if(splitted) {
			done();
		}
	}

	@Override
	protected void done() {
		super.done();
		splitted = false;
	}

}
