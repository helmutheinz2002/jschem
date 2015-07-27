package org.heinz.framework.crossplatform.utils;

import java.util.Iterator;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationListener;
import org.heinz.framework.crossplatform.Document;

public class ApplicationListenerSupport extends AbstractListenerSupport {

	public ApplicationListenerSupport(Application application) {
		super(application);
	}

	public void addApplicationListener(ApplicationListener l) {
		addListener(l);
	}

	public void removeApplicationListener(ApplicationListener l) {
		removeListener(l);
	}
	
	public void fireApplicationStarted() {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.applicationStarted();
		}
	}
	
	public void fireOpenFile(String filename) {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.openFile(filename);
		}
	}
	
	public void fireAbout() {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.about();
		}
	}
	
	public void firePreferences() {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.preferences();
		}
	}
	
	public void fireQuit() {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.quit();
		}
	}
	
	public void fireDocumentCreated(Document document) {
		for(Iterator it=listeners(); it.hasNext();) {
			ApplicationListener l = (ApplicationListener) it.next();
			l.documentCreated(document);
		}
	}
}
