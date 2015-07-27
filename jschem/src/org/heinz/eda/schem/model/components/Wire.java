package org.heinz.eda.schem.model.components;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;

public class Wire extends Line {
	private int cornerRadius = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_CORNER_RADIUS);
	private boolean smartJunctionsOutline = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE);
	
	public Wire() {
	}
	
	public Wire(int x, int y, int dx, int dy) {
		super(x, y, dx, dy);
	}

	public Wire(Wire wire) {
		super(wire);
	}
	
	protected void init() {
		stickyHandles = true;
		super.init();
	}

	static final Font font = new Font("SansSerif", Font.PLAIN, 10);
	static final boolean DEBUG = false; 
	
	protected void draw(Graphics g, double zoom, boolean selected) {
		super.draw(g, zoom, selected);
		
		int r = (int) (cornerRadius * zoom);
		int d = 2 * r;

		for(Iterator it=getHandles().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			int wires = 0;
			try {
				if(hasSmartJunctions())
					wires = h.getConnectedWires();
				else
					wires = 1;
			} catch(Exception e) {
			}
			
			Point p = h.getHandlePosition().pos;
			int x2 = (int) (p.x * zoom);
			int y2 = (int) (p.y * zoom);
			if(DEBUG) {
				g.setFont(font);
				g.drawString(""+wires, x2+10, y2+16);
			}
			
			if(wires == 2) {
				if(smartJunctionsOutline) {
					setStroke(g, 0, zoom, false);
					g.drawRect(x2-r, y2-r, d, d);
				}
			} else {
				setStroke(g, zoom);
				g.fillArc(x2-r, y2-r, d, d, 0, 360);
			}
		}
	}
	
	protected ExtRect getBounds() {
		ExtRect b = super.getBounds();
		if(b == null)
			return null;
		
		int d = 2 * cornerRadius;
		b.add(new ExtRect(-cornerRadius, -cornerRadius, d, d));
		b.add(new ExtRect(getDx() - cornerRadius, getDy() - cornerRadius, d, d));
		return b;
	}
	
	public AbstractComponent duplicate() {
		return new Wire(this);
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);
		
		if(e.getPropertyName().equals(SchemOptions.PROPERTY_CORNER_RADIUS)) {
			cornerRadius = ((Integer) e.getNewValue()).intValue();
			fireChanged();
		} else if(e.getPropertyName().equals(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE)) {
			smartJunctionsOutline = ((Boolean) e.getNewValue()).booleanValue();
			fireChanged();
		}
	}
}
