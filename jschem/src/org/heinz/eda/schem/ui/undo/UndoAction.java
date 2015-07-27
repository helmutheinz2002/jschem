package org.heinz.eda.schem.ui.undo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;

public class UndoAction extends AbstractUndoableEdit {
	protected List components;
	protected Sheet sheet;
	
	public UndoAction(Sheet sheet, List components) {
		this.sheet = sheet;
		this.components = new ArrayList(components);
	}
	
	public UndoAction(Sheet sheet, AbstractComponent component) {
		this(sheet, add(component));
	}
	
	private static List add(AbstractComponent component) {
		List l = new ArrayList();
		l.add(component);
		return l;
	}

}
