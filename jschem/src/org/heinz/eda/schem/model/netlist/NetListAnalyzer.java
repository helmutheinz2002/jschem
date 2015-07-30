
package org.heinz.eda.schem.model.netlist;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Symbol;
import org.heinz.eda.schem.model.components.Wire;

public class NetListAnalyzer {

	public static final String NET_NAME_GROUND = "Ground";

	private List nets = new ArrayList();

	private Map pinsByComponent = new HashMap();

	public NetListAnalyzer(List sheets) {
		List wires = Schematics.getComponentList(sheets, Wire.class);
		List components = Schematics.getComponentList(sheets, Component.class);
		Map symbols = sortSymbolsByName(Schematics.getComponentList(sheets, Symbol.class));

		// Create a named net for each symbol
		for(Iterator nit = symbols.keySet().iterator(); nit.hasNext();) {
			String netName = (String) nit.next();
			List netSymbols = (List) symbols.get(netName);

			// Create a net
			Net net = new Net(netName);
			nets.add(net);
			if(NET_NAME_GROUND.equals(netName)) {
				net.netNo = 0;
			}

			// Collect pin positions for all symbols 
			for(Iterator sit = netSymbols.iterator(); sit.hasNext();) {
				Symbol symbol = (Symbol) sit.next();
				addHandlePositions(net, symbol);
			}
		}

		// Map wires to nets
		int netNr = 0;
		while(true) {
			// Check each wire if it has contact to a net
			boolean netFound = false;
			for(Iterator wit = wires.iterator(); wit.hasNext();) {
				Wire wire = (Wire) wit.next();
				Net net = findNetForWire(nets, wire);
				if(net != null) {
					wit.remove();
					net.addWire(wire);
					netFound = true;
				}
			}

			// Finished, no more wires left
			if(wires.isEmpty()) {
				break;
			}

			// No wire could be attached to an existing net, so we create a new one
			if(!netFound) {
				Net net = new Net("Anonymous" + netNr);
				nets.add(net);
				netNr++;
				net.addWire((Wire) wires.remove(0));
			}
		}

		// Add pins to nets
		for(Iterator cit = components.iterator(); cit.hasNext();) {
			Component component = (Component) cit.next();
			connectComponentToNets(nets, component);
		}

		numberNets(nets);
		Collections.sort(nets);
	}

	private void numberNets(List nets) {
		// Number nets
		int netNo = 1;
		for(Iterator nit = nets.iterator(); nit.hasNext();) {
			Net net = (Net) nit.next();
			if(net.netNo < 0) {
				net.netNo = netNo;
				netNo++;
			}
		}
	}

	private void addHandlePositions(Net net, Symbol symbol) {
		for(Iterator it = getPins(symbol).iterator(); it.hasNext();) {
			Pin pin = (Pin) it.next();
			// Pins have only one handle
			Handle h = (Handle) pin.getStickyHandles().get(0);
			net.addPoint(h.getHandlePosition().absPos);
		}
	}

	private List getPins(Component component) {
		List components = new ArrayList();
		components.add(component);
		Map pins = new HashMap();
		Sheet.collectPins(components, pins);

		return new ArrayList(pins.keySet());
	}

	private void connectComponentToNets(List nets, Component component) {
		List pinNetInfos = new ArrayList();
		pinsByComponent.put(component, pinNetInfos);

		// Get all pins of the component and connect them to nets
		for(Iterator hit = getPins(component).iterator(); hit.hasNext();) {
			Pin pin = (Pin) hit.next();
			// Pins have only one handle
			Handle handle = (Handle) pin.getStickyHandles().get(0);
			Point absPos = (Point) handle.getHandlePosition().absPos;

			Net net = findNetByHandlePosition(nets, absPos);
			if(net == null) {
				// This pin is not connected. create an anonymous net
				String compName = component.getAttributeText(Component.KEY_PART_ID).getText();
				String pinName = pin.getAttributeText(Pin.KEY_PIN_NO).getText();
				net = new Net(compName + ",Pin " + pinName);
				net.addPoint(absPos);
				nets.add(net);
			}

			net.addPin(component, pin);
			pinNetInfos.add(new PinNetInfo(pin, net));
		}

		Collections.sort(pinNetInfos);
	}

	private Net findNetForWire(List nets, Wire wire) {
		// Get the wire's handles and their positions
		List handles = wire.getStickyHandles();

		// Check if a net has a handle at the same location
		for(Iterator hit = handles.iterator(); hit.hasNext();) {
			Handle handle = (Handle) hit.next();
			Point absPos = (Point) handle.getHandlePosition().absPos;
			Net net = findNetByHandlePosition(nets, absPos);
			if(net != null) {
				return net;
			}
		}

		return null;
	}

	private Net findNetByHandlePosition(List nets, Point absPos) {
		for(Iterator nit = nets.iterator(); nit.hasNext();) {
			Net net = (Net) nit.next();
			if(net.containsPoint(absPos)) {
				return net;
			}
		}

		return null;
	}

	private Map sortSymbolsByName(List symbols) {
		Map sbn = new HashMap();
		for(Iterator it = symbols.iterator(); it.hasNext();) {
			Symbol s = (Symbol) it.next();
			String netName = s.getAttributeText(Symbol.KEY_NET_NAME).getText();
			if(netName.length() == 0) {
				continue;
			}

			List net = (List) sbn.get(netName);
			if(net == null) {
				net = new ArrayList();
				sbn.put(netName, net);
			}

			net.add(s);
		}
		return sbn;
	}

	public List getNets() {
		return nets;
	}

	public List getPinsForComponent(Component component) {
		return (List) pinsByComponent.get(component);
	}

	public Iterator components() {
		return pinsByComponent.keySet().iterator();
	}

	//-----------------------------------------------------------------

	public class PinNetInfo implements Comparable {

		public final Pin pin;

		public final Net net;

		public PinNetInfo(Pin pin, Net net) {
			this.net = net;
			this.pin = pin;
		}

		@Override
		public int compareTo(Object o) {
			int n1 = 0;
			int n2 = 0;

			try {
				n1 = new Integer(pin.getAttributeText(Pin.KEY_PIN_NO).getText());
			} catch(Exception e) {
			}

			PinNetInfo pi = (PinNetInfo) o;
			try {
				n2 = new Integer(pi.pin.getAttributeText(Pin.KEY_PIN_NO).getText());
			} catch(Exception e) {
			}

			return n1 - n2;
		}

	}

}
