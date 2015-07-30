
package org.heinz.eda.schem.ui.undo.sheet;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoAddSheet extends AbstractSheetOperation {

	public UndoAddSheet(Schematics schematics, Sheet sheet) {
		super(schematics, sheet);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		schematics.addSheet(sheet);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		schematics.removeSheet(sheet, false);
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_ADD_SHEET");
	}

}
