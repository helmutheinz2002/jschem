package org.heinz.framework.crossplatform.utils;

import javax.swing.event.UndoableEditEvent;

import org.heinz.framework.crossplatform.Document;

public interface ApplicationUndoListener {
	public void undoableEditHappened(Document document, UndoableEditEvent e);
}
