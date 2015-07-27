package org.heinz.framework.crossplatform.utils;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationAdapter;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;

public class ApplicationUndoManager extends UndoManager {
	private static ApplicationUndoManager instance;
	
	private Map undoManagersByDocument = new HashMap();
	private DocumentUndoManager applicationUndoManager = new DocumentUndoManager();
	private List listeners = new ArrayList();
	
	public ApplicationUndoManager(Application application) {
		if(instance != null)
			throw new IllegalStateException("Instance already set");
		
		instance = this;
			
		application.addApplicationListener(new ApplicationAdapter() {
			public void documentCreated(Document document) {
				addDocument(document);
			}
		});
	}
	
	public static ApplicationUndoManager instance() {
		return instance;
	}
	
	public void addApplicationUndoListener(ApplicationUndoListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeApplicationUndoListener(ApplicationUndoListener listener) {
		listeners.remove(listener);
	}
	
	public DocumentUndoManager getCurrentUndoManager() {
		Document document = CrossPlatform.getPlatform().getApplication().getActiveDocument();
		if(document == null)
			return applicationUndoManager;
		return (DocumentUndoManager) undoManagersByDocument.get(document);
	}
	
	public DocumentUndoManager getUndoManager(Document document) {
		if(document == null)
			return applicationUndoManager;
		return (DocumentUndoManager) undoManagersByDocument.get(document);
	}
	
	public DocumentUndoManager getUndoManager(Component component) {
		if(component == null)
			return applicationUndoManager;
		
		Application application = CrossPlatform.getPlatform().getApplication();
		List documents = application.getDocuments();
		
		while(true) {
			if(documents.contains(component))
				return (DocumentUndoManager) undoManagersByDocument.get(component);
			component = component.getParent();
			if(component == null)
				break;
		}
		
		return null;
	}
	
	private void addDocument(final Document document) {
		DocumentUndoManager undoManager = new DocumentUndoManager();
		undoManagersByDocument.put(document, undoManager);
		
		document.addDocumentListener(new DocumentAdapter() {
			public void documentClosed(Document document) {
				removeDocument(document);
			}
		});
		
		undoManager.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				fireUndoableEdit(document, e);
			}
		});
	}
	
	private void removeDocument(Document document) {
		undoManagersByDocument.remove(document);
	}
	
	private void fireUndoableEdit(Document document, UndoableEditEvent e) {
		for(Iterator it=listeners.iterator(); it.hasNext();) {
			ApplicationUndoListener l = (ApplicationUndoListener) it.next();
			l.undoableEditHappened(document, e);
		}
	}
}
