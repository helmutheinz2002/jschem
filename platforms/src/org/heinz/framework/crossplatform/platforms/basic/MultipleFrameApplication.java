
package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationListener;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.EditToolBarFactory;
import org.heinz.framework.crossplatform.MenuBarFactory;
import org.heinz.framework.crossplatform.StatusBarFactory;
import org.heinz.framework.crossplatform.ToolBar;
import org.heinz.framework.crossplatform.ToolBarFactory;
import org.heinz.framework.crossplatform.utils.ApplicationListenerSupport;
import org.heinz.framework.crossplatform.utils.ApplicationUndoManager;
import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;
import org.heinz.framework.crossplatform.utils.DocumentProperty;
import org.heinz.framework.crossplatform.utils.MenuHelper;
import org.heinz.framework.crossplatform.utils.WindowMenuHelper;
import org.heinz.framework.crossplatform.utils.WindowStacker;
import org.heinz.framework.utils.FileDragAndDrop;
import org.heinz.framework.utils.ViewUtils;

public abstract class MultipleFrameApplication implements Application, DocumentProperty, ActionStateInfoProvider {

	protected WindowStacker windowStacker = new WindowStacker();

	protected ApplicationListenerSupport als = new ApplicationListenerSupport(this);

	private MenuBarFactory menuBarFactory;

	private StatusBarFactory statusBarFactory;

	private ToolBarFactory toolBarFactory;

	private EditToolBarFactory editToolBarFactory;

	private final List documents = new ArrayList();

	private final WindowMenuHelper windowMenuHelper;

	private final Map windowMenuByDocument = new HashMap();

	private Document documentInCreation;

