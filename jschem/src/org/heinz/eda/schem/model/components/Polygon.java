
package org.heinz.eda.schem.model.components;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.xml.XmlPropertyConverterPoints;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.eda.schem.util.LineHelper;
import org.heinz.framework.utils.xml.XmlProperty;

public class Polygon extends AbstractComponent {

	static {
		PROPERTIES.put(new XmlProperty("points", XmlPropertyConverterPoints.instance()), Polygon.class);
	}

	private List points = new ArrayList();

	public Polygon() {
	}

	public Polygon(int x, int y) {
		super(x, y);
		addPoint(x, y);
	}

	public Polygon(Polygon polygon) {
		super(polygon);
		points = new ArrayList(polygon.points);
	}

	@Override
	public AbstractComponent duplicate() {
		return new Polygon(this);
	}

	@Override
	public void snapToGrid(int snapGrid) {
		fireWillChange();

		super.snapToGrid(snapGrid);

		int idx = 0;
		for(Iterator it = points.iterator(); it.hasNext(); idx++) {
			Point p = (Point) it.next();
			int x = GridHelper.snapToGrid(p.x, snapGrid);
			int y = GridHelper.snapToGrid(p.y, snapGrid);
			setPointAt(idx, x, y);
		}

		fireChanged();
	}

	public Point getPointAt(int idx) {
		return (Point) points.get(idx);
	}

	public void setPointAt(int idx, int x, int y) {
		fireWillChange();
		Point p = (Point) points.get(idx);
		p.x = x;
		p.y = y;
		fireChanged();
	}

	public void removeLastPoint() {
		fireWillChange();
		points.remove(points.size() - 1);
		Handle h = (Handle) getHandles().get(getHandles().size() - 1);
		removeHandle(h);
		fireChanged();
	}

	public final int addPoint(int x, int y) {
		fireWillChange();
		Point p = new Point(x - getX(), y - getY());
		int idx = points.size();
		points.add(p);
		addHandle(idx);
		fireChanged();
		return points.size() - 1;
	}

	private void addHandle(final int idx) {
		addHandle(new Handle(this, true, false) {

			@Override
			protected Point getPosition() {
				return new Point(getPointAt(idx));
			}

			@Override
			public void setPosition(Point offset, boolean dragging) {
				Point p = getPointAt(idx);
				fireWillChange();
				p.x += offset.x;
				p.y += offset.y;
				fireChanged();
			}

		});
	}

	@Override
	public List getHandles() {
		if(super.getHandles().isEmpty()) {
			for(int i = 0; i < points.size(); i++) {
				addHandle(i);
			}
		}
		return super.getHandles();
	}

	@Override
	protected void draw(Graphics g, double zoom, boolean selected) {
		setStroke(g, zoom, true);
		java.awt.Polygon poly = getPolygon(zoom);

		if(getFillColor() != null) {
			g.setColor(getFillColor(selected));
			g.fillPolygon(poly);
		}

		g.setColor(getColor(selected));
		g.drawPolygon(poly);
	}

	private java.awt.Polygon getPolygon(double zoom) {
		int l = points.size();
		int x[] = new int[l];
		int y[] = new int[l];
		int idx = 0;
		for(Iterator it = points.iterator(); it.hasNext(); idx++) {
			Point p = (Point) it.next();
			x[idx] = (int) ((double) p.x * zoom);
			y[idx] = (int) ((double) p.y * zoom);
		}

		return new java.awt.Polygon(x, y, l);
	}

	@Override
	protected ExtRect getBounds() {
		if(!isVisible()) {
			return null;
		}

		ExtRect bb = new ExtRect(0, 0, 0, 0);
		for(int i = 1; i < points.size(); i++) {
			Point p = (Point) points.get(i);
			bb.add(p);
		}
		return bb;
	}

	@Override
	public boolean contains(int x, int y, int clickTolerance) {
		if(!isVisible()) {
			return false;
		}

		if(getFillColor() != null) {
			java.awt.Polygon poly = getPolygon(1.0);
			boolean hit = poly.contains(x, y);
			if(hit) {
				return true;
			}
		}

		int w = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);
		clickTolerance += w;

		int l = points.size();
		for(int i = 0; i < l; i++) {
			Point p1 = (Point) points.get(i);
			Point p2 = (Point) points.get((i + 1) % l);
			boolean hit = LineHelper.contains(x, y, p1.x, p1.y, p2.x - p1.x, p2.y - p1.y, clickTolerance);
			if(hit) {
				return true;
			}
		}
		return false;
	}

	public List getPoints() {
		return points;
	}

	public void setPoints(List points) {
		this.points = points;
	}

}
