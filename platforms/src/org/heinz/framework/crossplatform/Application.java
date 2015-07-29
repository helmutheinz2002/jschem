
package org.heinz.framework.crossplatform;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;

import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;



public interface Application extends FileSelector {

	static final String DEFAULT_DATA_PATH = "data/";

	void start();

	Document createDocument();

	void setTitle(String title);

	void setIconImage(Image icon);

	Frame getDialogOwner(Document document);

	Component getOptionPaneOwner();

	Dimension getSize();

	void addApplicationListener(ApplicationListener listener);

	void setMenuBarFactory(MenuBarFactory menuBarFactory);

	void setStatusBarFactory(StatusBarFactory statusBarFactory);

	void setToolBarFactory(ToolBarFactory toolBarFactory);

	void setEditToolBarFactory(EditToolBarFactory toolBarFactory);

	JMenu getWindowMenu();

	List getDocuments();

	void addPreferencesMenuItem(JMenu menu, Action action, boolean separator);

	void addAboutMenuItem(JMenu menu, Action action, boolean separator);

	void addCloseMenuItem(JMenu menu, Action action, boolean separator);

	void addQuitMenuItem(JMenu menu, Action action, boolean separator);

	Document getActiveDocument();

	void setCursor(Cursor cursor);

	ToolBar getToolBar(Document document);

	void closeAllDocuments() throws DocumentCloseVetoException;

}
