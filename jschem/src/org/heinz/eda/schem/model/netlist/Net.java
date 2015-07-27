package org.heinz.eda.schem.model.netlist;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Wire;

public class Net implements Comparable {
	private final String netName;
	private Set handlePositions = new HashSet();
	private List pins = new ArrayList();
	public int netNo = -1;
	
	public Net(String netName) {
		this.netName = netName;
	}
	
	public void addPoint(Point handlePos) {
		handlePositions.add(handlePos);
	}
	
	public void addWire(Wire wire) {
		List sh = wire.getStickyHandles();
		
		for(Iterator it=sh.iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			addPoint(h.getHandlePosition().absPos);
		}
	}
	
	public void addPin(Component component, Pin pin) {
		pins.add(new PinInfo(component, pin));
	}
	
	public boolean containsPoint(Point absPos) {
		return handlePositions.contains(absPos);
	}
	
	public String toString() {
		return "Net:'" + netName + "' Points:" + handlePositions.size() + " Pins:" + pins.size(); 
	}

	public int compareTo(Object o) {
		return netNo - ((Net) o).netNo;
	}
	
	//-----------------------------------------------------------------
	
	class PinInfo {
		Component component;
		Pin pin;
		
		public PinInfo(Component component, Pin pin) {
			this.component = component;
			this.pin = pin;
		}
	}
	
}

