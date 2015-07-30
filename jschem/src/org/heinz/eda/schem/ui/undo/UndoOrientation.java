
package org.heinz.eda.schem.ui.undo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoOrientation extends UndoStoredDataAction {

	public UndoOrientation(Sheet sheet, List components, Map oldOrientations, Map newOrientations) {
		super(sheet, components, oldOrientations, newOrientations);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Orientation o = (Orientation) getNewData().get(c);
			c.setOrientation(o);
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Orientation o = (Orientation) getOldData().get(c);
			c.setOrientation(o);
		}
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_ORIENTATION");
	}

}
