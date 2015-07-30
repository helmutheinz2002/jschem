
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Polygon;

public class PolygonTool extends AbstractNewTool {

	private int lastIndex;

	public PolygonTool() {
		super("polygontool.png", true);
	}

	@Override
	protected AbstractComponent createComponent(int x, int y) {
		lastIndex = 0;
		return new Polygon(x, y);
	}

	@Override
	protected void handleCancel() {
		if(getNewComponent() != null) {
			Polygon poly = (Polygon) getNewComponent();
			poly.removeLastPoint();
		}
		done();
	}

	@Override
	protected void handleMouseDown(MouseEvent e) {
		if(getNewComponent() == null) {
			super.handleMouseDown(e);
		}

		Polygon poly = (Polygon) getNewComponent();
		Point p = e.getPoint();
		Point pos = sheetPanel.constrainScreenPoint(p.x, p.y, false);
		lastIndex = poly.addPoint(pos.x, pos.y);
	}

	@Override
	protected void handleMouseMove(MouseEvent e) {
		processMouseEvent(e);
	}

	@Override
	protected void handleMouseUp(MouseEvent e) {
		processMouseEvent(e);
	}

	@Override
	protected void handleMouseDrag(MouseEvent e) {
		processMouseEvent(e);
	}

	private void processMouseEvent(MouseEvent e) {
		Point p = e.getPoint();
		Point pos = sheetPanel.constrainScreenPoint(p.x, p.y, false);
		Polygon poly = (Polygon) getNewComponent();
		Point pp = poly.getPosition();
		pos = new Point(pos.x - pp.x, pos.y - pp.y);
		poly.setPointAt(lastIndex, pos.x, pos.y);
	}

}
