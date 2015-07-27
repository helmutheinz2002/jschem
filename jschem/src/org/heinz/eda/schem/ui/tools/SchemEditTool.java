package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;

import org.heinz.eda.schem.ui.SchemActions;
import org.heinz.eda.schem.ui.SheetPanel;
import org.heinz.framework.crossplatform.EditTool;
import org.heinz.framework.crossplatform.EditToolListener;

public abstract class SchemEditTool implements EditTool, ActionListener {
	protected Point start;
	protected Point startPosition;
	protected SheetPanel sheetPanel;
	protected boolean cancel;
	private List toolbarObjects = new ArrayList();
	private String icon; 
	private EditToolListener toolListener;
	
	public SchemEditTool() {
		this(null);
	}
	
	public void addEditToolListener(EditToolListener toolListener) {
		this.toolListener = toolListener;
	}
	
	public void removeEditToolListener(EditToolListener toolListener) {
		toolListener = null;
	}
	
	protected SchemEditTool(String icon) {
		SchemActions.instance().addActionListener(this);
		this.icon = icon;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void cancel() {
		if(cancel)
			return;
		
		cancel = true;
		handleCancel();
		done();
	}
	
	public void setSheetPanel(SheetPanel sheetPanel) {
		this.sheetPanel = sheetPanel;
		setup();
	}

	public final void mouseDown(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			cancel();
		} else {
			start(e.getPoint());
			handleMouseDown(e);
		}
	}
	
	public final void mouseDrag(MouseEvent e) {
		if(!cancel && (start != null))
			handleMouseDrag(e);
	}

	public final void mouseMove(MouseEvent e) {
		if(!cancel && (start != null))
			handleMouseMove(e);
	}

	public final void mouseUp(MouseEvent e) {
		if(!cancel && (start != null))
			handleMouseUp(e);
	}

	public final void mouseClicked(MouseEvent e) {
		handleMouseClicked(e);
	}


	public boolean keyPressed(KeyEvent e) {
		if(!cancel && (start != null))
			return handleKey(e);
		return false;
	}
	
	protected abstract void handleCancel();
	protected abstract void handleMouseDown(MouseEvent e);
	protected abstract void handleMouseDrag(MouseEvent e);
	protected abstract void handleMouseUp(MouseEvent e);
	protected abstract boolean handleKey(KeyEvent e);
	
	protected void handleMouseMove(MouseEvent e) {
	}
	
	protected void handleMouseClicked(MouseEvent e) {
	}

	protected void setup() {
	}

	protected void done() {
		start = null;
		startPosition = null;
		try {
			toolListener.toolDone(this);
		} catch(Exception e) {
		}
	}
	
	protected void start(Point p) {
		start = p;
		startPosition = sheetPanel.constrainScreenPoint(start.x, start.y, false);
		cancel = false;
	}
	
	public List getToolbarObjects() {
		return toolbarObjects;
	}
	
	protected void addToolbarObject(JComponent c) {
		if(c instanceof AbstractButton) {
			AbstractButton b = (AbstractButton) c;
			final Action a = b.getAction();
			if(a == null)
				b.addActionListener(this);
			else
				b.setText(null);
		}

		toolbarObjects.add(c);
	}

	public boolean isActive() {
		return (start != null);
	}
	
	public void actionPerformed(ActionEvent e) {
	}
}
