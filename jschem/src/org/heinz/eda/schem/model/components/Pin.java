package org.heinz.eda.schem.model.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import org.heinz.eda.schem.model.ArcType;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;

public class Pin extends Arc {
	public static final String KEY_PIN_NO = "PIN_NR";
	public static final String KEY_PIN_NAME = "PIN_NAME";
	
	private static final Font font = new Font("SansSerif", Font.PLAIN, 10);
	private static final boolean DEBUG = false; 
	
	private boolean smartJunctionsOutline = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE);
	
	public Pin() {
	}
	
	public Pin(int x, int y) {
		super(x, y, SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_PIN_RADIUS), ArcType.ARC_FULL, SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR));
		
		int ay = 0;
		ay = addAttributeText(KEY_PIN_NO, "", ay, false);
		ay = addAttributeText(KEY_PIN_NAME, "", ay, false);
	}

	public Pin(Pin pin) {
		super(pin);
	}
	
	protected void addHandles() {
		addHandle(new Handle(this, false, true) {
			protected Point getPosition() {
				return new Point(0, 0);
			}

			public void setPosition(Point offset, boolean dragging) {
			}
		});
	}
	
	protected void draw(Graphics g, double zoom, boolean selected) {
		Handle h = (Handle) getHandles().get(0);
		boolean drawPin = true;
		
		int wires = 0;
		try {
			if(hasSmartJunctions())
				wires = h.getConnectedWires();
			else
				wires = 1;
		} catch(Exception e) {
		}
		
		drawPin = (wires <= 1);
		if(DEBUG) {
			g.setColor(Color.black);
			g.setFont(font);
			g.drawString(""+wires, 10, -6);
		}
		
		if(drawPin || smartJunctionsOutline)
			super.drawArc(g, zoom, selected, drawPin ? getFillColor(selected) : null, false, !drawPin);
	}

	protected void init() {
		super.init();
		
		setFillColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR));
		super.setRadius(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_PIN_RADIUS));
	}

	protected ExtRect getBounds() {
		if(!isVisible())
			return null;
		
		ExtRect b = super.getBounds();
		ExtRect r = getChildBounds();
		
		if(r != null)
			b.add(r);
		
		return b;
	}
	
	public boolean contains(int x, int y, int clickTolerance) {
		boolean r = super.contains(x, y, clickTolerance);
		if(!r)
			return childContains(x, y, clickTolerance) != null;
		
		return true;
	}
	
	public AbstractComponent duplicate() {
		return new Pin(this);
	}
	
	public void setArcType(int arcType) {
	}

	public void setRadius(int radius) {
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE)) {
			smartJunctionsOutline = ((Boolean) e.getNewValue()).booleanValue();
			fireChanged();
		} else
			super.propertyChange(e);
	}
}
