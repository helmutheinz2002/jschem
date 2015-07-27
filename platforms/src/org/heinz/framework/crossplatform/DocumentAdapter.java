package org.heinz.framework.crossplatform;

import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;

public class DocumentAdapter implements DocumentListener {
	public void documentClosed(Document document) {
	}

	public void documentClosing(Document document, boolean inApplicationQuit) throws DocumentCloseVetoException {
	}

	public void documentActivated(Document document) {
	}

	public void documentDeactivated(Document document) {
	}
}
