
package org.heinz.eda.schem.ui.undo;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoMove extends UndoStoredDataAction {

	public UndoMove(Sheet sheet, List components, Map oldPositions, Map newPositions) {
		super(sheet, components, oldPositions, newPositions);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Point p = (Point) getNewData().get(c);
			c.setPosition(p.x, p.y);
		}
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		for(Iterator it = components.iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			Point p = (Point) getOldData().get(c);
			c.setPosition(p.x, p.y);
		}
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_MOVE");
	}

}