	private final boolean withMenuDisable;

	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "LeakingThisInConstructor"})
	public MultipleFrameApplication(boolean withMenuDisable) {
		this.withMenuDisable = withMenuDisable;

		windowMenuHelper = new WindowMenuHelper(this, false, withMenuDisable);

		new ApplicationUndoManager(this);
		ApplicationActions actions = new ApplicationActions();
		new ApplicationActionStateProvider(this, actions);
		actions.addStateInfoProvider(this);
	}

	protected FrameDocument createDocumentImpl() {
		return new FrameDocument();
	}

	protected JMenuBar createMenuBar() {
		return AccessoryHelper.createMenuBar(menuBarFactory, withMenuDisable);
	}

	private void activeDocumentChanged(FrameDocument document) {
		updateWindowMenus();
		ApplicationActions.instance().setActionStates();
		if(document != null) {
			JOptionPane.setRootFrame(document);
		}
	}

	@Override
	public synchronized Document createDocument() {
		FrameDocument document = createDocumentImpl();
		documentInCreation = document;
		if(menuBarFactory != null) {
			JMenuBar menuBar = createMenuBar();
			document.setJMenuBar(menuBar);
		}

		if(statusBarFactory != null) {
			AccessoryHelper.createStatusBar(statusBarFactory, document.getContentPane());
		}

		if(toolBarFactory != null) {
			ToolBar toolBar = AccessoryHelper.createToolBar(toolBarFactory, document.getContentPane());
			if(toolBar != null) {
				document.setProperty(DOC_PROPERTY_TOOLBAR, toolBar);
			}
		}

		if(editToolBarFactory != null) {
			EditToolBar editToolBar = AccessoryHelper.createEditToolBar(editToolBarFactory, document.getContentPane());
			editToolBar.setDocument(document);
			document.setEditToolBar(editToolBar);
		}

		documentInCreation = null;
		document.setLocation(windowStacker.getNextPosition());
		document.setSize(ViewUtils.getDefaultWindowSize());

		document.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateWindowMenus();
			}

		});

		document.addDocumentListener(new DocumentAdapter() {

			@Override
			public void documentClosed(Document document) {
				removeDocument(document);
				lastDocumentClosed();
			}

			@Override
			public void documentActivated(Document document) {
				activeDocumentChanged((FrameDocument) document);
			}

			@Override
			public void documentDeactivated(Document document) {
				activeDocumentChanged(null);
			}

		});
		document.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				updateWindowMenus();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				updateWindowMenus();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				updateWindowMenus();
			}

		});
		documents.add(document);
		updateWindowMenus();

		new FileDragAndDrop(document) {

			@Override
			public void filesDropped(List files) {
				openFilesLater(files);
			}

		};

		als.fireDocumentCreated(document);
		return document;
	}

	protected void openFilesLater(final List files) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for(Iterator it = files.iterator(); it.hasNext();) {
					File file = (File) it.next();
					openFile(file.getAbsolutePath());
				}
			}

		});
	}

	protected void lastDocumentClosed() {
		if(documents.isEmpty()) {
			als.fireQuit();
		}
	}

	private synchronized void removeDocument(Document document) {
		documents.remove(document);
		windowMenuByDocument.remove(document);
		updateWindowMenus();
	}

	private void updateWindowMenus() {
		for(Iterator it = windowMenuByDocument.values().iterator(); it.hasNext();) {
			JMenu menu = (JMenu) it.next();
			windowMenuHelper.update(menu);
		}
	}

	public void about() {
		als.fireAbout();
	}

	public void openFile(String filename) {
		if(filename == null) {
			WindowMenuHelper.allToFront(documents, true);
		}

		if(filename != null) {
			als.fireOpenFile(filename);
		}
	}

	public void preferences() {
		als.firePreferences();
	}

	public void quit() {
		if(als.getListenerCount() > 0) {
			als.fireQuit();
		} else {
			System.exit(0);
		}
	}

	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				als.fireApplicationStarted();
			}

		});
	}

	@Override
	public void setTitle(String title) {
		// not supported here
	}

	@Override
	public void setIconImage(Image icon) {
		// not supported here
	}

	@Override
	public void addApplicationListener(ApplicationListener listener) {
		als.addApplicationListener(listener);
	}

	@Override
	public Frame getDialogOwner(Document document) {
		if(document == null) {
			return null;
		}
		return (FrameDocument) document;
	}

	@Override
	public void setMenuBarFactory(MenuBarFactory menuBarFactory) {
		this.menuBarFactory = menuBarFactory;
	}

	@Override
	public void setStatusBarFactory(StatusBarFactory statusBarFactory) {
		this.statusBarFactory = statusBarFactory;
	}

	@Override
	public void setToolBarFactory(ToolBarFactory toolBarFactory) {
		this.toolBarFactory = toolBarFactory;
	}

	@Override
	public void setEditToolBarFactory(EditToolBarFactory editToolBarFactory) {
		this.editToolBarFactory = editToolBarFactory;
	}

	@Override
	public synchronized List getDocuments() {
		return new ArrayList(documents);
	}

	protected boolean isDocumentInCreation() {
		return (documentInCreation != null);
	}

	@Override
	public JMenu getWindowMenu() {
		if(!isDocumentInCreation()) {
			throw new IllegalStateException("getWindowMenu() must only be called from MenuBarFactory");
		}

		JMenu menu = new JMenu(ApplicationActions.instance().windowMenu);
		windowMenuByDocument.put(documentInCreation, menu);
		windowMenuHelper.update(menu);
		return menu;
	}

	@Override
	public void addAboutMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {

			@Override
			public void run() {
				als.fireAbout();
			}

		});
	}

	@Override
	public void addPreferencesMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {

			@Override
			public void run() {
				als.firePreferences();
			}

		});
	}

	@Override
	public void addCloseMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {

			@Override
			@SuppressWarnings("CallToPrintStackTrace")
			public void run() {
				FrameDocument document = (FrameDocument) getActiveDocument();
				if(document != null) {
					try {
						document.close();
					} catch(DocumentCloseVetoException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	@Override
	public void addQuitMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {

			@Override
			public void run() {
				als.fireQuit();
			}

		});
	}

	@Override
	public Dimension getSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	@Override
	public Component getOptionPaneOwner() {
		return null;
	}

	@Override
	public Document getActiveDocument() {
		for(Iterator it = documents.iterator(); it.hasNext();) {
			FrameDocument document = (FrameDocument) it.next();
			if(document.isActive()) {
				return document;
			}
		}
		return null;
	}

	protected boolean isWindowMenu(JMenu menu) {
		return windowMenuByDocument.values().contains(menu);
	}

	@Override
	public void setCursor(Cursor cursor) {
		for(Iterator it = documents.iterator(); it.hasNext();) {
			FrameDocument f = (FrameDocument) it.next();
			f.setCursor(cursor);
		}
	}

	@Override
	public ToolBar getToolBar(Document document) {
		return (ToolBar) document.getProperty(DOC_PROPERTY_TOOLBAR);
	}

	@Override
	public void addActionStateInfos(ActionStateInfos stateInfos) {
		Document document = getActiveDocument();
		if(document != null) {
			document.addActionStateInfos(stateInfos);
		}
	}

	@Override
	public void closeAllDocuments() throws DocumentCloseVetoException {
		for(Iterator it = new ArrayList(documents).iterator(); it.hasNext();) {
			FrameDocument document = (FrameDocument) it.next();
			document.close();
		}
	}

}
