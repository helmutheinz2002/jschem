
package org.heinz.framework.examples;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationAdapter;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.EditToolBarFactory;
import org.heinz.framework.crossplatform.MenuBarFactory;
import org.heinz.framework.crossplatform.Platform;
import org.heinz.framework.crossplatform.PopupMenu;
import org.heinz.framework.crossplatform.StatusBar;
import org.heinz.framework.crossplatform.StatusBarFactory;
import org.heinz.framework.crossplatform.ToolBar;
import org.heinz.framework.crossplatform.ToolBarFactory;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfoProvider;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfos;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;
import org.heinz.framework.crossplatform.utils.SplashScreenInfo;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.AboutHelper;

public class TestApp extends ApplicationAdapter implements MenuBarFactory, StatusBarFactory, ToolBarFactory, EditToolBarFactory, ActionListener {

	private final Application application;

	private final JPopupMenu menu;

	private int docCount = 0;

	public static void main(String[] args) {
		//Locale.setDefault(new Locale("iw"));
		Platform platform = CrossPlatform.getPlatform("TestApp", args, 12032);
		SplashScreenInfo si = new SplashScreenInfo("/data/icons/splash/default.png", "TestApp", "A Simple Cross-Platform GUI Demo", "(C) 2007 Bernhard Walter");

		boolean mdi = platform.isDefaultMDI();
		//boolean mdi = false;
		platform.startApplication(si, mdi, new Runnable() {

			@Override
			@SuppressWarnings("ResultOfObjectAllocationIgnored")
			public void run() {
				new TestApp();
			}

		});
	}

	@SuppressWarnings("LeakingThisInConstructor")
	public TestApp() {
		application = CrossPlatform.getPlatform().getApplication();
		application.setTitle("TestApp");

		application.addApplicationListener(this);
		application.setMenuBarFactory(this);
		application.setStatusBarFactory(this);
		application.setToolBarFactory(this);
		application.setEditToolBarFactory(this);

		menu = new JPopupMenu();
		menu.add(ApplicationActions.instance().aboutItem);
		menu.add(ApplicationActions.instance().exitItem);
		menu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				PopupMenu.preparePopupMenu((JPopupMenu) e.getSource());
			}

		});

		application.start();
	}

	private Document newDocument(String title) {
		Document document = application.createDocument();
		document.setTitle(title + (docCount++));

		document.addDocumentListener(new DocumentAdapter() {

			@Override
			public void documentClosing(Document document, boolean inApplicationQuit) throws DocumentCloseVetoException {
				int res = JOptionPane.showConfirmDialog(document.getContainer(), "Close Document?");
				if(res == JOptionPane.OK_OPTION) {
					document.dispose();
				}
			}

		});

		final int n = docCount & 1;
		document.addStateInfoProvider(new ActionStateInfoProvider() {

			@Override
			public void addActionStateInfos(ActionStateInfos stateInfos) {
				stateInfos.put(ActionStateInfos.STATE_INFO_NUM_SELECTED, n);
			}

		});

		JButton l = new JButton("The Document");
		l.setOpaque(true);
		l.setBackground(Color.red);
		l.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				processPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				processPopupMenu(e);
			}

		});
		document.setDocumentPane(l);
		document.setSelected();
		return document;
	}

	private void processPopupMenu(MouseEvent e) {
		if(e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void about() {
		application.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		List libraries = new ArrayList();
		libraries.add(new AboutHelper.LibraryInfo("Lib A", "www.liba.com"));
		libraries.add(new AboutHelper.LibraryInfo("Lib B", "www.libb.com"));
		libraries.add(new AboutHelper.LibraryInfo("Lib C", "www.libc.com"));
		String text = AboutHelper.getAboutBoxText("TestApp 1.0", "11.11.2011", "Copright 2007 BW", "This is useless software", libraries);
		application.setCursor(Cursor.getDefaultCursor());
		JOptionPane.showMessageDialog(application.getOptionPaneOwner(), text, Translator.translate("ABOUT_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void openFile(String filename) {
		newDocument(filename);
	}

	@Override
	public void preferences() {
		JOptionPane.showMessageDialog(application.getOptionPaneOwner(), "Preferences");
	}

	@Override
	public void quit() {
		int res = JOptionPane.showConfirmDialog(application.getOptionPaneOwner(), "Exit?");
		if(res == JOptionPane.OK_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public void applicationStarted() {
		newDocument("Default");
	}

	@Override
	public JMenuBar createMenuBar() {
		final ApplicationActions ai = ApplicationActions.instance();
		ai.addActionListener(this);

		JMenuBar menuBar = new JMenuBar();
		JMenu m = new JMenu(ai.fileMenu);
		JMenuItem n = new JMenuItem(ai.newItem);
		m.add(n);
		application.addPreferencesMenuItem(m, ai.selectAllItem, false);
		application.addPreferencesMenuItem(m, ai.preferencesItem, false);
		m.add(ai.printItem);
		application.addCloseMenuItem(m, ai.closeItem, true);
		application.addQuitMenuItem(m, ai.exitItem, true);
		menuBar.add(m);

		JMenu t = new JMenu("Test");
		JMenuItem t1 = new JMenuItem("Enable Prefs");
		t1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ai.preferencesItem.setEnabled(true);
			}

		});
		JMenuItem t2 = new JMenuItem("Disable Prefs");
		t2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ai.preferencesItem.setEnabled(false);
			}

		});
		t.add(t1);
		t.add(t2);
		menuBar.add(t);

		JMenu wm = application.getWindowMenu();
		menuBar.add(wm);

		JMenu h = new JMenu(ai.helpMenu);
		application.addAboutMenuItem(h, ai.aboutItem, false);
		if(h.getItemCount() > 0) {
			menuBar.add(h);
		}
		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ApplicationActions.instance().newItem) {
			newDocument("Menu");
		}
	}

	@Override
	public StatusBar createStatusBar() {
		StatusBar s = new StatusBar();
		s.addInfo(false).setText("Teil 1");
		s.addInfo(false).setText("Teil 2");
		s.addInfo(true);
		return s;
	}

	@Override
	public ToolBar createToolBar() {
		ToolBar t = new ToolBar();
		t.add(ApplicationActions.instance().cutItem);
		t.add(ApplicationActions.instance().copyItem);
		t.add(ApplicationActions.instance().pasteItem);
		t.add(ApplicationActions.instance().preferencesItem);
		return t;
	}

	@Override
	public EditToolBar createEditToolBar() {
		EditToolBar t = new EditToolBar(null);
		t.add(ApplicationActions.instance().zoomInItem);
		t.add(ApplicationActions.instance().zoomOutItem);
		t.add(ApplicationActions.instance().zoomFitItem);
		return t;
	}

}
