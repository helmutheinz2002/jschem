package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Line;

public class LineTool extends AbstractNewTool {
	protected boolean stopOnHandle; 
	
	public LineTool() {
		super("linetool.png", false);
	}

	protected LineTool(String icon) {
		super(icon, false);
	}

	protected AbstractComponent createComponent(int x, int y) {
		return new Line(x, y, 0, 0);
	}

	protected void handleMouseDown(MouseEvent e) {
		AbstractComponent nc = getNewComponent();
		if(nc != null) {
			Point p = processMouseEvent(e, true);
			if(nc.isReleased())
				nc = null;
			
			if(nc != null)
				checkSize(p);
			done();
			
			if(nc != null) {
				Line l = (Line) nc;
				Handle h = (Handle) l.getHandles().get(1);
				List hl = sheetPanel.getSheet().getHandlesAt(h.getHandlePosition().absPos);
				boolean isOnHandle = (hl.size() > 1);
				if(stopOnHandle && isOnHandle)
					return;
			}
			start(e.getPoint());
		}
		super.handleMouseDown(e);
	}

	protected void handleMouseMove(MouseEvent e) {
		processMouseEvent(e, false);
	}
	
	protected void handleMouseUp(MouseEvent e) {
		processMouseEvent(e, false);
	}
	
	protected void handleMouseDrag(MouseEvent e) {
		processMouseEvent(e, false);
	}
	
	private Point processMouseEvent(MouseEvent e, boolean withHandles) {
		Point p = e.getPoint();
		Point pos = sheetPanel.constrainScreenPoint(p.x, p.y, false);
		Line line = (Line) getNewComponent();
		Point lp = line.getPosition();
		pos = new Point(pos.x - lp.x, pos.y - lp.y);
		line.setOffset(pos.x, pos.y, withHandles);
		return pos;
	}
}
