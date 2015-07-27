package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

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
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.MenuHelper;
import org.heinz.framework.crossplatform.utils.WindowMenuHelper;
import org.heinz.framework.crossplatform.utils.WindowStacker;
import org.heinz.framework.utils.FileDragAndDrop;
import org.heinz.framework.utils.ViewUtils;

public abstract class MultipleDocumentApplication extends JFrame implements Application, ActionStateInfoProvider {
	private JDesktopPane desktop;
	protected WindowStacker windowStacker = new WindowStacker();
	protected ApplicationListenerSupport als = new ApplicationListenerSupport(this);
	private List documents = new ArrayList();
	private WindowMenuHelper windowMenuHelper;
	private JMenu windowMenu = new JMenu();
	final private boolean openDocsMaximized; 
	final private boolean withMenuDisable; 
	private MenuBarFactory menuBarFactory;
	private EditToolBarFactory editToolBarFactory;
	private ToolBar toolBar;
	
	public MultipleDocumentApplication(boolean openDocsMaximized, boolean withMenuDisable) {
		this.openDocsMaximized = openDocsMaximized;
		this.withMenuDisable = withMenuDisable;
		
		windowMenuHelper = new WindowMenuHelper(this, true, withMenuDisable);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		desktop = new JDesktopPane();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(desktop, c);
		
		setSize(ViewUtils.getDefaultWindowSize());
		setLocation(ViewUtils.getDefaultWindowPosition());
		setIconImage(IconLoader.instance().loadImage("data/icons/application/application.png"));
		
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				// Use invokeLater to avoid hanging splash screen
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						als.fireApplicationStarted();
					}
				});
			}

			public void windowClosing(WindowEvent e) {
				if(als.getListenerCount() == 0)
					System.exit(0);
				else
					als.fireQuit();
			}
		});
		
		new ApplicationUndoManager(this);
		ApplicationActions actions = new ApplicationActions();
		new ApplicationActionStateProvider(this, actions);
		actions.addStateInfoProvider(this);
		
		new FileDragAndDrop(this) {
			public void filesDropped(List files) {
				openFilesLater(files);
			}
		};

		windowMenu = new JMenu(ApplicationActions.instance().windowMenu);
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
	}
	
	protected void openFilesLater(final List files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for(Iterator it=files.iterator(); it.hasNext();) {
					File file = (File) it.next();
					openFile(file.getAbsolutePath());
				}
			}
		});
	}
	
	protected InternalDocument createDocumentImpl() {
		return new InternalDocument();
	}
	
	private void activeDocumentChanged(InternalDocument document) {
		updateWindowMenus();
		ApplicationActions.instance().setActionStates();
	}
	
	public void about() {
		als.fireAbout();
	}
	
	public void preferences() {
		als.firePreferences();
	}
	
	public void quit() {
		if(als.getListenerCount() > 0)
			als.fireQuit();
		else
			System.exit(0);
	}
	
	public Document createDocument() {
		final InternalDocument document = createDocumentImpl();
		desktop.add(document);
		Dimension d = desktop.getSize();
		document.setLocation(windowStacker.getNextPosition());
		document.setSize(ViewUtils.getDefaultWindowSize(d));
		
		documents.add(document);
		
		document.addDocumentListener(new DocumentAdapter() {
			public void documentActivated(Document document) {
				activeDocumentChanged((InternalDocument) document);
			}

			public void documentClosed(Document document) {
				removeDocument(document);
				ApplicationActions.instance().setActionStates();
				try {
					getTopDocument().setSelected();
				} catch(Exception ex) {
				}
			}

			public void documentDeactivated(Document document) {
				activeDocumentChanged(null);
			}
			
		});
		document.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameDeiconified(InternalFrameEvent e) {
				updateWindowMenus();
			}

			public void internalFrameIconified(InternalFrameEvent e) {
				updateWindowMenus();
			}
		});
		
		document.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(JInternalFrame.TITLE_PROPERTY))
					updateWindowMenus();
			}
		});
		
		if(openDocsMaximized) {
			try {
				document.setMaximum(true);
			} catch (PropertyVetoException e) {
			}
		}
		
		updateWindowMenus();
		
		if(editToolBarFactory != null) {
			final EditToolBar editToolBar = AccessoryHelper.createEditToolBar(editToolBarFactory, document.getContentPane());
			editToolBar.setDocument(document);
			document.setEditToolBar(editToolBar);
			document.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameActivated(InternalFrameEvent e) {
					editToolBar.fireCurrentTool();
				}
			});
		}
		
		als.fireDocumentCreated(document);
		return document;
	}

	private Document getTopDocument() {
		if(documents.size() == 0)
			return null;

		InternalDocument topDoc = null;
		
		try {
			int z = Integer.MAX_VALUE;
			for(Iterator it=documents.iterator(); it.hasNext();) {
				InternalDocument doc = (InternalDocument) it.next();
				int cz = desktop.getIndexOf(doc);
				if(!doc.isIcon() && (cz < z)) {
					z = cz;
					topDoc = doc;
				}
			}
		} catch(NoSuchMethodError ex) {
			topDoc = (InternalDocument) documents.get(0);
		}
		return topDoc;
	}
	
	protected void updateWindowMenus() {
		windowMenuHelper.update(windowMenu);
	}
	
	public void start() {
		setVisible(true);
	}

	public void addApplicationListener(ApplicationListener listener) {
		als.addApplicationListener(listener);
	}

	public Frame getDialogOwner(Document document) {
		return this;
	}

	public void openFile(String filename) {
		setVisible(true);
		setExtendedState(getExtendedState() & ~JFrame.ICONIFIED);
		
		if(filename != null)
			als.fireOpenFile(filename);
	}

	protected JMenuBar createMenuBar() {
		return AccessoryHelper.createMenuBar(menuBarFactory, withMenuDisable);
	}
	
	public void setMenuBarFactory(MenuBarFactory menuBarFactory) {
		this.menuBarFactory = menuBarFactory;
		if(menuBarFactory == null)
			return;
		
		JMenuBar menuBar = createMenuBar(); 
		setJMenuBar(menuBar);
	}

	public void setStatusBarFactory(StatusBarFactory statusBarFactory) {
		if(statusBarFactory != null)
			AccessoryHelper.createStatusBar(statusBarFactory, getContentPane());
	}

	public void setToolBarFactory(ToolBarFactory toolBarFactory) {
		if(toolBarFactory != null)
			toolBar = AccessoryHelper.createToolBar(toolBarFactory, getContentPane());
	}
	
	public void setEditToolBarFactory(EditToolBarFactory editToolBarFactory) {
		this.editToolBarFactory = editToolBarFactory;
	}
	
	public List getDocuments() {
		return documents;
	}

	private void removeDocument(Document document) {
		documents.remove(document);
		updateWindowMenus();
	}
	
	public JMenu getWindowMenu() {
		return windowMenu;
	}

	public void addAboutMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {
			public void run() {
				als.fireAbout();
			}
		});
	}

	public void addPreferencesMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {
			public void run() {
				als.firePreferences();
			}
		});
	}

	public void addQuitMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {
			public void run() {
				als.fireQuit();
			}
		});
	}

	public void addCloseMenuItem(JMenu menu, Action action, boolean separator) {
		MenuHelper.addMenuItem(menu, action, separator, new Runnable() {
			public void run() {
				InternalDocument document = (InternalDocument) getActiveDocument();
				if(document != null)
					try {
						document.close();
					} catch (DocumentCloseVetoException e) {
						e.printStackTrace();
					}
			}
		});
	}

	protected boolean isWindowMenu(JMenu menu) {
		return menu == windowMenu;
	}
	
	public Component getOptionPaneOwner() {
		return this;
	}

	public Document getActiveDocument() {
		for(Iterator it=documents.iterator(); it.hasNext();) {
			InternalDocument document = (InternalDocument) it.next();
			if(document.isSelected())
				return document;
		}
		return null;
	}
	
	public ToolBar getToolBar(Document document) {
		return toolBar;
	}
	
	public void addActionStateInfos(ActionStateInfos stateInfos) {
		Document document = getActiveDocument();
		if(document != null)
			document.addActionStateInfos(stateInfos);
	}
	
	public void closeAllDocuments() throws DocumentCloseVetoException {
		for(Iterator it=new ArrayList(documents).iterator(); it.hasNext();) {
			InternalDocument document = (InternalDocument) it.next();
			document.close();
		}
	}
}
