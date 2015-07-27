package org.heinz.eda.schem.model.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.heinz.eda.schem.model.ComponentListener;
import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.xml.XmlPropertyConverterOrientation;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterBoolean;
import org.heinz.framework.utils.xml.XmlPropertyConverterColor;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;
import org.heinz.framework.utils.xml.XmlPropertyConverterString;

public abstract class AbstractComponent implements ComponentListener, PropertyChangeListener, Comparable, Serializable {
	public static final Map PROPERTIES = new HashMap();
	
	static {
		PROPERTIES.put(new XmlProperty("key", XmlPropertyConverterString.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("x", XmlPropertyConverterInteger.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("y", XmlPropertyConverterInteger.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("mobile", XmlPropertyConverterBoolean.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("visible", XmlPropertyConverterBoolean.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("orientation", XmlPropertyConverterOrientation.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("color", XmlPropertyConverterColor.instance()), AbstractComponent.class);
		PROPERTIES.put(new XmlProperty("fillColor", XmlPropertyConverterColor.instance()), AbstractComponent.class);
	}
	
	private String key;
	private AbstractComponent parent;
	private List elements = new ArrayList();
	private List handles = new ArrayList();
	private List stickyHandles = new ArrayList();
	private int x;
	private int y;
	private Color color;
	private Color fillColor;
	private Color selectedColor;
	private List listeners = new ArrayList();
	private Orientation orientation = Orientation.RIGHT;
	private boolean mobile;
	protected boolean group;
	private List graphicsStates = new ArrayList();
	private boolean visible = true;
	private boolean smartJunctions;
	private BufferedImage bufferImage;
	private BufferImageParams bufferImageParams;
	private boolean useImageCache = false;
	private Point bufferDrawOffset = null;
	
	private static final boolean SHOW_BOUNDS = false;
	
	public AbstractComponent() {
		this(0, 0);
	}
	
	public AbstractComponent(int x, int y) {
		this.x = x;
		this.y = y;
		
		init();
	}
	
	public AbstractComponent(AbstractComponent c) {
		x = c.x;
		y = c.y;
		color = c.color;
		fillColor = c.fillColor;
		selectedColor = c.selectedColor;
		orientation = c.orientation;
		mobile = c.mobile;
		group = c.group;
		visible = c.visible;
		key = c.key;
		
		duplicateChildren(c);
		init();
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	protected boolean hasSmartJunctions() {
		return smartJunctions;
	}
	
	public boolean intersects(Rectangle clip, double zoom) {
		if(clip == null)
			return true;
		
		ExtRect bbox = getScreenBounds(zoom).normalize();
		int tolerance = (int) (((double) SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH)) * zoom);
		int tol2 = 2 * tolerance;
		Rectangle r = new Rectangle(bbox.x - tolerance, bbox.y - tolerance, bbox.width + tol2, bbox.height + tol2);
		return clip.intersects(r);
	}
	
	protected void removeHandle(Handle h) {
		handles.remove(h);
	}
	
	public void snapToGrid(int snapGrid) {
		fireWillChange();
		
		int px = GridHelper.snapToGrid(x, snapGrid);
		int py = GridHelper.snapToGrid(y, snapGrid);
		setPosition(px, py);
		snapChildrenToGrid(snapGrid);
		
		fireChanged();
	}
	
	private void snapChildrenToGrid(int snapGrid) {
		for(Iterator it=elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			child.snapToGrid(snapGrid);
		}
	}
	
	protected void init() {
		selectedColor = SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_SELECTED_COLOR);
		smartJunctions = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SMART_JUNCTIONS);
		SchemOptions.instance().addPropertyChangeListener(this);
		addHandles();
	}
	
	public boolean hasBecomeInvalid() {
		return false;
	}
	
	protected void addHandles() {
	}
	
	public Iterator elements() {
		return elements.iterator();
	}
	
	public AbstractComponent elementAt(int idx) {
		return (AbstractComponent) elements.get(idx);
	}
	
	public boolean hasElements() {
		return elements.size() > 0;
	}
	
	public abstract AbstractComponent duplicate();
	
	private void duplicateChildren(AbstractComponent comp) {
		for(Iterator it=comp.elements.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c = c.duplicate();
			addComponent(c);
		}
	}

	public void release() {
		SchemOptions.instance().removePropertyChangeListener(this);
		listeners = null;
		handles = null;
		stickyHandles = null;
	}
	
	public AbstractComponent getParent() {
		return parent;
	}
	
	public AbstractComponent getToplevel() {
		if(parent == null)
			return this;
		return parent.getToplevel();
	}
	
	public List getHandles() {
		return Collections.unmodifiableList(handles); 
	}
	
	public List getStickyHandles() {
		List handles = new ArrayList(stickyHandles);
		for(Iterator it=elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			handles.addAll(child.getStickyHandles());
		}
		return handles;
	}
	
	public Map getHandlePositions() {
		Map hpos = new HashMap();
		for(Iterator it=getHandles().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			if(h.isSticky())
				hpos.put(h, h.getHandlePosition().absPos);
		}
		
		for(Iterator it=elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			hpos.putAll(child.getHandlePositions());
		}
		return hpos;
	}
	
	public void setPosition(int x, int y) {
		setPosition(x, y, true, false);
	}
	
	public void setPosition(int x, int y, boolean withHandles, boolean dragging) {
		if((getX() == x) && (getY() == y))
			return;
				
		Map hpos = new HashMap();
		if(withHandles)
			hpos = getHandlePositions();
		
		Point offset = new Point(x - getX(), y - getY());
		fireWillChange();
		this.x = x;
		this.y = y;
		fireChanged();

		if(withHandles) {
			Map handles = new HashMap();
			for(Iterator it=hpos.keySet().iterator(); it.hasNext();) {
				Handle h = (Handle) it.next();
				Point hp = (Point) hpos.get(h);
				handles.put(h, hp);
			}
			fireHandlesMoved(handles, offset, false);
		}
	}
	
	protected Point getCenterOffset() {
		ExtRect b = getBounds();
		return new Point(b.width/2, b.height/2);
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Orientation orientation) {
		if(this.orientation == orientation)
			return;
		
		Map oldPos = getHandlePositions();
		
		fireWillChange();
		this.orientation = orientation;
		fireChanged();
		
		Map newPos = getHandlePositions();
		for(Iterator it=oldPos.keySet().iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			Point op = (Point) oldPos.get(h);
			Point np = (Point) newPos.get(h);
			Point offset = new Point(np.x - op.x, np.y - op.y);
			Map m = new HashMap();
			m.put(h, op);
			fireHandlesMoved(m, offset, false);
		}
	}
	
	public Point getPosition() {
		return new Point(x, y);
	}
	
	public boolean isMobile() {
		return mobile;
	}
	
	public boolean isGroup() {
		return group;
	}
	
	public boolean isReleased() {
		return (listeners == null) || (handles == null) || (stickyHandles == null);
	}
	
	public List getComponents() {
		return elements;
	}
	
	protected void pushState(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		graphicsStates.add(new GraphicsState(g2d.getTransform(), g2d.getStroke()));
	}
	
	protected void popState(Graphics g) {
		GraphicsState st = (GraphicsState) graphicsStates.remove(graphicsStates.size()-1);
		((Graphics2D) g).setTransform(st.transform);
		((Graphics2D) g).setStroke(st.stroke);
	}
	
	protected void setStroke(Graphics g, double zoom) {
		setStroke(g, zoom, false);
	}
	
	protected void setStroke(Graphics g, double zoom, boolean round) {
		int w = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);
		setStroke(g, w, zoom, round);
	}
	
	protected void setStroke(Graphics g, int lineWidth, double zoom, boolean round) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke((float) (lineWidth * zoom), BasicStroke.CAP_ROUND, round ? BasicStroke.JOIN_ROUND : BasicStroke.JOIN_MITER));
	}
	
	protected void transform(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.transform(getOrientation().getAffineTransform());
	}
	
	protected void addHandle(Handle handle) {
		handles.add(handle);
		if(handle.isSticky())
			stickyHandles.add(handle);
	}
	
	protected void translate(Graphics g, int x, int y, double zoom) {
		((Graphics2D) g).translate(x * zoom, y * zoom);
	}
	
	/**
	 * Draws the component without any rotations/translations/handles.
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	protected abstract void draw(Graphics g, double zoom, boolean selected);
	
	/**
	 * Draws the component without any rotations/translations/handles.
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	protected void drawCached(Graphics g, double zoom, boolean selected) {
		if(!useImageCache) {
			draw(g, zoom, selected);
			return;
		}
		
		BufferImageParams bimp = new BufferImageParams(selected, zoom);
		
		if((bufferImage == null) || !bimp.equals(bufferImageParams)) {
			ExtRect d = getScreenBounds(zoom).normalize();
			if(d.width <= 0)
				d.width = 1;
			if(d.height <= 0)
				d.height = 1;
			bufferImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			
			Point loc = getScreenLocation(zoom);
			bufferDrawOffset = new Point(d.x - loc.x, d.y - loc.y);
			
			Graphics g2 = bufferImage.getGraphics();
			g2.setColor(Color.red);
			g2.fillRect(bufferDrawOffset.x, bufferDrawOffset.y, bufferImage.getWidth(), bufferImage.getHeight());
			draw(g2, zoom, selected);
			g2.dispose();
			bufferImageParams = bimp;
		}
		
		g.drawImage(bufferImage, 0, 0, null);
	}
	
	/**
	 * Draws the handles of this component.
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	protected void drawHandles(Graphics g, double zoom, boolean selected) {
		if(selected && (parent == null)) {
			setStroke(g, zoom);
			
			for(Iterator it=getHandles().iterator(); it.hasNext();) {
				Handle handle = (Handle) it.next();
				handle.draw(g, zoom);
			}
		}
	}
	
	/**
	 * Draws the bounding box of the component (for debugging purposes).
	 * @param g
	 * @param zoom
	 */
	protected void drawBounds(Graphics g, double zoom) {
		g.setColor(Color.lightGray);
		ExtRect b = getBounds().normalize();
		g.drawRect((int) (zoom*b.x), (int) (zoom*b.y), (int) (zoom*b.width), (int) (zoom*b.height));
	}

	/**
	 * Draws only the component (but not its subcomponents).
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	protected void drawComponent(Graphics g, Rectangle clip, double zoom, boolean selected) {
		boolean needsDraw = intersects(clip, zoom);
		if(!needsDraw)
			return;
		
		pushState(g);
		
		translate(g, x, y, zoom);
		
		if(SHOW_BOUNDS) {
			g.setColor(Color.red);
			g.fillRect(0, 0, 5, 5);
		}
		
		transform(g);
		drawCached(g, zoom, selected);
		drawHandles(g, zoom, selected);
		
		if(SHOW_BOUNDS)
			drawBounds(g, zoom);
		
		popState(g);
	}

	/**
	 * Draws the component and all its subcomponents if this is a composite component.
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	public void drawAll(Graphics g, Rectangle clip, double zoom, boolean selected) {
		if(!visible)
			return;
		
		drawComponent(g, clip, zoom, selected);
		drawChildren(g, clip, zoom, selected);
	}
	
	/**
	 * Draws the subcomponents of this component.
	 * @param g
	 * @param zoom
	 * @param selected
	 */
	protected void drawChildren(Graphics g, Rectangle clip, double zoom, boolean selected) {
		if(elements.size() == 0)
			return;
		
		pushState(g);
		
		translate(g, x, y, zoom);
		transform(g);
		
		for(Iterator it=elements.iterator(); it.hasNext();) {
			AbstractComponent d = (AbstractComponent) it.next();
			d.drawAll(g, clip, zoom, selected);
		}
		
		popState(g);
	}

	public boolean contains(int x, int y, int clickTolerance) {
		return childContains(x, y, clickTolerance) != null;
	}
	
	public AbstractComponent childContains(int x, int y, int clickTolerance) {
		if(!visible)
			return null;
		
		Point op = new Point(x, y);
		
		for(Iterator it=elements.iterator(); it.hasNext();) {
			AbstractComponent d = (AbstractComponent) it.next();
			Point p = d.normalize(op);
			if(d.contains(p.x, p.y, clickTolerance))
				return d;
		}
		
		return null;
	}
	
	public Handle handleContains(int absX, int absY, int handleSize, boolean onlySticky) {
		if(!visible)
			return null;
		
		List hd = onlySticky ? getStickyHandles() : getHandles();
		for(Iterator it=hd.iterator(); it.hasNext();) {
			Handle h = (Handle) it.next();
			Point hp = h.getHandlePosition().absPos;
			ExtRect r = new ExtRect(hp.x - handleSize, hp.y - handleSize, 2 * handleSize, 2 * handleSize);
			if(r.contains(absX, absY))
				return h;
		}
		
		return null;
	}
	
	public Point getScreenLocation(double zoom) {
		Point p = getLocation();
		p = new Point((int) (zoom * p.x), (int) (zoom * p.y));
		return p;
	}
	
	public Point getLocation() {
		Point p = new Point(x, y);
		
		if(parent != null) {
			Point pp = parent.getLocation();
			p = parent.getOrientation().transform(p);
			p.translate(pp.x, pp.y);
		}
		
		return p;
	}
	
	public Point getScreenPosition(double zoom) {
		Point p = new Point((int) (zoom * x), (int) (zoom * y));
		return new Point(p.x, p.y);
	}
	
	protected Color getColor(boolean selected) {
		if(selected)
			return selectedColor;
		return (color == null) ? SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR) : color;
	}
	
