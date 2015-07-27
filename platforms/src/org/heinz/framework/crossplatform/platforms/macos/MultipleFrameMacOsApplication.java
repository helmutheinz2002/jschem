package org.heinz.framework.crossplatform.platforms.macos;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.ben.macos.OSXApp;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.MenuBarFactory;
import org.heinz.framework.crossplatform.platforms.basic.FrameDocument;
import org.heinz.framework.crossplatform.platforms.basic.MultipleFrameApplication;
import org.heinz.framework.crossplatform.utils.MenuHelper;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public class MultipleFrameMacOsApplication extends MultipleFrameApplication implements OSXApp {
	private JFrame hiddenFrame;
	private boolean hiddenDocument;
	
	public MultipleFrameMacOsApplication() {
		super(true);
		
		hiddenFrame = new JFrame();
		hiddenFrame.setUndecorated(true);
		hiddenFrame.setSize(new Dimension(0, 0));
		hiddenFrame.setLocation(-10, -10);
		hiddenFrame.setVisible(true);
		
		hiddenFrame.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				als.fireApplicationStarted();
			}

			public void windowActivated(WindowEvent e) {
				JOptionPane.setRootFrame(hiddenFrame);
			}
		});
		
		JOptionPane.setRootFrame(hiddenFrame);
		
		windowStacker.setStartPosition(new Point(10, 30));
		new MacOsApplicationSupport(als);
	}
	
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = super.createMenuBar();
		MenuHelper.cleanMenus(menuBar, true, false, false);
		return menuBar;
	}
	
	protected void lastDocumentClosed() {
		// No problem on Mac OS X
	}

	public Frame getDialogOwner(Document document) {
		if(document == null)
			return hiddenFrame;
		return (FrameDocument) document;
	}

	protected boolean isDocumentInCreation() {
		return super.isDocumentInCreation() || hiddenDocument;
	}

	public synchronized void setMenuBarFactory(MenuBarFactory menuBarFactory) {
		super.setMenuBarFactory(menuBarFactory);
		hiddenDocument = true;
		hiddenFrame.setJMenuBar(createMenuBar());
		hiddenDocument = false;
	}

	public void addAboutMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

	public void addPreferencesMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

	public void addQuitMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

	public File selectFile(File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
		Component owner = getActiveDocument() != null ? getActiveDocument().getContainer() : getOptionPaneOwner();
		return MacOsFileSelection.selectFile(getDialogOwner(null), owner , defaultFile, fileFilter, extEnsurer, save, title);
	}
}
