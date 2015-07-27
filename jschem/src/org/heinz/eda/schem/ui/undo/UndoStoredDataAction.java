package org.heinz.eda.schem.ui.undo;

import java.util.List;
import java.util.Map;

import org.heinz.eda.schem.model.Sheet;

public class UndoStoredDataAction extends UndoAction {
	private final Map oldData;
	private final Map newData;
	
	public UndoStoredDataAction(Sheet sheet, List components, Map oldData, Map newData) {
		super(sheet, components);
		this.oldData = oldData;
		this.newData = newData;
	}

	public Map getNewData() {
		return newData;
	}

	public Map getOldData() {
		return oldData;
	}
}
