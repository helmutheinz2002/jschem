
package org.heinz.framework.crossplatform.utils;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

public class DocumentUndoManager extends UndoManager {

	UndoableEditSupport support = new UndoableEditSupport();

	@Override
	public boolean addEdit(UndoableEdit edit) {
		boolean ret = super.addEdit(edit);

		if(ret) {
			support.postEdit(edit);
		}

		return ret;
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		UndoableEdit edit = editToBeRedone();
		super.redo();
		support.postEdit(edit);
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		UndoableEdit edit = editToBeUndone();
		super.undo();
		support.postEdit(edit);
	}

	public void addUndoableEditListener(UndoableEditListener l) {
		support.addUndoableEditListener(l);
	}

	public void removeUndoableEditListener(UndoableEditListener l) {
		support.removeUndoableEditListener(l);
	}

}
