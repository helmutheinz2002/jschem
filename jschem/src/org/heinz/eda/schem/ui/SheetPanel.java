
package org.heinz.eda.schem.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.heinz.eda.schem.model.ComponentListener;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.ui.tools.SchemEditTool;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.eda.schem.util.ListListener;
import org.heinz.eda.schem.util.ListenableArrayList;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfoProvider;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfos;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.IconLoader;

public class SheetPanel extends JPanel implements ComponentListener, MouseListener, MouseMotionListener,
		PropertyChangeListener, ActionStateInfoProvider {

	public static final String PROPERTY_MOUSE_POSITION = "SheetPanel.mousePosition";

	public static final String PROPERTY_ZOOM = "SheetPanel.zoom";

	private static final int CLICK_TOLERANCE = 3;

	private static final int MIN_GRID_DIST = 10;

	private final Sheet sheet;

	private final ListenableArrayList selection = new ListenableArrayList();

	private SchemEditTool tool;

	private ExtRect frame;

	private double zoom;

	private int grid;

	private final Color panelColor;

	private Color sheetColor;

	private Color gridColor;

	private Color pageBorderColor;

	private Color selectionFrameColor;

	private boolean gridSnap;

	private boolean gridVisible;

	private boolean antiAliasing;

	private int gridSnapSpacing;

	private final SheetPopupMenu popupMenu;

	private boolean allowPopupMenu;

	private final Stroke selectionFrameStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{4, 4}, 0);

	private Image toolImage;

	private Point toolImagePos = new Point();

	private boolean mouseOutside = true;

	@SuppressWarnings("LeakingThisInConstructor")
	public SheetPanel(Sheet sheet) {
		super();
		this.sheet = sheet;
		panelColor = getBackground();

		setFocusable(true);

		pageBorderColor = SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_PAGE_BORDER_COLOR);
		sheetColor = SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_SHEET_COLOR);
		gridColor = SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_GRID_COLOR);
		selectionFrameColor = SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_SELECTION_BOX_COLOR);
		grid = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SPACING);
		zoom = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM_DEFAULT);
		gridSnapSpacing = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING);
		gridSnap = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_SNAP);
		gridVisible = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_VISIBLE);
		antiAliasing = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_ANTIALIASING);

		adjustSize();

		addMouseListener(this);
		addMouseMotionListener(this);
		SchemOptions.instance().addPropertyChangeListener(this);
		sheet.addComponentListener(this);
		sheet.addPropertyChangeListener(this);

		for(Iterator it = sheet.components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c.addComponentListener(this);
		}

		popupMenu = new SheetPopupMenu();

		selection.addListListener(new ListListener() {

			@Override
			public void listChanged() {
				ApplicationActions.instance().setActionStates();
			}

		});
		ApplicationActions.instance().setActionStates();
	}

	public void release() {
		SchemOptions.instance().removePropertyChangeListener(this);
		ApplicationActions.instance().removeStateInfoProvider(this);
	}

	public void selectionToFront() {
		sheet.toFront((AbstractComponent) selection.get(0));
	}

	public void selectionToBack() {
		sheet.toBack((AbstractComponent) selection.get(0));
	}

	public void setEditTool(SchemEditTool tool, boolean force) {
		requestFocusInWindow();

		if(!force && (this.tool == tool)) {
			return;
		}

		toolImage = null;
		if(this.tool != null) {
			this.tool.cancel();
			repaint();
		}
		this.tool = tool;
		if(tool != null) {
			tool.setSheetPanel(this);
			if(tool.getIcon() != null) {
				String icon = "tool/" + tool.getIcon();
				ImageIcon iic = (ImageIcon) IconLoader.instance().loadIcon(icon);
				toolImage = iic.getImage();
			}
		}
	}

	public Point getCenter() {
		Rectangle r = getVisibleRect();
		int cx = (int) ((double) (r.x + r.width / 2) / zoom);
		int cy = (int) ((double) (r.y + r.height / 2) / zoom);

		return new Point(cx, cy);
	}

	public void setCenter(Point center) {
		Point c = getCenter();

		Rectangle r = getVisibleRect();
		int dx = (int) ((double) (center.x - c.x) * zoom);
		int dy = (int) ((double) (center.y - c.y) * zoom);
		r.translate(dx, dy);
		scrollRectToVisible(r);
	}

	public void zoomInArea(ExtRect area) {
		double px = (double) area.x / zoom;
		double py = (double) area.y / zoom;
		double w = (double) area.width / zoom;
		double h = (double) area.height / zoom;

		zoomToFit(getVisibleArea(), new Dimension((int) w, (int) h));

		int sx = (int) (px * zoom);
		int sy = (int) (py * zoom);
		int sw = (int) (w * zoom);
		int sh = (int) (h * zoom);
		Rectangle newArea = new Rectangle(sx, sy, sw, sh);
		scrollRectToVisible(newArea);
	}

	public void zoomIn() {
		double zoomStep = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM_INCREMENT);
		setZoom(zoom * zoomStep);
	}

	public void zoomOut() {
		double zoomStep = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM_INCREMENT);
		setZoom(zoom / zoomStep);
	}

	public void zoomToFit() {
		final Dimension size = new Dimension(sheet.getSize().width, sheet.getSize().height);
		zoomToFit(getVisibleArea(), size);
	}

	private void zoomToFit(Dimension viewSize, Dimension sheetSize) {
		double zx = (double) (viewSize.width - 2) / (double) sheetSize.width;
		double zy = (double) (viewSize.height - 2) / (double) sheetSize.height;

		double newZoom = zx;
		if(zy < newZoom) {
			newZoom = zy;
		}

		setZoom(newZoom);
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private Dimension getVisibleArea() {
		Dimension d = getSize();
		try {
			JViewport p = (JViewport) getParent();
			JScrollPane sp = (JScrollPane) p.getParent();
			d = p.getSize();
			if(sp.getVerticalScrollBar() != null) {
				if(sp.getVerticalScrollBar().isVisible()) {
					d.width = d.width + sp.getVerticalScrollBar().getWidth();
				}
			}
			if(sp.getHorizontalScrollBar() != null) {
				if(sp.getHorizontalScrollBar().isVisible()) {
					d.height = d.height + sp.getHorizontalScrollBar().getHeight();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	private void adjustSize() {
		int dx = (int) (sheet.getSize().width * zoom);
		int dy = (int) (sheet.getSize().height * zoom);
		Dimension d = new Dimension(dx, dy);

		setMinimumSize(d);
		setPreferredSize(d);
		setSize(d);
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Map hints = new HashMap(g2d.getRenderingHints());

		Rectangle clip = g.getClipBounds();
		drawSheet(g, clip);

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if(gridVisible) {
			drawGrid(g, clip);
		}

		g.setColor(pageBorderColor);
		g.drawRect(0, 0, (int) (sheet.getSize().width * zoom), (int) (sheet.getSize().height * zoom));

		if(antiAliasing) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		for(Iterator it = sheet.components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(!isSelected(c)) {
				c.drawAll(g, clip, zoom, false);
			}
		}

		for(Iterator it = selection.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c.drawAll(g, clip, zoom, true);
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if(frame != null) {
			Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(selectionFrameStroke);
			g.setColor(selectionFrameColor);
			g.drawRect(frame.x, frame.y, frame.width, frame.height);
			g2d.setStroke(oldStroke);
		}

		if((toolImage != null) && !mouseOutside) {
			g.drawImage(toolImage, toolImagePos.x, toolImagePos.y, null);
		}

		g2d.setRenderingHints(hints);
	}

	public Sheet getSheet() {
		return sheet;
	}

	public Point constrainScreenPoint(int x, int y, boolean forceNoSnap) {
		return constrainPoint(x, y, zoom, forceNoSnap);
	}

	public Point constrainPoint(int x, int y, boolean forceNoSnap) {
		return constrainPoint(x, y, 1.0, forceNoSnap);
	}

	public Point constrainPoint(Point p, boolean forceNoSnap) {
		return constrainPoint(p.x, p.y, forceNoSnap);
	}

	public Point convertScreenToUnits(int x, int y) {
		int px = (int) ((double) x / zoom);
		int py = (int) ((double) y / zoom);
		return new Point(px, py);
	}

	public Point convertUnitsToScreen(int x, int y) {
		int px = (int) ((double) x * zoom);
		int py = (int) ((double) y * zoom);
		return new Point(px, py);
	}

	private Point constrainPoint(int x, int y, double zoom, boolean forceNoSnap) {
		int px = (int) ((double) x / zoom);
		int py = (int) ((double) y / zoom);

		if(gridSnap && !forceNoSnap) {
			px = constrain(x, zoom);
			py = constrain(y, zoom);
		}

		return new Point(px, py);
	}

	private int constrain(int v, double zoom) {
		double dv = (double) v / zoom;
		return GridHelper.snapToGrid((int) dv, gridSnapSpacing);
	}

	private void drawGrid(Graphics g, Rectangle clip) {
		int w = (int) ((double) sheet.getSize().width * zoom);
		int h = (int) ((double) sheet.getSize().height * zoom);

		double gridPixelDist = grid * zoom;
		if(gridPixelDist < MIN_GRID_DIST) {
			return;
		}

		int startx = 0;
		int starty = 0;
		int endx = w;
		int endy = h;

		if(clip != null) {
			startx = (int) (((int) (clip.x / gridPixelDist)) * gridPixelDist);
			starty = (int) (((int) (clip.y / gridPixelDist)) * gridPixelDist);
			endx = (int) (((int) ((clip.x + clip.width) / gridPixelDist + 1)) * gridPixelDist);
			if(endx > w) {
				endx = w;
			}
			endy = (int) (((int) ((clip.y + clip.height) / gridPixelDist + 1)) * gridPixelDist);
			if(endy > h) {
				endy = h;
			}
		}

		g.setColor(gridColor);

		int rad = (int) (6.0 * zoom);
		int dia = 2 * rad;

		for(double px = startx - rad; px < endx; px += gridPixelDist) {
			for(double py = starty - rad; py < endy; py += gridPixelDist) {
				if(dia <= 1) {
					g.fillRect((int) px, (int) py, 1, 1);
				} else {
					g.fillArc((int) px, (int) py, dia, dia, 0, 360);
				}
			}
		}
	}

	@SuppressWarnings("null")
	private void drawSheet(Graphics g, Rectangle clip) {
		int w = (int) ((double) sheet.getSize().width * zoom);
		int h = (int) ((double) sheet.getSize().height * zoom);

		boolean paintPanel = true;
		boolean paintSheet = true;

		if(clip != null) {
			ExtRect sheetRect = new ExtRect(0, 0, w, h);
			ExtRect clipRect = new ExtRect(clip.x, clip.y, clip.width, clip.height);

			if(sheetRect.contains(clipRect)) {
				paintPanel = false;
			} else if(sheetRect.excludes(clipRect)) {
				paintSheet = false;
			}
		}

		if(paintPanel) {
			g.setColor(panelColor);
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
		}
		if(paintSheet) {
			int dx = clip.x + clip.width;
			int dy = clip.y + clip.height;
			if(w < dx) {
				dx = w;
			}
			if(h < dy) {
				dy = h;
			}
			g.setColor(sheetColor);
			g.fillRect(0, 0, dx, dy);
		}
	}

	public List findComponentsIn(ExtRect r) {
		List ret = new ArrayList();
		boolean mustContain = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SELECTION_CONTAINS);

		for(Iterator it = sheet.components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(!c.isVisible()) {
				continue;
			}

			ExtRect b = c.getScreenBounds(zoom);
			if(b.width == 0) {
				b.width = 1;
			}
			if(b.height == 0) {
				b.height = 1;
			}

			if((mustContain && r.contains(b)) || (!mustContain && r.intersects(b))) {
				ret.add(c);
			}
		}

		return ret;
	}

	public AbstractComponent findComponentOnScreen(Point screen) {
		int px = (int) (screen.x / zoom);
		int py = (int) (screen.y / zoom);

		return findComponent(new Point(px, py));
	}

	public AbstractComponent findComponent(Point pos) {
		int clickTolerance = (int) (CLICK_TOLERANCE / zoom);

		AbstractComponent c = Sheet.findComponent(selection, pos.x, pos.y, clickTolerance);
		if(c != null) {
			return c;
		}

		return sheet.findComponent(pos.x, pos.y, clickTolerance);
	}

	public boolean checkSplitOnScreen(Point screen) {
		int px = (int) (screen.x / zoom);
		int py = (int) (screen.y / zoom);
		Point p = constrainScreenPoint(screen.x, screen.y, false);

		return sheet.checkSplit(new Point(px, py), new Point(p.x, p.y), null);
	}

	public int getSelectionCount() {
		return selection.size();
	}

	public AbstractComponent findSubComponent(AbstractComponent c, Point p) {
		int clickTolerance = (int) (CLICK_TOLERANCE / zoom);
		Point op = new Point((int) (p.x / zoom), (int) (p.y / zoom));
		op = c.normalize(op);
		return c.childContains(op.x, op.y, clickTolerance);
	}

	public Handle findHandleOnScreen(AbstractComponent c, Point screen) {
		Point op = new Point((int) (screen.x / zoom), (int) (screen.y / zoom));
		int handleSize = (int) (Handle.SIZE / zoom);
		return sheet.findHandle(c, new Point(op.x, op.y), handleSize, false);
	}

	public void removeSelection() {
		for(Iterator it = new ArrayList(selection).iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.removeComponent(c, true);
		}
	}

	public void clearSelection() {
		List tmpSel = new ArrayList(selection);
		selection.clear();

		for(Iterator it = tmpSel.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			componentChanged(c);
		}
	}

	public void selectAll() {
		selection.clear();

		for(Iterator it = sheet.components(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			addToSelection(c);
		}
	}

	public List getSelection() {
		return selection;
	}

	public void addToSelection(AbstractComponent c) {
		if(!selection.contains(c)) {
			selection.add(c);
		}
		componentChanged(c);
	}

	public void addToSelection(List cs) {
		for(Iterator it = cs.iterator(); it.hasNext();) {
			addToSelection((AbstractComponent) it.next());
		}
	}

	public void toggleSelection(AbstractComponent c) {
		if(selection.contains(c)) {
			selection.remove(c);
		} else {
			selection.add(c);
		}
		componentChanged(c);
	}

	public boolean isSelected(AbstractComponent c) {
		return selection.contains(c);
	}

	private void processPopupMenu(Point p) {
		if(tool != null) {
			tool.cancel();
		}
		popupMenu.show(this, p.x, p.y);
	}

	private int countPins(List components) {
		int pins = 0;
		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(c instanceof Pin) {
				pins++;
			} else if(c.isGroup()) {
				pins += countPins(c.getComponents());
			}
		}
		return pins;
	}

	private int countGroups(List components) {
		int groups = 0;
		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(c.isGroup()) {
				groups++;
			}
		}
		return groups;
	}

	private Rectangle getToolImageRect() {
		int w = toolImage.getWidth(null);
		int h = toolImage.getHeight(null);
		return new Rectangle(toolImagePos.x, toolImagePos.y, w, h);
	}

	private void redrawToolImage(Point p) {
		if(toolImage != null) {
			Rectangle r = getToolImageRect();
			toolImagePos = p;
			toolImagePos.translate(10, 10);
			r.add(getToolImageRect());
			repaintLater(r.x, r.y, r.width, r.height);
		}
	}

	private void fireCurrentMousePosition(Point p) {
		Point2D mp = new Point2D.Double((double) p.x / zoom / 100, (double) p.y / zoom / 100);
		firePropertyChange(PROPERTY_MOUSE_POSITION, null, mp);
	}

	private void repaintComponent(AbstractComponent c) {
		try {
			ExtRect r = c.getScreenBounds(zoom).normalize();
			repaintLater(r.x - 20, r.y - 20, r.width + 40, r.height + 40);
		} catch(Exception e) {
			repaint();
		}
	}

	public void setZoom(double zoom) {
		double oldZoom = this.zoom;
		Rectangle r = getVisibleRect();
		final Point center = new Point((int) ((double) (r.x + r.width / 2) / zoom), (int) ((double) (r.y + r.height / 2) / zoom));
		this.zoom = zoom;
		adjustSize();
		setCenter(center);

		firePropertyChange(PROPERTY_ZOOM, oldZoom, zoom);
	}

	// ----------------------------------------

	@Override
	public void componentWillChange(AbstractComponent c) {
		repaintComponent(c);
	}

	@Override
	public void componentChanged(AbstractComponent c) {
		if(c.isVisible()) {
			repaintComponent(c);
		} else {
			selection.remove(c);
		}
	}

	@Override
	public void componentAdded(AbstractComponent c) {
		c.addComponentListener(this);
		componentChanged(c);
	}

	@Override
	public void componentRemoved(AbstractComponent c) {
		c.removeComponentListener(this);
		selection.remove(c);
		repaint();
	}

	@Override
	public void handlesMoved(Map handles, Point offset, boolean dragging) {
	}

	// ----------------------------------------

	@Override
	public void mouseDragged(MouseEvent e) {
		if(tool != null) {
			tool.mouseDrag(e);
		}

		fireCurrentMousePosition(e.getPoint());
		redrawToolImage(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(tool != null) {
			tool.mouseMove(e);
		}

		fireCurrentMousePosition(e.getPoint());
		redrawToolImage(e.getPoint());
	}

	// ----------------------------------------

	@Override
	public void mouseClicked(MouseEvent e) {
		if(tool != null) {
			tool.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOutside = false;
		if(toolImage != null) {
			repaint(getToolImageRect());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseOutside = true;
		if(toolImage != null) {
			repaint(getToolImageRect());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();

		allowPopupMenu = true;
		if(e.getButton() == MouseEvent.BUTTON3) {
			if((tool != null) && (tool.isActive())) {
				tool.cancel();
				allowPopupMenu = false;
			}
		}

		if(allowPopupMenu && e.isPopupTrigger()) {
			processPopupMenu(e.getPoint());
		} else {
			if(tool != null) {
				tool.mouseDown(e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(allowPopupMenu && e.isPopupTrigger()) {
			processPopupMenu(e.getPoint());
		} else {
			if(tool != null) {
				tool.mouseUp(e);
			}
		}
	}

	// ----------------------------------------

	@SuppressWarnings("null")
	public void drawSelectionFrame(ExtRect frame) {
		ExtRect oldFrame = this.frame;

		this.frame = frame;
		if(this.frame != null) {
			this.frame = this.frame.normalize();
		}

		if((oldFrame == null) && (frame == null)) {
			return;
		}

		if((oldFrame != null) && (frame == null)) {
			repaintLater(oldFrame.x - 2, oldFrame.y - 2, oldFrame.width + 4, oldFrame.height + 4);
		} else if((oldFrame == null) && (frame != null)) {
			repaintLater(frame.x - 2, frame.y - 2, frame.width + 4, frame.height + 4);
		} else {
			int fw = frame.x + frame.width;
			int fh = frame.y + frame.height;
			int ofw = oldFrame.x + oldFrame.width;
			int ofh = oldFrame.y + oldFrame.height;

			repaintAreaImmediately(frame.x, oldFrame.x, frame.y, oldFrame.y, fw, ofw, frame.y, oldFrame.y);
			repaintAreaImmediately(frame.x, oldFrame.x, frame.y, oldFrame.y, frame.x, oldFrame.x, fh, ofh);
			repaintAreaImmediately(frame.x, oldFrame.x, fh, ofh, fw, ofw, fh, ofh);
			repaintAreaImmediately(fw, ofw, frame.y, oldFrame.y, fw, ofw, fh, ofh);
		}
	}

	private void repaintAreaImmediately(int x1, int x2, int y1, int y2, int w1, int w2, int h1, int h2) {
		if(x2 < x1) {
			x1 = x2;
		}
		if(y2 < y1) {
			y1 = y2;
		}
		if(w2 > w1) {
			w1 = w2;
		}
		if(h2 > h1) {
			h1 = h2;
		}

		final int x = x1 - 2;
		final int y = y1 - 2;
		final int w = w1 - x1 + 4;
		final int h = h1 - y1 + 4;

		paintImmediately(x, y, w, h);
	}

	private void repaintLater(int x, int y, int w, int h) {
		repaint(0, x, y, w, h);
	}

	// ----------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == SchemOptions.instance()) {
			String pn = evt.getPropertyName();
			if(pn.equals(SchemOptions.PROPERTY_GRID_SPACING)) {
				grid = ((Integer) evt.getNewValue());
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_SHEET_COLOR)) {
				sheetColor = (Color) evt.getNewValue();
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_GRID_COLOR)) {
				gridColor = (Color) evt.getNewValue();
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_PAGE_BORDER_COLOR)) {
				pageBorderColor = (Color) evt.getNewValue();
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_SELECTION_BOX_COLOR)) {
				selectionFrameColor = (Color) evt.getNewValue();
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_GRID_SNAP)) {
				gridSnap = ((Boolean) evt.getNewValue());
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_GRID_SNAP_SPACING)) {
				gridSnapSpacing = ((Integer) evt.getNewValue());
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_ANTIALIASING)) {
				antiAliasing = ((Boolean) evt.getNewValue());
				repaint();
			} else if(pn.equals(SchemOptions.PROPERTY_GRID_VISIBLE)) {
				gridVisible = ((Boolean) evt.getNewValue());
				repaint();
			}
		} else if(evt.getPropertyName().equals(Sheet.PROPERTY_SIZE)) {
			adjustSize();
		}
	}

	// ---------------------------------------------

	@Override
	protected void processKeyEvent(KeyEvent e) {
		boolean passOn = true;
		if(tool != null) {
			if((e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
				tool.cancel();
			} else {
				passOn = !tool.keyPressed(e);
			}
		}

		if(passOn) {
			super.processKeyEvent(e);
		}
	}

	// ---------------------------------------------

	@Override
	public void addActionStateInfos(ActionStateInfos stateInfos) {
		stateInfos.put(SchemActions.STATE_INFO_NUM_GROUPS, countGroups(selection));
		stateInfos.put(SchemActions.STATE_INFO_NUM_PINS, countPins(selection));
		stateInfos.put(ActionStateInfos.STATE_INFO_NUM_SELECTED, selection.size());
		stateInfos.put(ActionStateInfos.STATE_INFO_NUM_OBJECTS_TO_SELECT, sheet.getComponents().size());
	}

}
