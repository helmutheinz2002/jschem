
package org.heinz.framework.crossplatform.utils;

import java.util.Iterator;

import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentListener;

public class DocumentListenerSupport extends AbstractListenerSupport {

	public DocumentListenerSupport(Document document) {
		super(document);
	}

	public void addDocumentListener(DocumentListener l) {
		addListener(l);
	}

	public void removeDocumentListener(DocumentListener l) {
		removeListener(l);
	}

	public void fireDocumentClosing(boolean inApplicationQuit) throws DocumentCloseVetoException {
		DocumentCloseVetoException exception = null;
		for(Iterator it = listeners(); it.hasNext();) {
			DocumentListener l = (DocumentListener) it.next();
			try {
				l.documentClosing((Document) sender, inApplicationQuit);
			} catch(DocumentCloseVetoException ex) {
				exception = ex;
			}
		}

		if(exception != null) {
			throw exception;
		}
	}

	public void fireDocumentClosed() {
		for(Iterator it = listeners(); it.hasNext();) {
			DocumentListener l = (DocumentListener) it.next();
			l.documentClosed((Document) sender);
		}
	}

	public void fireDocumentActivated() {
		for(Iterator it = listeners(); it.hasNext();) {
			DocumentListener l = (DocumentListener) it.next();
			l.documentActivated((Document) sender);
		}
	}

	public void fireDocumentDeactivated() {
		for(Iterator it = listeners(); it.hasNext();) {
			DocumentListener l = (DocumentListener) it.next();
			l.documentDeactivated((Document) sender);
		}
	}

}
