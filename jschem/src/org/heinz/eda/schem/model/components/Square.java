package org.heinz.eda.schem.model.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;


public class Square extends AbstractComponent {
	static {
		PROPERTIES.put(new XmlProperty("width", XmlPropertyConverterInteger.instance()), Square.class);
		PROPERTIES.put(new XmlProperty("height", XmlPropertyConverterInteger.instance()), Square.class);
	}

	private int width;
	private int height;
	
	public Square() {
	}
	
	public Square(int x, int y, int width, int height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}

	public Square(Square square) {
		super(square);
		width = square.width;
		height = square.height;
	}

	public boolean hasBecomeInvalid() {
		return (width == 0) && (height == 0);
	}
	
	public void snapToGrid(int snapGrid) {
		fireWillChange();
		super.snapToGrid(snapGrid);
		int w = GridHelper.snapToGrid(width, snapGrid);
		int h = GridHelper.snapToGrid(height, snapGrid);
		setSize(new Dimension(w, h));
		fireChanged();
	}
	
	protected void addHandles() {
		addHandle(new Handle(this, true, false) {
			protected Point getPosition() {
				return new Point(0, 0);
			}

			public void setPosition(Point offset, boolean dragging) {
				Square.this.setPosition(getX() + offset.x, getY() + offset.y);
				offset = getOrientation().unTransform(offset);
				setWidth(width - offset.x);
				setHeight(height - offset.y);
			}
		});
		addHandle(new Handle(this, true, false) {
			protected Point getPosition() {
				return new Point(width, height);
			}

			public void setPosition(Point offset, boolean dragging) {
				offset = getOrientation().unTransform(offset);
				setWidth(width + offset.x);
				setHeight(height + offset.y);
			}
		});
	}
	
	protected void draw(Graphics g, double zoom, boolean selected) {
		ExtRect r = getBounds().normalize();

		setStroke(g, zoom);
		int rx = (int) ((double) r.x * zoom);
		int ry = (int) ((double) r.y * zoom);
		int rw = (int) ((double) r.width * zoom);
		int rh = (int) ((double) r.height * zoom);
		
		if(getFillColor() != null) {
			g.setColor(getFillColor(selected));
			g.fillRect(rx, ry, rw, rh);
		}
		
		g.setColor(getColor(selected));
		g.drawRect(rx, ry, rw, rh);
	}
	
	public void setSize(Dimension size) {
		fireWillChange();
		width = size.width;
		height = size.height;
		fireChanged();
	}

	protected ExtRect getBounds() {
		if(!isVisible())
			return null;
		
		ExtRect b = new ExtRect(0, 0, width, height);
		return b;
	}

	public boolean contains(int x, int y, int clickTolerance) {
		if(!isVisible())
			return false;

		int w = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);
		clickTolerance += w;

		ExtRect r = getBounds().normalize();
		int x2 = r.x + r.width;
		int y2 = r.y + r.height;
		
		boolean inY = (y >= (r.y - clickTolerance)) && (y<= (y2 + clickTolerance));
		boolean inX = (x >= (r.x - clickTolerance)) && (x<= (x2 + clickTolerance));
		
		if(getFillColor() != null)
			return inX && inY;
		
		if((Math.abs(x - r.x) < clickTolerance) && inY)
			return true;
		if((Math.abs(x - x2) < clickTolerance) && inY)
			return true;
		
		if((Math.abs(y - r.y) < clickTolerance) && inX)
			return true;
		if((Math.abs(y - y2) < clickTolerance) && inX)
			return true;
		
		return false;
	}

	public AbstractComponent duplicate() {
		return new Square(this);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		fireWillChange();
		this.height = height;
		fireChanged();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		fireWillChange();
		this.width = width;
		fireChanged();
	}
}
