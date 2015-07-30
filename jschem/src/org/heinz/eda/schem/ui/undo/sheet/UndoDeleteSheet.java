
package org.heinz.eda.schem.ui.undo.sheet;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoDeleteSheet extends AbstractSheetOperation {

	protected int index;

	public UndoDeleteSheet(Schematics schematics, Sheet sheet, int index) {
		super(schematics, sheet);
		this.index = index;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		schematics.removeSheet(sheet, false);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		schematics.addSheet(sheet, index);
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_DELETE_SHEET");
	}

}