	public Iterator attributeTexts() {
		List attributes = new ArrayList();
		
		for(Iterator it=elements(); it.hasNext();) {
			AbstractComponent sc = (AbstractComponent) it.next();
			if((sc instanceof Text) && (sc.isMobile()))
				attributes.add(sc);
		}
		
		return attributes.iterator();
	}
	
	public Text getAttributeText(String key) {
		for(Iterator it=attributeTexts(); it.hasNext();) {
			Text txt = (Text) it.next();
			if(key.equals(txt.getKey()))
				return txt;
		}

		return null;
	}
	
	public int addAttributeText(String key, String s, int y, boolean visible) {
		Text t = createAttributeText(s);
		t.setKey(key);
		t.setVisible(visible);
		y = placeNextText(t, y);
		addComponent(t, true);
		return y;
	}
	
	public static Text createAttributeText(String text) {
		int fontSize = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE);
		int fontStyle = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_STYLE);
		String fontName = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME);
		Text t = new Text(0, 0, text, fontName, fontSize, fontStyle);
		return t;
	}
	
	public static int placeNextText(AbstractComponent c, int y) {
		int fontSize = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE);
		c.setPosition(100, y);
		return y + fontSize * 12/10;
	}
	
	public void addComponent(AbstractComponent c) {
		addComponent(c, false);
	}
	
	public void addComponent(AbstractComponent c, boolean mobile) {
		addComponent(c, mobile, true);
	}
	
	public void addComponent(AbstractComponent c, boolean mobile, boolean fire) {
		if(fire)
			fireWillChange();
		c.parent = this;
		if(mobile)
			c.mobile = mobile;
		elements.add(c);
		c.addComponentListener(this);
		if(fire)
			fireAdded(c);
	}
	
	public void removeComponent(AbstractComponent c) {
		c.parent = null;
		elements.remove(c);
		c.removeComponentListener(this);
		fireRemoved(c);
	}
	
	public ExtRect getScreenBounds(double zoom) {
		if(!isVisible())
			return null;
		
		ExtRect b = getSheetBoundingBox();
		int dx = (int) (b.width * zoom);
		int dy = (int) (b.height * zoom);
		return new ExtRect((int) (b.x * zoom), (int) (b.y * zoom), dx, dy);
	}
	
	public ExtRect getSheetBoundingBox() {
		if(!isVisible())
			return null;
		
		ExtRect r = getBounds();
		AbstractComponent c = this;
		while(c != null) {
			r = c.getOrientation().transform(r);
			r.translate(c.x, c.y);
			c = c.parent;
		}
		
		return r;
	}
	
	public ExtRect getBoundingBox() {
		if(!isVisible())
			return null;
		
		ExtRect r = getBounds();
		r = getOrientation().transform(r);
		r.translate(x, y);
		return r;
	}
	
	protected ExtRect getBounds() {
		ExtRect b = getChildBounds();
		return b;
	}
	
	protected ExtRect getChildBounds() {
		ExtRect r = null;
		
		for(Iterator it=elements.iterator(); it.hasNext();) {
			AbstractComponent d = (AbstractComponent) it.next();
			if(!d.isVisible())
				continue;
			
			ExtRect bb = d.getBounds();
			bb = d.getOrientation().transform(bb);
			bb.translate(d.getX(), d.getY());
			if(r == null)
				r = bb;
			else
				r.add(bb);
		}
		
		return r;
	}
	
	public void addComponentListener(ComponentListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeComponentListener(ComponentListener listener) {
		listeners.remove(listener);
	}
	
	void fireChanged() {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			l.componentChanged(this);
		}
	}
	
	void fireWillChange() {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			l.componentWillChange(this);
		}
	}
	
	public void fireHandlesMoved(Map handles, Point offset, boolean dragging) {
		for(Iterator it=new ArrayList(listeners).iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			l.handlesMoved(handles, offset, dragging);
		}
	}
	
	protected void fireRemoved(AbstractComponent c) {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			l.componentRemoved(c);
		}
	}
	
	protected void fireAdded(AbstractComponent c) {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			ComponentListener l = (ComponentListener) it.next();
			l.componentAdded(c);
		}
	}
	
	public Point normalize(Point p) {
		Point np = new Point(p);
		np.translate(-x, -y);
		np = getOrientation().unTransform(np);
		return np;
	}
	
	public void handlesMoved(Map handles, Point offset, boolean dragging) {
	}
	
	public void componentChanged(AbstractComponent c) {
		fireChanged();
	}

	public void componentWillChange(AbstractComponent c) {
		fireWillChange();
	}

	public void componentAdded(AbstractComponent c) {
	}

	public void componentRemoved(AbstractComponent c) {
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String pn = evt.getPropertyName();
		if(pn.equals(SchemOptions.PROPERTY_SELECTED_COLOR)) {
			selectedColor = (Color) evt.getNewValue();
			fireChanged();
		} else if(pn.equals(SchemOptions.PROPERTY_HANDLE_FILL_COLOR)) {
			fireChanged();
		} else if(pn.equals(SchemOptions.PROPERTY_SMART_JUNCTIONS)) {
			smartJunctions = ((Boolean) evt.getNewValue()).booleanValue();
			fireChanged();
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		fireWillChange();
		
		this.color = color;
		for(Iterator it=elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			child.setColor(color);
		}
		
		fireChanged();
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		fireWillChange();
		this.selectedColor = selectedColor;
		fireChanged();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		fireWillChange();
		this.x = x;
		fireChanged();
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		fireWillChange();
		this.y = y;
		fireChanged();
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}
	
	public int compareTo(Object o) {
		return o.hashCode() - hashCode();
	}
	

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		fireWillChange();
		this.visible = visible;
		fireChanged();
	}

	public Color getFillColor() {
		return fillColor;
	}

	public Color getFillColor(boolean selected) {
		if(selected && SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_FILL_WITH_SELECTION_COLOR))
			return selectedColor;
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		fireWillChange();
		this.fillColor = fillColor;
		fireChanged();
	}
	
	//--------------------------------------------
	
	class GraphicsState {
		final AffineTransform transform;
		final Stroke stroke;
		
		GraphicsState(AffineTransform transform, Stroke stroke) {
			this.transform = transform;
			this.stroke = stroke;
		}
	}
	
	class BufferImageParams {
		final boolean selected;
		final double zoom;
		
		public BufferImageParams(boolean selected, double zoom) {
			this.selected = selected;
			this.zoom = zoom;
		}
	}
}
