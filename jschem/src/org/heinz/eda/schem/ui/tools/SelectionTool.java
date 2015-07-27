package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Handle;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.ui.SchemActions;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;

public class SelectionTool extends SchemEditTool {
	private AbstractComponent startComponent;
	private Robot robot;
	private Point lastPosition;
	private Point lastScreenPoint;
	private Map orgLocations = new HashMap();
	private boolean shiftDown;
	private boolean toggle;
	private boolean wasDragged;
	
	public SelectionTool() {
		try {
			robot = new Robot();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		addToolbarObject(new JButton(SchemActions.instance().rotateItem));
		addToolbarObject(new JButton(SchemActions.instance().rotateNoTextItem));
		addToolbarObject(new JToolBar.Separator());
		addToolbarObject(new JButton(SchemActions.instance().flipLeftRightItem));
		addToolbarObject(new JButton(SchemActions.instance().flipLeftRightNoTextItem));
		addToolbarObject(new JButton(SchemActions.instance().flipTopBottomItem));
		addToolbarObject(new JButton(SchemActions.instance().flipTopBottomNoTextItem));
	}
	
	protected void handleCancel() {
		sheetPanel.drawSelectionFrame(null);
		if(start != null)
			moveComponents(0, 0, true, false);
	}

	protected void handleMouseClicked(MouseEvent e) {
		if((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
			lastScreenPoint = e.getPoint();
			AbstractComponent c = sheetPanel.findComponentOnScreen(lastScreenPoint);

			if(c != null) {
				ApplicationActions.instance().propertiesItem.fireActionPerformed();
			} else {
				Point up = sheetPanel.constrainScreenPoint(lastScreenPoint.x, lastScreenPoint.y, false);
				sheetPanel.setCenter(up);
			}
		}
	}

	protected void handleMouseDown(MouseEvent e) {
		lastScreenPoint = e.getPoint();
		startComponent = sheetPanel.findComponentOnScreen(lastScreenPoint);
		shiftDown = e.isShiftDown();
		toggle = false;
		wasDragged = false;
		
		if (startComponent != null) {
			Handle handle = sheetPanel.findHandleOnScreen(startComponent, lastScreenPoint);
			AbstractComponent subComp = sheetPanel.findSubComponent(startComponent, lastScreenPoint);

			orgLocations.clear();
			lastPosition = new Point(0, 0);

			toggle = (handle == null) && (shiftDown || e.isControlDown());
			if ((handle != null) || (!sheetPanel.isSelected(startComponent) && !toggle))
				sheetPanel.clearSelection();
			if (toggle)
				sheetPanel.toggleSelection(startComponent);
			else
				sheetPanel.addToSelection(startComponent);

			boolean subCompHit = ((subComp != null) && subComp.isMobile());
			boolean handleHit = (handle != null);
			if (!toggle && (subCompHit || handleHit) && (sheetPanel.getSelectionCount() == 1) && sheetPanel.isSelected(startComponent)) {
				if(subCompHit)
					orgLocations.put(subComp, subComp.getPosition());
				if(handleHit)
					orgLocations.put(handle, handle.getHandlePosition());
			} else {
				for (Iterator it = sheetPanel.getSelection().iterator(); it.hasNext();) {
					AbstractComponent c = (AbstractComponent) it.next();
					orgLocations.put(c, c.getPosition());
				}
			}
		}
	}

	protected boolean handleKey(KeyEvent e) {
		if((startComponent != null) && (robot != null)) {
			int ox = 0;
			int oy = 0;
			int grid = 1;
			
			switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					oy = -grid;
					break;
				case KeyEvent.VK_DOWN:
					oy = grid;
					break;
				case KeyEvent.VK_LEFT:
					ox = -grid;
					break;
				case KeyEvent.VK_RIGHT:
					ox = grid;
					break;
				default:
					break;
			}
			
			Point p = new Point(lastScreenPoint);
			SwingUtilities.convertPointToScreen(p, sheetPanel);
			if((ox != 0) || (oy != 0)) {
				robot.mouseMove(p.x + ox, p.y + oy);
				return true;
			}
		}
		return false;
	}
	
	protected void handleMouseDrag(MouseEvent e) {
		lastScreenPoint = e.getPoint();
		wasDragged = true;

		int ox = lastScreenPoint.x - start.x;
		int oy = lastScreenPoint.y - start.y;

		if (startComponent == null) {
			ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
			sheetPanel.drawSelectionFrame(frame);
		} else
			moveComponents(ox, oy, false, true);
	}

	protected void handleMouseUp(MouseEvent e) {
		lastScreenPoint = e.getPoint();
		int ox = lastScreenPoint.x - start.x;
		int oy = lastScreenPoint.y - start.y;
		
		ExtRect frame = new ExtRect(start.x, start.y, ox, oy);
		sheetPanel.drawSelectionFrame(null);

		if (startComponent == null) {
			if (!toggle)
				sheetPanel.clearSelection();

			List comps = sheetPanel.findComponentsIn(frame);
			for (Iterator it = comps.iterator(); it.hasNext();) {
				AbstractComponent c = (AbstractComponent) it.next();
				if (toggle)
					sheetPanel.toggleSelection(c);
				else
					sheetPanel.addToSelection(c);
			}
		} else {
			if(wasDragged)
				moveComponents(ox, oy, false, false);
		}
		
		done();
	}

	protected void done() {
		if((startComponent != null) && (sheetPanel.getSelectionCount() > 0)) {
			Map oldPos = new HashMap(orgLocations);
			Map newPos = new HashMap();
			for (Iterator it = sheetPanel.getSelection().iterator(); it.hasNext();) {
				AbstractComponent c = (AbstractComponent) it.next();
				Point op = (Point) oldPos.get(c);
				if(op != null) {
					Point np = c.getPosition();
					if(!op.equals(np))
						newPos.put(c, c.getPosition());
				}
			}
		
//			if(newPos.size() > 0)
//				ApplicationUndoManager.instance().getUndoManager(sheetPanel).addEdit(new UndoMove(sheetPanel.getSheet(), sheetPanel.getSelection(), oldPos, newPos));
		}
		
		super.done();
	}

	private void moveComponents(int ox, int oy, boolean snap, boolean dragging) {
		Point op = sheetPanel.convertScreenToUnits(ox, oy);
		Point cop = sheetPanel.constrainPoint(op.x, op.y, snap);
		Point offset = new Point(cop);
		if(lastPosition == null)
			return;
		offset.translate(-lastPosition.x, -lastPosition.y);
		
		Map movedHandles = new HashMap();
		
		for (Iterator it = orgLocations.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			
			if(key instanceof AbstractComponent) {
				Point l = (Point) orgLocations.get(key);
				l = sheetPanel.constrainPoint(l, snap);
				AbstractComponent c = (AbstractComponent) key;
				
				Point p = new Point(cop);
				AbstractComponent parent = c.getParent();
				if(parent != null)
					p = parent.getOrientation().unTransform(p);
			
				Map compHandles = c.getHandlePositions();
				movedHandles.putAll(compHandles);
				c.setPosition(l.x + p.x, l.y + p.y, false, dragging);
			} else if(key instanceof Handle) {
				Handle h = (Handle) key;
				
				if(shiftDown && !dragging)
					h.setHandlePosition(offset, true, dragging);
				else
					h.setHandlePosition(offset, !shiftDown, dragging);
			}
		}
		
		lastPosition = cop;
		if(movedHandles.size() > 0)
			sheetPanel.getSheet().handlesMoved(movedHandles, offset, dragging);
	}

	/*
	private Map getOrientations(List sel) {
		Map os = new HashMap();
		
		for (Iterator it = sel.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Orientation o = c.getOrientation();
			os.put(c, o);
		}
		
		return os;
	}
	*/
	
	private void rotateTextsBackwards(AbstractComponent c) {
		if(c instanceof Text) {
			Orientation o = c.getOrientation();
			o = o.getPrevOrientation();
			c.setOrientation(o);
		}
		
		for(Iterator it=c.elements(); it.hasNext();) {
			rotateTextsBackwards((AbstractComponent) it.next());
		}
	}
	
	private List findTexts(AbstractComponent c) {
		List ret = new ArrayList();
		
		if(c instanceof Text)
			ret.add(c);
		
		for(Iterator it=c.elements(); it.hasNext();) {
			AbstractComponent child = (AbstractComponent) it.next();
			ret.addAll(findTexts(child));
		}
		
		return ret;
	}
	
	private void rotateObjects(boolean noTexts) {
		List sel = sheetPanel.getSelection();
//		Map oldOs = getOrientations(sel);
		
		for (Iterator it = sel.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Orientation orientation = c.getOrientation().getNextOrientation();
			c.setOrientation(orientation);
			if(noTexts)
				rotateTextsBackwards(c);
		}
		
//		Map newOs = getOrientations(sel);
//		ApplicationUndoManager.instance().getUndoManager(sheetPanel).addEdit(new UndoOrientation(sheetPanel.getSheet(), sel, oldOs, newOs));
	}
	
	private void flipObjects(List objects, boolean lr, boolean withTexts) {
//		Map oldOs = getOrientations(objects);
		
		for (Iterator it = objects.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Orientation o = c.getOrientation();
			if(lr)
				c.setOrientation(o.flipHorizontal());
			else
				c.setOrientation(o.flipVertical());
			
			if(!withTexts) {
				List texts = findTexts(c);
				flipObjects(texts, lr, true);
			}
		}
		
//		Map newOs = getOrientations(objects);
//		ApplicationUndoManager.instance().getUndoManager(sheetPanel).addEdit(new UndoOrientation(sheetPanel.getSheet(), objects, oldOs, newOs));
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == SchemActions.instance().rotateItem) {
			rotateObjects(false);
		} else if(e.getSource() == SchemActions.instance().rotateNoTextItem) {
			rotateObjects(true);
		} else if(e.getSource() == SchemActions.instance().flipLeftRightItem) {
			flipObjects(sheetPanel.getSelection(), true, true);
		} else if(e.getSource() == SchemActions.instance().flipTopBottomItem) {
			flipObjects(sheetPanel.getSelection(), false, true);
		} else if(e.getSource() == SchemActions.instance().flipLeftRightNoTextItem) {
			flipObjects(sheetPanel.getSelection(), true, false);
		} else if(e.getSource() == SchemActions.instance().flipTopBottomNoTextItem) {
			flipObjects(sheetPanel.getSelection(), false, false);
		}
	}
}
