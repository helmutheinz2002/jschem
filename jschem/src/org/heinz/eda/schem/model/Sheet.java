
package org.heinz.eda.schem.model;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Line;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Square;
import org.heinz.eda.schem.model.components.Symbol;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.model.components.Wire;
import org.heinz.eda.schem.model.xml.XmlPropertyConverterSheetSize;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterString;

public class Sheet implements ComponentListener {

	public static final Map PROPERTIES = new HashMap();

	static {
		PROPERTIES.put(new XmlProperty("title", XmlPropertyConverterString.instance()), Sheet.class);
		PROPERTIES.put(new XmlProperty("size", new XmlPropertyConverterSheetSize()), Sheet.class);
	}

	public static final String PROPERTY_TITLE = "Sheet.title";

	public static final String PROPERTY_SIZE = "Sheet.size";

	public static final String PROPERTY_DIRTY = "Sheet.dirty";

	private static final String SHEET_INFO_ID = "SheetInfo";

	private static final String KEY_SHEETINFO_AUTHOR = "DESIGNER";

	private static final String KEY_SHEETINFO_COMPANY = "COMPANY_NAME";

	private static final String KEY_SHEETINFO_PAGE = "PAGE_NR";

	private final List listeners = new ArrayList();

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private List components = new ArrayList();

	private String title = "";

	private SheetSize size;

	private boolean dirty;

	private List dragNeighbourHandles;

	private final Map connectedObjectsByHandle = new HashMap();

	private final Map handlesByPosition = new HashMap();

	private final Set checkingAttachHandles = new HashSet();

