
package org.heinz.eda.schem.model.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;

public abstract class Handle implements Serializable {

	public static final int SIZE = 3;

	private final AbstractComponent owner;

	private final boolean visible;

	private final boolean sticky;

	private int connectedWires = 0;

	public Handle(AbstractComponent owner, boolean visible, boolean sticky) {
		this.owner = owner;
		this.visible = visible;
		this.sticky = sticky;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void draw(Graphics g, double zoom) {
		if(!visible) {
			return;
		}

		Point p = getPosition();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		g.setColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_HANDLE_FILL_COLOR));

		int px = (int) (p.x * zoom);
		int py = (int) (p.y * zoom);

		g.fillRect(px - SIZE, py - SIZE, 2 * SIZE, 2 * SIZE);
		g.setColor(owner.getColor());
		g.drawRect(px - SIZE, py - SIZE, 2 * SIZE, 2 * SIZE);
	}

	public boolean contains(int x, int y, int handleSize) {
		Point p = getPosition();
		p.translate(-handleSize, -handleSize);
		ExtRect r = new ExtRect(p, 2 * handleSize, 2 * handleSize);
		return r.contains(x, y);
	}

	protected Point getAbsolutePosition() {
		Point pos = getPosition();
		Point pp = owner.getLocation();
		pos = owner.getOrientation().transform(pos);
		pos.translate(pp.x, pp.y);
		return pos;
	}

	public AbstractComponent getOwner() {
		return owner.getToplevel();
	}

	public void setHandlePosition(Point rel, boolean fire, boolean dragging) {
		if(!visible) {
			return;
		}

		Point oldAbsPos = getAbsolutePosition();
		Point oldPos = getPosition();
		if(oldPos.equals(rel) && dragging) {
			return;
		}

		setPosition(rel, dragging);
		if(fire && sticky) {
			Map map = new HashMap();
			map.put(this, oldAbsPos);
			owner.fireHandlesMoved(map, rel, dragging);
		}
	}

	public HandlePosition getHandlePosition() {
		return new HandlePosition(getAbsolutePosition(), getPosition());
	}

	public void fireChanged() {
		owner.fireChanged();
	}

	public void fireWillChange() {
		owner.fireWillChange();
	}

	protected abstract void setPosition(Point rel, boolean dragging);

	protected abstract Point getPosition();

	public void setConnectedWires(int connectedWires) {
		fireWillChange();
		this.connectedWires = connectedWires;
		fireChanged();
	}

	public int getConnectedWires() {
		return connectedWires;
	}

	//--------------------------------------------

	public static class HandlePosition {

		public final Point absPos;

		public final Point pos;

		public HandlePosition(Point absPos, Point pos) {
			this.absPos = absPos;
			this.pos = pos;
		}

	}

}
