
package org.heinz.eda.schem.model.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.heinz.eda.schem.model.ArcType;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;


public class Arc extends AbstractComponent {

	static {
		PROPERTIES.put(new XmlProperty("radius", XmlPropertyConverterInteger.instance()), Arc.class);
		PROPERTIES.put(new XmlProperty("arcType", XmlPropertyConverterInteger.instance()), Arc.class);
	}

	private int radius;

	private int arcType;

	public Arc() {
	}

	public Arc(int x, int y, int radius, int arcType) {
		this(x, y, radius, arcType, null);
	}

	protected Arc(int x, int y, int radius, int arcType, Color fillColor) {
		super(x, y);
		this.radius = radius;
		this.arcType = arcType;
		setFillColor(getFillColor());
	}

	public Arc(Arc arc) {
		super(arc);
		radius = arc.radius;
		arcType = arc.arcType;
	}

	@Override
	public boolean hasBecomeInvalid() {
		return radius == 0;
	}

	@Override
	public void snapToGrid(int snapGrid) {
		fireWillChange();
		super.snapToGrid(snapGrid);
		setRadius(GridHelper.snapToGrid(radius, snapGrid));
		fireChanged();
	}

	@Override
	protected void addHandles() {
		addHandle(new Handle(this, true, false) {

			@Override
			protected Point getPosition() {
				return new Point(-radius, 0);
			}

			@Override
			public void setPosition(Point offset, boolean dragging) {
				offset = getOrientation().unTransform(offset);
				int newRad = radius - offset.x;
				if(newRad < 0) {
					newRad = 0;
				}
				setRadius(newRad);
			}

		});
	}

	@Override
	protected Point getCenterOffset() {
		return new Point(0, 0);
	}

	@Override
	protected void draw(Graphics g, double zoom, boolean selected) {
		drawArc(g, zoom, selected, getFillColor(selected), true, false);
	}

	protected void drawArc(Graphics g, double zoom, boolean selected, Color fillColor, boolean setStroke, boolean square) {
		if(setStroke) {
			setStroke(g, zoom);
		}

		ArcType at = ArcType.ARC_TYPES[arcType];
		int d = (int) (radius * zoom * 2);
		int r = (int) (radius * zoom);

		if(fillColor != null) {
			g.setColor(fillColor);
			if(square) {
				g.fillRect(-r, -r, d, d);
			} else {
				g.fillArc(-r, -r, d, d, at.startAngle, at.arcAngle);
			}
		}

		g.setColor(getColor(selected));
		if(square) {
			g.drawRect(-r, -r, d, d);
		} else {
			g.drawArc(-r, -r, d, d, at.startAngle, at.arcAngle);
		}
	}

	public void setRadius(int radius) {
		fireWillChange();
		this.radius = radius;
		fireChanged();
	}

	@Override
	protected ExtRect getBounds() {
		if(!isVisible()) {
			return null;
		}

		ArcType at = ArcType.ARC_TYPES[arcType];
		int d = 2 * radius;
		double rad = (double) at.startAngle * Math.PI / 180.0;
		int x = (int) (Math.cos(rad) * (double) radius);
		int y = (int) (Math.sin(rad) * (double) radius);
		switch(arcType) {
			case ArcType.ARC_300:
			case ArcType.ARC_210:
			case ArcType.ARC_HALF:
				return new ExtRect(-radius, -radius, radius + x, d);
			case ArcType.ARC_QUARTER:
				return new ExtRect(-radius, -y, radius + x, 2 * y);
			case ArcType.ARC_CORNER:
				return new ExtRect(-radius, -radius, radius, radius);
			case ArcType.ARC_FULL:
				return new ExtRect(-radius, -radius, d, d);
			default:
				break;
		}

		throw new UnsupportedOperationException("ArcType not supported");
	}

	@Override
	public boolean contains(int x, int y, int clickTolerance) {
		if(!isVisible()) {
			return false;
		}

		int dx = x;
		int dy = y;
		double d = Math.sqrt(dx * dx + dy * dy);

		int lw = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);
		clickTolerance += lw;

		boolean filled = (getFillColor() != null);

		if(((d - radius) > clickTolerance) || (!filled && ((radius - d) > clickTolerance))) {
			return false;
		}

		Point td = new Point(dx, dy);

		double as = Math.asin((double) td.y / d) * 360 / (2 * Math.PI);
		double ac = Math.acos((double) td.x / d) * 360 / (2 * Math.PI);

		int w = (int) ac;
		if(as > 0) {
			w = 360 - (int) ac;
		}

		ArcType at = ArcType.ARC_TYPES[arcType];
		int startDeg = at.startAngle;
		int endDeg = startDeg + at.arcAngle;

		return (w >= startDeg) && (w <= endDeg);
	}

	@Override
	public AbstractComponent duplicate() {
		return new Arc(this);
	}

	public int getArcType() {
		return arcType;
	}

	public void setArcType(int arcType) {
		fireWillChange();
		this.arcType = arcType;
		fireChanged();
	}

	public int getRadius() {
		return radius;
	}

}
