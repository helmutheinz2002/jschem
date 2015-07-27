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
	
	protected void setup() {
		sheetPanel.clearSelection();
	}

	protected boolean handleKey(KeyEvent e) {
		return false;
	}
	
	protected void handleCancel() {
	}

	protected void handleMouseDown(MouseEvent e) {
	}

	protected void handleMouseDrag(MouseEvent e) {
	}

	protected void handleMouseUp(MouseEvent e) {
		Point p = e.getPoint();
		AbstractComponent c = sheetPanel.findComponentOnScreen(p);
		if(c == null)
			return;
		
		try {
			splitted = sheetPanel.checkSplitOnScreen(p);
		} catch(Exception ex) {
		}
		
		if(splitted)
			done();
	}

	protected void done() {
		super.done();
		splitted = false;
	}
}
