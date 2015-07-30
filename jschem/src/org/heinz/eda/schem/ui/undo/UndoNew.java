
package org.heinz.eda.schem.ui.undo;

import java.util.Iterator;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoNew extends UndoAction {

	public UndoNew(Sheet sheet, List components) {
		super(sheet, components);
	}

	public UndoNew(Sheet sheet, AbstractComponent component) {
		super(sheet, component);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.addComponent(c);
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			sheet.removeComponent(c, false);
		}
	}

	@Override
	public void die() {
		super.die();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			if(!sheet.hasComponent(c)) {
				c.release();
			}
		}
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_NEW");
	}

}
