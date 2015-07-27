package org.heinz.framework.crossplatform;

public interface ApplicationListener {
	void applicationStarted();
	
	void documentCreated(Document document);
	
	void about();
	void openFile(String filename);
	void preferences();
	void quit();
}
