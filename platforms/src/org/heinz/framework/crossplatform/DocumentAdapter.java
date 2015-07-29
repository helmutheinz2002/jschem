
package org.heinz.framework.crossplatform;

import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;

public class DocumentAdapter implements DocumentListener {

	@Override
	public void documentClosed(Document document) {
	}

	@Override
	public void documentClosing(Document document, boolean inApplicationQuit) throws DocumentCloseVetoException {
	}

	@Override
	public void documentActivated(Document document) {
	}

	@Override
	public void documentDeactivated(Document document) {
	}

}
