
package org.heinz.eda.schem.ui.undo.sheet;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoRenameSheet extends AbstractSheetOperation {

	protected String oldTitle;

	protected String newTitle;

	public UndoRenameSheet(Sheet sheet, String oldTitle) {
		super(null, sheet);
		this.newTitle = sheet.getTitle();
		this.oldTitle = oldTitle;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		sheet.setTitle(newTitle);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		sheet.setTitle(oldTitle);
	}

	@Override
	public String getPresentationName() {
		return Translator.translate("UNDO_RENAME_SHEET");
	}

}