	public Sheet() {
		this("", SheetSize.SIZES[SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_DEFAULT_SHEET_SIZE)], false);
	}

	public Sheet(String title, SheetSize size, boolean init) {
		setTitle(title);
		this.size = size;
		if(init) {
			init();
		}
	}

	public void toFront(AbstractComponent c) {
		components.remove(c);
		components.add(c);
	}

	public void toBack(AbstractComponent c) {
		components.remove(c);
		components.add(0, c);
	}

	public void enforceSnapGrid(List components, int snapGrid) {
		for(Iterator it = new ArrayList(components).iterator(); it.hasNext();) {
			AbstractComponent comp = (AbstractComponent) it.next();
			comp.snapToGrid(snapGrid);
			if(comp.isGroup()) {
				enforceSnapGrid(comp.getComponents(), snapGrid);
			}
		}
	}

	public static void collectPins(List components, Map pinCoords) {
		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent comp = (AbstractComponent) it.next();

			try {
				Pin pin = (Pin) comp;
				pinCoords.put(pin, pin.getPosition());
			} catch(ClassCastException cex) {
			}

			if(comp.isGroup()) {
				collectPins(comp.getComponents(), pinCoords);
			}
		}
	}

	public BufferedImage getImage() {
		double zoom = 0.1;
		int w = (int) ((double) size.width * zoom);
		int h = (int) ((double) size.height * zoom);

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = img.getGraphics();

		paint(g, zoom);

		g.dispose();
		return img;
	}

	public void autoAssignIds(int startFrom) {
		Map idMap = new HashMap();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent ac = (AbstractComponent) it.next();
			if(ac instanceof Component) {
				Component c = (Component) ac;
				if(!c.supportsAutoIdAssignment()) {
					continue;
				}

				Text id = c.getAttributeText(Component.KEY_PART_ID);
				String idText = id.getText();

				while(true) {
					int l = idText.length();
					if(l == 0) {
						break;
					}

					char ch = idText.charAt(l - 1);
					if(Character.isDigit(ch)) {
						idText = idText.substring(0, l - 1);
					} else {
						break;
					}
				}

				if((idText.length() == 0) || idText.equals(SHEET_INFO_ID)) {
					continue;
				}

				Integer count = (Integer) idMap.get(idText);
				if(count == null) {
					count = startFrom;
				} else {
					count = count + 1;
				}
				idMap.put(idText, count);

				id.setText(idText + count);
			}
		}
	}

	public void paint(Graphics g, double zoom) {
		int w = (int) ((double) size.width * zoom);
		int h = (int) ((double) size.height * zoom);

		g.setColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_SHEET_COLOR));
		g.fillRect(0, 0, w, h);

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c.drawAll(g, null, zoom, false);
		}
	}

	public void release() {
		// Already released
		if(components == null) {
			return;
		}

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c.release();
		}
		components = null;
	}

	public void setSize(SheetSize size) {
		SheetSize oldSize = this.size;
		this.size = size;
		pcs.firePropertyChange(PROPERTY_SIZE, oldSize, size);
		setDirty(true);
	}

	public boolean hasComponent(AbstractComponent c) {
		return components.contains(c);
	}

	public static Component group(List components, Component newComp, Sheet sheet) {
		Point base = getBasePoint(components);
		newComp.setPosition(base.x, base.y);

		for(Iterator it = new ArrayList(components).iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Point p = c.getPosition();
			if(sheet != null) {
				sheet.removeComponent(c, false);
			}
			c.setPosition(p.x - base.x, p.y - base.y);
			newComp.addComponent(c);
		}

		if(sheet != null) {
			sheet.addComponent(newComp);
		}
		return newComp;
	}

	public Component groupComponent(List components) {
		Component newComp = new Component(0, 0);
		group(components, newComp, this);
		return newComp;
	}

	public Symbol groupSymbol(List components) {
		Symbol newComp = new Symbol(0, 0);
		group(components, newComp, this);
		return newComp;
	}

	public List ungroup(Component group) {
		if(!group.isGroup()) {
			return null;
		}

		Point base = group.getPosition();
		Orientation o = group.getOrientation();

		List groupComponents = new ArrayList(group.getComponents());
		List attributes = new ArrayList();

		for(Iterator it = groupComponents.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if((c instanceof Text) && (c.isMobile())) {
				attributes.add(c);
			}
		}

		for(Iterator it = groupComponents.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			group.removeComponent(c);
			if(attributes.contains(c)) {
				it.remove();
				c.release();
				continue;
			}

			Point p = c.getPosition();
			p = o.transform(p);
			Orientation co = c.getOrientation();
			c.setOrientation(co.transform(o));
			c.setPosition(p.x + base.x, p.y + base.y);
			addComponent(c);
		}

		removeComponent(group, true);
		return groupComponents;
	}

	public final void setTitle(String title) {
		String oldTitle = this.title;
		this.title = title;
		pcs.firePropertyChange(PROPERTY_TITLE, oldTitle, title);
		setDirty(true);
	}

	public String getTitle() {
		return title;
	}

	public SheetSize getSize() {
		return size;
	}

	private boolean needsHandleCheck(AbstractComponent c) {
		return (c instanceof Wire) || (c instanceof Pin) || c.isGroup();
	}

	public void checkHandleConnections(List handles) {
		for(Iterator hit = handles.iterator(); hit.hasNext();) {
			Handle h = (Handle) hit.next();
			checkHandleConnections(h, true);
		}
	}

	public void checkHandleConnections(Handle handle, boolean cascade) {
		List handles = (List) connectedObjectsByHandle.get(handle);
		if(handles == null) {
			handles = new ArrayList();
			connectedObjectsByHandle.put(handle, handles);
		}

		int numHandles = handles.size();
		handles.clear();
		handles.addAll(getHandlesAt(handle.getHandlePosition().absPos));

		if(handles.size() != numHandles) {
			handle.fireChanged();
		}

		if(handles.isEmpty()) {
			connectedObjectsByHandle.remove(handle);
		} else if(cascade) {
			for(Iterator hit = handles.iterator(); hit.hasNext();) {
				Handle h = (Handle) hit.next();
				if(h != handle) {
					checkHandleConnections(h, false);
				}
			}
		}
	}

	public List getHandlesAt(Point handlePos) {
		List handles = new ArrayList();

		for(Iterator it = components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();

			if(needsHandleCheck(c)) {
				Map compHandles = c.getHandlePositions();

				for(Iterator hit = compHandles.keySet().iterator(); hit.hasNext();) {
					Handle h = (Handle) hit.next();
					Point hp = (Point) compHandles.get(h);

					if(handlePos.equals(hp)) {
						if(!handles.contains(h)) {
							handles.add(h);
						}
						if(c instanceof Wire) {
							break;
						}
					}
				}
			}
		}

		return handles;
	}

	public int getConnectedObjectCountFor(Handle handle) {
		List wires = (List) connectedObjectsByHandle.get(handle);
		if(wires == null) {
			return 0;
		}
		return wires.size();
	}

	public void addComponent(AbstractComponent component) {
		addComponent(component, false);
	}

	public void addComponent(AbstractComponent component, boolean temporary) {
		if(component.isReleased()) {
			throw new IllegalStateException("Component is released " + component.getClass());
		}

		components.add(component);
		component.addComponentListener(this);
		if(needsHandleCheck(component)) {
			List handles = component.getStickyHandles();
			addHandlesToCache(handles);
		}
		if(!temporary) {
			checkAttach(component);
		}
		fireComponentAdded(component);
	}

	public void addComponents(List components) {
		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			addComponent(c);
		}
	}

	public void removeComponent(AbstractComponent component, boolean release) {
		if(component.isReleased()) {
			throw new IllegalStateException("Component is released " + component.getClass());
		}

		components.remove(component);
		removeHandlesFromCache(component.getStickyHandles());
		component.removeComponentListener(this);
		fireComponentRemoved(component);
		if(release) {
			component.release();
		}
		setDirty(true);
	}

	private void addHandleToCache(Handle handle) {
		Point p = handle.getHandlePosition().absPos;
		List handlesAtPoint = (List) handlesByPosition.get(p);
		if(handlesAtPoint == null) {
			handlesAtPoint = new ArrayList();
			handlesByPosition.put(p, handlesAtPoint);
		}
		if(!handlesAtPoint.contains(handle)) {
			handlesAtPoint.add(handle);

			int l = handlesAtPoint.size();
			for(Iterator it = handlesAtPoint.iterator(); it.hasNext();) {
				Handle h = (Handle) it.next();
				h.setConnectedWires(l);
			}
		}
	}

	private void removeHandleFromCache(Handle handle) {
		//System.out.println("remove " + handle);
		List handlesAtPoint = null;
		Point absPos = null;

		for(Iterator it = handlesByPosition.keySet().iterator(); it.hasNext();) {
			Point p = (Point) it.next();
			List l = (List) handlesByPosition.get(p);
			if(l.contains(handle)) {
				handlesAtPoint = l;
				absPos = p;
				break;
			}
		}

		if(handlesAtPoint == null) {
			return;
		}

		if(handlesAtPoint.remove(handle)) {
			int l = handlesAtPoint.size();
			if(l == 0) {
				handlesByPosition.remove(absPos);
			} else {
				for(Iterator hit = handlesAtPoint.iterator(); hit.hasNext();) {
					Handle h = (Handle) hit.next();
					h.setConnectedWires(l);
				}
			}
		}
	}

	private void addHandlesToCache(Collection stickyHandles) {
		for(Iterator it = stickyHandles.iterator(); it.hasNext();) {
			Handle handle = (Handle) it.next();
			addHandleToCache(handle);
		}
	}

	private void removeHandlesFromCache(Collection stickyHandles) {
		for(Iterator it = stickyHandles.iterator(); it.hasNext();) {
			Handle handle = (Handle) it.next();
			removeHandleFromCache(handle);
		}
	}

	public List getComponents() {
		return Collections.unmodifiableList(components);
	}

	public Iterator components() {
		return components.iterator();
	}

	public void addComponentListener(ComponentListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removeComponentListener(ComponentListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireComponentAdded(AbstractComponent c) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			try {
				l.componentAdded(c);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireComponentRemoved(AbstractComponent c) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			try {
				l.componentRemoved(c);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Point getBasePoint(List components) {
		Point base = null;

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Point p = c.getPosition();
			if(base == null) {
				base = p;
			}
			if(p.x < base.x) {
				base.x = p.x;
			}
			if(p.y < base.y) {
				base.y = p.y;
			}
		}

		return base;
	}

	public Component findSheetInfo() {
		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent ac = (AbstractComponent) it.next();
			if(ac instanceof Component) {
				Component c = (Component) ac;
				Text sheetInfo = c.getAttributeText(Component.KEY_PART_ID);
				if(SHEET_INFO_ID.equals(sheetInfo.getText())) {
					return c;
				}
			}
		}

		return null;
	}

	public void setIndexInfo(int index) {
		Component c = findSheetInfo();
		if(c != null) {
			setPageNr(c, index);
		}
	}

	private void setPageNr(Component sheetInfo, int index) {
		Text pageText = sheetInfo.getAttributeText(KEY_SHEETINFO_PAGE);
		String pageNr = Translator.translate("PAGE_NR");
		pageText.setText(pageNr + (index + 1));
	}

	public void setAuthorInfo() {
		Component c = findSheetInfo();
		if(c != null) {
			String author = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_AUTHOR);
			String company = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_COMPANY);

			c.getAttributeText(KEY_SHEETINFO_AUTHOR).setText(author);
			c.getAttributeText(KEY_SHEETINFO_COMPANY).setText(company);
		}
	}

	private void init() {
		int c = 3000;
		int r = 600;
		int w = 3 * c;
		int h = 3 * r;
		int border = 100;
		int tx = 500;
		int f = 500;
		int f2 = 350;
		int f3 = 200;
		int px = size.width - border - w;
		int py = size.height - border - h;

		String fontName = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME);
		String author = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_AUTHOR);
		String company = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_COMPANY);
		if((author == null) || (author.length() == 0)) {
			author = Translator.translate("DESIGNER");
		}
		if((company == null) || (company.length() == 0)) {
			company = Translator.translate("COMPANY_NAME");
		}

		Component info = new Component(px, py);
		info.getAttributeText(Component.KEY_PART_ID).setText(SHEET_INFO_ID);

		info.addComponent(new Square(0, 0, w, h));
		info.addComponent(new Line(0, r, w, 0));
		info.addComponent(new Line(0, 2 * r, w, 0));
		info.addComponent(new Line(c, 2 * r, 0, r));
		info.addComponent(new Line(2 * c, 2 * r, 0, r));
		info.addComponent(new Line(c, 5 * r / 2, c, 0));

		addComponent(info);
		px = 0;
		py = 0;
		Text companyText = new Text(px + tx, py + 100, company, fontName, f, Font.BOLD);
		companyText.setKey(KEY_SHEETINFO_COMPANY);
		info.addComponent(companyText, true);
		info.addComponent(new Text(px + tx, py + r + 100, Translator.translate("SCHEMATIC_NAME"), fontName, f, Font.BOLD), true);
		Text authorText = new Text(px + 100, py + 2 * r + 100, author, fontName, f3, Font.PLAIN);
		authorText.setKey(KEY_SHEETINFO_AUTHOR);
		info.addComponent(authorText, true);
		info.addComponent(new Text(px + 2 * c + 100, py + 2 * r + 100, Translator.translate("PAGE_NR"), fontName, f2, Font.PLAIN), true);
		info.addComponent(new Text(px + c + 800, py + 4 * r / 2 + 60, Translator.translate("REV_10"), fontName, f3, Font.PLAIN), true);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(new Date());
		info.addComponent(new Text(px + c + 800, py + 5 * r / 2 + 60, date, fontName, f3, Font.PLAIN), true);

		dirty = false;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		boolean oldVal = this.dirty;
		this.dirty = dirty;
		pcs.firePropertyChange(PROPERTY_DIRTY, oldVal, dirty);
	}

	public Handle findHandle(AbstractComponent c, Point p, int handleSize, boolean onlySticky) {
		return c.handleContains(p.x, p.y, handleSize, onlySticky);
	}

	public static AbstractComponent findComponent(List components, int px, int py, int clickTolerance) {
		return findComponent(components, null, px, py, clickTolerance);
	}

	public static AbstractComponent findComponent(List components, AbstractComponent refComp, int px, int py, int clickTolerance) {
		for(int i = components.size() - 1; i >= 0; i--) {
			AbstractComponent c = (AbstractComponent) components.get(i);
			if((refComp != null) && (c == refComp)) {
				continue;
			}

			Point p = c.normalize(new Point(px, py));
			if(c.contains(p.x, p.y, clickTolerance)) {
				return c;
			}
		}
		return null;
	}

	public AbstractComponent findComponent(int px, int py, int clickTolerance) {
		return findComponent(components, px, py, clickTolerance);
	}

	public boolean checkAttach(AbstractComponent newComp) {
		Map pos = newComp.getHandlePositions();
		boolean attached = false;

		for(Iterator it = pos.keySet().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			Point p = (Point) pos.get(h);
			attached = attached | checkAttach(h, p, p, newComp);
		}

		return attached;
	}

	public boolean checkAttach(Handle h, Point handleAbsPos, Point constrainedPos, AbstractComponent newComp) {
		int dist = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_PIN_RADIUS);

		AbstractComponent closeComp = findComponent(components, newComp, handleAbsPos.x, handleAbsPos.y, dist);
		if(closeComp == null) {
			return false;
		}

		Handle hd = findHandle(closeComp, handleAbsPos, dist, true);
		if((hd == null) || (hd.getHandlePosition().absPos.equals(handleAbsPos))) {
			return false;
		}

		synchronized(checkingAttachHandles) {
			if(checkingAttachHandles.contains(h)) {
				return false;
			}

			checkingAttachHandles.add(h);
			if(newComp instanceof Wire) {
				Point otherHandlePos = hd.getHandlePosition().absPos;
				Point offset = new Point(otherHandlePos.x - handleAbsPos.x, otherHandlePos.y - handleAbsPos.y);
				h.setHandlePosition(offset, true, false);
				return true;
			} else if(newComp.isGroup() && (closeComp instanceof Wire)) {
				Point otherHandlePos = hd.getHandlePosition().absPos;
				Point offset = new Point(handleAbsPos.x - otherHandlePos.x, handleAbsPos.y - otherHandlePos.y);
				hd.setHandlePosition(offset, true, false);
				return true;
			}

			checkingAttachHandles.remove(h);
		}
		return false;
	}

	public boolean checkSplit(AbstractComponent newComp) {
		if(!(newComp instanceof Wire)) {
			return false;
		}

		Wire newWire = (Wire) newComp;
		Map pos = newWire.getHandlePositions();
		boolean split = false;

		for(Iterator it = pos.keySet().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			Point p = (Point) pos.get(h);
			split |= checkSplit(p, p, newWire);
		}

		return split;
	}

	public boolean checkSplit(Point handleAbsPos, Point constrainedPos, Wire newWire) {
		int lineWidth = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);

		AbstractComponent c = findComponent(components, newWire, handleAbsPos.x, handleAbsPos.y, lineWidth);
		if(c == null) {
			return false;
		}

		try {
			Wire w = (Wire) c;
			Handle hd = findHandle(w, handleAbsPos, 0, false);
			if(hd != null) {
				return false;
			}

			splitWire(w, constrainedPos);
			return true;
		} catch(ClassCastException cex) {
		}

		return false;
	}

	private void splitWire(Wire wire, Point p) {
		Map pos = wire.getHandlePositions();
		for(Iterator it = pos.keySet().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			Point hp = (Point) pos.get(h);

			int dx = p.x - hp.x;
			int dy = p.y - hp.y;

			Wire w = (Wire) wire.duplicate();
			w.setPosition(hp.x, hp.y);
			w.setOffset(dx, dy, false);
			addComponent(w);
		}

		removeComponent(wire, true);
	}

	public List getNeighbourHandles(Map handles) {
		Set neighbourHandles = new HashSet();

		// Find all handles that lie on the same coordinates as the ones being dragged
		for(Iterator it = handles.values().iterator(); it.hasNext();) {
			Point p = (Point) it.next();
			List attachedHandles = (List) handlesByPosition.get(p);
			if(attachedHandles != null) {
				neighbourHandles.addAll(attachedHandles);
			}
		}

		// Remove the handles that are being dragged 
		neighbourHandles.removeAll(handles.keySet());

		return new ArrayList(neighbourHandles);
	}

	private List getComponents(Collection handles) {
		Set handleComponents = new HashSet();

		for(Iterator it = handles.iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			AbstractComponent c = (AbstractComponent) h.getOwner();
			handleComponents.add(c);
		}

		return new ArrayList(handleComponents);
	}

	@Override
	public void componentWillChange(AbstractComponent c) {
	}

	@Override
	public void componentChanged(AbstractComponent c) {
		setDirty(true);
	}

	@Override
	public void componentAdded(AbstractComponent c) {
		setDirty(true);
	}

	@Override
	public void componentRemoved(AbstractComponent c) {
		setDirty(true);
	}

	@Override
	public void handlesMoved(Map handles, Point offset, boolean dragging) {
		if(dragNeighbourHandles == null) {
			dragNeighbourHandles = getNeighbourHandles(handles);
		}

		for(Iterator it = dragNeighbourHandles.iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			h.setHandlePosition(offset, false, true);
		}

		if(!dragging) {
			List allHandles = new ArrayList(handles.keySet());
			allHandles.addAll(dragNeighbourHandles);

			List comps = getComponents(allHandles);

			removeHandlesFromCache(handles.keySet());
			removeHandlesFromCache(dragNeighbourHandles);

			addHandlesToCache(handles.keySet());
			addHandlesToCache(dragNeighbourHandles);

			dragNeighbourHandles = null;

			for(Iterator it2 = comps.iterator(); it2.hasNext();) {
				AbstractComponent c = (AbstractComponent) it2.next();
				if(c.isReleased()) {
					continue;
				}

				if(c.hasBecomeInvalid()) {
					removeComponent(c, true);
				} else {
					if(!checkAttach(c)) {
						checkSplit(c);
					}
				}
			}
		}
	}

	public Sheet duplicate() {
		return (Sheet) clone();
	}

	@Override
	public Object clone() {
		Sheet sheet = new Sheet(getTitle(), getSize(), false);
		for(Iterator it = components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.addComponent(c.duplicate());
		}
		return sheet;
	}

}
