
package org.heinz.framework.crossplatform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class EditToolBar extends JToolBar implements EditToolListener, ActionListener {

	private final List listeners = new ArrayList();

	private Document document;

	private final ButtonGroup group = new ButtonGroup();

	private final Map toolsByButton = new HashMap();

	private final Map toolsByClass = new HashMap();

	private final Map buttonsByTool = new HashMap();

	private final Class defaultToolClass;

	private EditTool currentTool;

	public EditToolBar(Class defaultToolClass) {
		this.defaultToolClass = defaultToolClass;

		setFloatable(false);
		setOrientation(JToolBar.VERTICAL);
		setBorder(null);
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	public EditTool getCurrentTool() {
		return currentTool;
	}

	public EditTool getTool(Class toolClass) {
		return (EditTool) toolsByClass.get(toolClass);
	}

	public void setDefaultTool() {
		if(defaultToolClass == null) {
			return;
		}
		EditTool defaultTool = (EditTool) toolsByClass.get(defaultToolClass);
		selectTool(defaultTool);
	}

	protected JToggleButton addEditTool(Action action, EditTool tool) {
		final JToggleButton b = new JToggleButton(action);
		b.addActionListener(this);
		b.setText(null);
		group.add(b);
		add(b);

		toolsByButton.put(b, tool);
		buttonsByTool.put(tool, b);
		toolsByClass.put(tool.getClass(), tool);

		tool.addEditToolListener(this);
		return b;
	}

	protected EditTool getToolForButton(JToggleButton a) {
		return (EditTool) toolsByButton.get(a);
	}

	public void selectTool(EditTool tool) {
		JToggleButton b = (JToggleButton) buttonsByTool.get(tool);
		b.doClick();
		currentTool = tool;
	}

	public void addEditToolBarListener(EditToolBarListener l) {
		if(!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeEditToolBarListener(EditToolBarListener l) {
		listeners.remove(l);
	}

	public void fireCurrentTool() {
		fireEditToolChanged(getCurrentTool());
	}

	protected void fireEditToolChanged(EditTool tool) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			EditToolBarListener l = (EditToolBarListener) it.next();
			l.toolChanged(document, tool);
		}
	}

	@Override
	public void toolDone(EditTool tool) {
		EditTool newTool = (EditTool) toolsByClass.get(getNextTool(tool.getClass()));
		if(newTool != tool) {
			selectTool(newTool);
		}
	}

	protected Class getNextTool(Class toolClass) {
		// to be overridden by subclasses
		return toolClass;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		EditTool tool = getToolForButton((JToggleButton) e.getSource());
		if(tool == null) {
			return;
		}

		currentTool = tool;
		fireEditToolChanged(tool);
	}

}
