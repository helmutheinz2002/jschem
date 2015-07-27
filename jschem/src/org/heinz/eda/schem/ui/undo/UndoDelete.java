package org.heinz.eda.schem.ui.undo;

import java.util.Iterator;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;


public class UndoDelete extends UndoAction {
	public UndoDelete(Sheet sheet, List components) {
		super(sheet, components);
	}

	public String getPresentationName() {
		return Translator.translate("UNDO_DELETE");
	}

	public void redo() throws CannotRedoException {
		super.redo();
		
		for(Iterator it=components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.removeComponent(c, false);
		}
	}

	public void undo() throws CannotUndoException {
		super.undo();
		
		for(Iterator it=components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.addComponent(c);
		}
	}

	public void die() {
		super.die();
		
		for(Iterator it=components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(!sheet.hasComponent(c))
				c.release();
		}
	}
}
