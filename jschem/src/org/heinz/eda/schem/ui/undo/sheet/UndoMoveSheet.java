
package org.heinz.eda.schem.ui.undo.sheet;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoMoveSheet extends AbstractSheetOperation {

	protected boolean left;

	public UndoMoveSheet(Schematics schematics, Sheet sheet, boolean left) {
		super(schematics, sheet);
		this.left = left;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		schematics.moveSheet(sheet, left);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		schematics.moveSheet(sheet, !left);
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_MOVE_SHEET");
	}

}
