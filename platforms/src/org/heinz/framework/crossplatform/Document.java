package org.heinz.framework.crossplatform;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;

import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfoProvider;

public interface Document extends ActionStateInfoProvider {
	String getTitle();
	void setTitle(String title);
	Dimension getSize();
	void setSize(int width, int height);
	void setLocation(int width, int height);
	void setIconImage(Image icon);
	
	void addDocumentListener(DocumentListener listener);
	void removeDocumentListener(DocumentListener listener);

	Container getContainer();
	void setDocumentPane(Component pane);
	Component getDocumentPane();
	
	EditToolBar getEditToolBar();
	
	void dispose();
	boolean requestFocusInWindow();
	void setSelected();
	boolean isSelected();
	void toFront();
	void setIconified(boolean b);
	void setMaximized(boolean b);
	
	boolean isIconified();
	boolean isMaximized();
	
	void setCursor(Cursor cursor);
	void setProperty(Object key, Object value);
	Object getProperty(Object key);
	
	void addStateInfoProvider(ActionStateInfoProvider stateInfoProvider);
	void removeStateInfoProvider(ActionStateInfoProvider stateInfoProvider);
}
