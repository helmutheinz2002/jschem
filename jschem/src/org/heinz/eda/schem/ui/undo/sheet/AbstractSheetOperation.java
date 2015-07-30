
package org.heinz.eda.schem.ui.undo.sheet;

import javax.swing.undo.AbstractUndoableEdit;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;

public abstract class AbstractSheetOperation extends AbstractUndoableEdit {

	protected Schematics schematics;

	protected Sheet sheet;

	public AbstractSheetOperation(Schematics schematics, Sheet sheet) {
		this.schematics = schematics;
		this.sheet = sheet;
	}

	@Override
	public void die() {
		super.die();

		if((sheet != null) && !schematics.containsSheet(sheet)) {
			sheet.release();
		}

		sheet = null;
		schematics = null;
	}

}
