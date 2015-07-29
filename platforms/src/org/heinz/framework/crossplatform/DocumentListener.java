
package org.heinz.framework.crossplatform;

import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;

public interface DocumentListener {

	public static final int CONTINUE = 0;

	public static final int ABORT = 1;

	void documentClosing(Document document, boolean inApplicationQuit) throws DocumentCloseVetoException;

	void documentClosed(Document document);

	void documentActivated(Document document);

	void documentDeactivated(Document document);

}
