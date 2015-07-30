
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.heinz.eda.schem.util.ExtRect;

public class ZoomTool extends SchemEditTool {

	public ZoomTool() {
		super("zoomtool.png");
	}

	@Override
	protected boolean handleKey(KeyEvent e) {
		return false;
	}

	@Override
	protected void handleMouseDown(MouseEvent e) {
	}

	@Override
	protected void handleMouseDrag(MouseEvent e) {
		Point p = e.getPoint();

		int ox = p.x - start.x;
		int oy = p.y - start.y;

		ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
		sheetPanel.drawSelectionFrame(frame);
	}

	@Override
	protected void handleMouseUp(MouseEvent e) {
		Point p = e.getPoint();

		int ox = p.x - start.x;
		int oy = p.y - start.y;

		if((ox == 0) || (oy == 0)) {
			return;
		}

		ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
		sheetPanel.drawSelectionFrame(null);
		sheetPanel.zoomInArea(frame.normalize());
		done();
	}

	@Override
	protected void handleCancel() {
		sheetPanel.drawSelectionFrame(null);
	}

}
