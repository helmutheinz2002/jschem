package org.heinz.eda.schem.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.heinz.eda.schem.model.ComponentListener;
import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.SchematicsListener;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.ui.tools.SchemEditTool;
import org.heinz.framework.crossplatform.EditTool;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfoProvider;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfos;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.export.ExportImageProducer;

public class SchemTabbook extends JTabbedPane implements SchematicsListener, ComponentListener,
	PropertyChangeListener, ExportImageProducer, ActionStateInfoProvider {
	public static final String PROPERTY_MOUSE_POSITION = "SchemTabbook.mousePosition";
	
	private Schematics schematics;
	private SchemEditTool editTool;
	
	public SchemTabbook(Schematics schematics) {
		super();
		this.schematics = schematics;
		
		setTabPlacement(JTabbedPane.BOTTOM);
		init();
		setSelectedIndex(0);
	}
	
	public void setZoomForAll(double zoom) {
		for(Iterator it=editors(); it.hasNext();) {
			SheetPanel sp = (SheetPanel) it.next();
			sp.setZoom(zoom);
		}
	}
	
	public Schematics getSchematics() {
		return schematics;
	}
	
	public void setEditTool(EditTool editTool) {
		this.editTool = (SchemEditTool) editTool;
		setEditTool(getCurrentEditor(), false);
	}
	
	private void setEditTool(SheetPanel sheetPanel, boolean force) {
		sheetPanel.setEditTool(editTool, force);
	}
	
	public SheetPanel getEditorAt(int index) {
		JScrollPane sp = (JScrollPane) getComponentAt(index);
		return (SheetPanel) sp.getViewport().getView();
	}
	
	public Iterator editors() {
		List e = new ArrayList();
		int tabs = getTabCount();
		for(int i=0; i<tabs; i++)
			e.add(getEditorAt(i));
		return e.iterator();
	}
	
	public SheetPanel getCurrentEditor() {
		JScrollPane sp = (JScrollPane) getSelectedComponent();
		return (SheetPanel) sp.getViewport().getView();
	}
	
	public Sheet getCurrentSheet() {
		int idx = getSelectedIndex();
		return schematics.getSheetAt(idx);
	}
	
	private void init() {
		int nr = 0;
		for(Iterator it=schematics.sheets(); it.hasNext(); nr++) {
			Sheet sheet = (Sheet) it.next();
			addPage(sheet, nr);
		}
		
		schematics.addSchematicsListener(this);
		
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				SheetPanel sp = getCurrentEditor();
				setEditTool(sp, true);
				ApplicationActions.instance().setActionStates();
			}
		});
		
		ApplicationActions.instance().setActionStates();
	}
	
	public void release() {
		ApplicationActions.instance().removeStateInfoProvider(this);
	}
	
	private void addPage(Sheet sheet, int newIndex) {
		SheetPanel sheetPanel = new SheetPanel(sheet);
		JScrollPane sp = new JScrollPane(sheetPanel);
		sp.setBorder(null);
		sp.getHorizontalScrollBar().setUnitIncrement(20);
		sp.getVerticalScrollBar().setUnitIncrement(20);
		sheet.addComponentListener(this);
		sheet.addPropertyChangeListener(this);
		insertTab(sheet.getTitle(), null, sp, null, newIndex);
		setSelectedIndex(newIndex);
		sheetPanel.setEditTool(editTool, false);
		sheetPanel.addPropertyChangeListener(this);
		
		requestFocusInWindow();
		ApplicationActions.instance().setActionStates();
	}
	
	private int getSheetIndex(Sheet sheet) {
		int idx = 0;
		for(Iterator it=schematics.sheets(); it.hasNext(); idx++) {
			Sheet s = (Sheet) it.next();
			if(sheet == s)
				return idx;
		}
		return -1;
	}
	
	public void sheetAdded(Sheet sheet, int newIndex) {
		addPage(sheet, newIndex);
	}

	public void sheetMoved(Sheet sheet, int oldIndex, int newIndex) {
		Component page = getComponentAt(oldIndex);
		removeTabAt(oldIndex);
		insertTab(sheet.getTitle(), null, page, null, newIndex);
		setSelectedIndex(newIndex);
		ApplicationActions.instance().setActionStates();
	}

	public void componentAdded(org.heinz.eda.schem.model.components.AbstractComponent c) {
	}

	public void componentRemoved(org.heinz.eda.schem.model.components.AbstractComponent c) {
	}

	public void sheetRemoved(Sheet sheet, int oldIndex) {
		sheet.removeComponentListener(this);
		SheetPanel p = getEditorAt(oldIndex);
		removeTabAt(oldIndex);
		p.getSheet().removePropertyChangeListener(this);
		p.release();
		
		ApplicationActions.instance().setActionStates();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Sheet.PROPERTY_TITLE)) {
			int idx = getSheetIndex((Sheet) evt.getSource());
			if(idx >= 0)
				setTitleAt(idx, (String) evt.getNewValue());
		} else if(evt.getPropertyName().equals(SheetPanel.PROPERTY_MOUSE_POSITION)) {
			if(evt.getSource() == getCurrentEditor())
				firePropertyChange(PROPERTY_MOUSE_POSITION, evt.getOldValue(), evt.getNewValue());
		}
	}

	public BufferedImage createExportImage(int page) {
		return schematics.getSheetAt(page).getImage();
	}

	public int getNumPages() {
		return schematics.getSheetCount();
	}

	public void componentChanged(AbstractComponent c) {
	}

	public void componentWillChange(AbstractComponent c) {
	}

	public void handlesMoved(Map handles, Point offset, boolean dragging) {
	}

	public void addActionStateInfos(ActionStateInfos stateInfos) {
		int numSheets = schematics.getSheetCount();
		boolean isLeft = (getSelectedIndex() == 0);
		boolean isRight = (getSelectedIndex() == (numSheets-1));
		stateInfos.put(SchemActions.STATE_INFO_NUM_SHEETS, new Integer(numSheets));
		stateInfos.put(SchemActions.STATE_INFO_CAN_MOVE_LEFT, new Boolean(!isLeft));
		stateInfos.put(SchemActions.STATE_INFO_CAN_MOVE_RIGHT, new Boolean(!isRight));
		
		SheetPanel panel = getCurrentEditor();
		if(panel != null)
			panel.addActionStateInfos(stateInfos);
	}
}
