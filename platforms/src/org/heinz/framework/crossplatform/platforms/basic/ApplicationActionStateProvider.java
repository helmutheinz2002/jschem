package org.heinz.framework.crossplatform.platforms.basic;


import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationAdapter;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;

public class ApplicationActionStateProvider extends DocumentAdapter implements ActionStateInfoProvider {
	private Document activeDocument;
	private final Application application;
	private final ApplicationActions actions;
	
	public ApplicationActionStateProvider(Application application, ApplicationActions actions) {
		this.application = application;
		this.actions = actions;
		
		application.addApplicationListener(new ApplicationAdapter() {
			public void documentCreated(Document document) {
				document.addDocumentListener(ApplicationActionStateProvider.this);
			}
		});
		
		actions.addStateInfoProvider(this);
		activeDocument = application.getActiveDocument();
		actions.setActionStates();
	}

	public void documentClosed(Document document) {
		document.removeDocumentListener(this);
		activeDocument = application.getActiveDocument();
		actions.setActionStates();
	}

	public void documentActivated(Document document) {
		activeDocument = application.getActiveDocument();
		actions.setActionStates();
	}

	public void documentDeactivated(Document document) {
		activeDocument = application.getActiveDocument();
		actions.setActionStates();
	}

	public void addActionStateInfos(ActionStateInfos stateInfos) {
		stateInfos.put(ActionStateInfos.STATE_INFO_ACTIVE_DOCUMENT, activeDocument);
	}
}
