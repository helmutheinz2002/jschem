package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.heinz.eda.schem.util.ExtRect;

public class ZoomTool extends SchemEditTool {
	public ZoomTool() {
		super("zoomtool.png");
	}
	
	protected boolean handleKey(KeyEvent e) {
		return false;
	}
	
	protected void handleMouseDown(MouseEvent e) {
	}

	protected void handleMouseDrag(MouseEvent e) {
		Point p = e.getPoint();

		int ox = p.x - start.x;
		int oy = p.y - start.y;

		ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
		sheetPanel.drawSelectionFrame(frame);
	}

	protected void handleMouseUp(MouseEvent e) {
		Point p = e.getPoint();

		int ox = p.x - start.x;
		int oy = p.y - start.y;
		
		if((ox == 0) || (oy == 0))
			return;

		ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
		sheetPanel.drawSelectionFrame(null);
		sheetPanel.zoomInArea(frame.normalize());
		done();
	}

	protected void handleCancel() {
		sheetPanel.drawSelectionFrame(null);
	}
}
