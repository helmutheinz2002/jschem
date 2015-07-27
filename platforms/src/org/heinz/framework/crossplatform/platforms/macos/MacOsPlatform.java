package org.heinz.framework.crossplatform.platforms.macos;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.ben.macos.OSXApp;
import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationAdapter;
import org.heinz.framework.crossplatform.ApplicationFactory;
import org.heinz.framework.crossplatform.PlatformDefaults;
import org.heinz.framework.crossplatform.platforms.basic.AbstractPlatform;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationStarter;
import org.heinz.framework.crossplatform.utils.SplashScreenInfo;

public class MacOsPlatform extends AbstractPlatform implements ApplicationFactory {
	private Application application;
	private String initialFilename;
	
	public MacOsPlatform(String applicationName, String[] args) {
		super(args, false);
		
		MacOsApplicationSupport.registerOSXApp(new OSXApp() {
			public void about() {
				((OSXApp) application).about();
			}

			public void openFile(String filename) {
				initialFilename = filename;
				((OSXApp) application).openFile(initialFilename);
			}

			public void preferences() {
				((OSXApp) application).preferences();
			}

			public void quit() {
				((OSXApp) application).quit();
			}
		});
	}

	public Application createApplication(boolean mdi) {
		if(application != null)
			throw new IllegalStateException("Application already created");
		
		try {
			String lnf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lnf);
		} catch(Throwable ex) {
			ex.printStackTrace();
		}

		Border orgBorder = UIManager.getBorder("PopupMenu.border");
		Border emptyBorder = BorderFactory.createEmptyBorder(5, 0, 5, 0);
		UIManager.put("PopupMenu.border", BorderFactory.createCompoundBorder(orgBorder, emptyBorder));
//		orgBorder = UIManager.getBorder("Menu.border");
//		UIManager.put("Menu.border", BorderFactory.createCompoundBorder(orgBorder, emptyBorder));

		if(mdi)
			application = new MultipleDocumentMacOsApplication();
		else
			application = new MultipleFrameMacOsApplication();
		
		application.addApplicationListener(new ApplicationAdapter() {
			public void applicationStarted() {
				if(initialFilename != null)
					((OSXApp) application).openFile(initialFilename);
			}
		});
		return application;
	}

	public void startApplication(SplashScreenInfo info, boolean mdi, final Runnable initialiser) {
		ApplicationStarter.startApplication(this, mdi, initialiser, info);
	}

	public Application getApplication() {
		return application;
	}

	public void registerFileType(String extension, String appName, String exePath) throws Exception {
		// registration is performed by Finder
	}

	public PlatformDefaults getPlatformDefaults() {
		PlatformDefaults d = new PlatformDefaults();
		d.put(PlatformDefaults.OPEN_DOCS_MAXIMIZED, Boolean.TRUE);
		d.put(PlatformDefaults.REVERSE_BUTTON_ORDER	, Boolean.TRUE);
		d.put(PlatformDefaults.BOTTOM_INSET, new Integer(25));
		d.put(PlatformDefaults.MENU_ICONS, Boolean.FALSE);
		d.put(PlatformDefaults.POPUP_MENU_ACCELERATORS, Boolean.FALSE);
		d.put(PlatformDefaults.POPUP_MENU_MNEMONICS, Boolean.FALSE);
		return d;
	}
	
	public String getName() {
		return "Mac";
	}
}
