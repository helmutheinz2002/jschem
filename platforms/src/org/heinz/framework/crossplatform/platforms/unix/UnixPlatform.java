package org.heinz.framework.crossplatform.platforms.unix;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.UIManager;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationFactory;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.PlatformDefaults;
import org.heinz.framework.crossplatform.platforms.basic.AbstractPlatform;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationStarter;
import org.heinz.framework.crossplatform.utils.SplashScreenInfo;
import org.heinz.framework.utils.LaunchManager;
import org.heinz.framework.utils.LaunchManagerListener;
import org.heinz.framework.utils.cmdline.CommandLineParser;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

public class UnixPlatform extends AbstractPlatform implements ApplicationFactory {
	protected int portNr;
	private Application mainWindow;
	private LaunchManager launchManager;
	
	public UnixPlatform(String applicationName, String[] args, int portNr) {
		super(args, true);
		this.portNr = portNr;
		
		if(portNr >= 0) {
			launchManager = new LaunchManager(portNr);
			if(launchManager.delegateLaunch(args))
				System.exit(0);

			launchManager.addLaunchManagerListener(new LaunchManagerListener() {
				public void programLaunched(String[] args) {
					CommandLineParser parser = new CommandLineParser();
					parser.parseArguments(args);
					final List arguments = parser.getArguments();
					if(arguments.size() > 0)
						openFiles(arguments);
				}
			});
			try {
				launchManager.startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void openFiles(List filenames) {
		UnixApplication app = (UnixApplication) mainWindow;
		for(Iterator it=filenames.iterator(); it.hasNext();) {
			String filename = (String) it.next(); 
			app.openFile(filename);
		}
	}
	
	protected void setLookAndFeel() {
        try {
       		PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
       		UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch(Throwable ex) {
        	ex.printStackTrace();
        }
	}
	
	private Application createApplicationImpl(boolean mdi) {
		if(mdi) {
			boolean max = CrossPlatform.getPlatform().getPlatformDefaults().getBool(PlatformDefaults.OPEN_DOCS_MAXIMIZED);
			return new MultipleDocumentUnixApplication(max);
		}
		return new MultipleFrameUnixApplication();
	}
	
	public Application createApplication(boolean mdi) {
		if(mainWindow != null)
			throw new IllegalStateException("Application already created");

		setLookAndFeel();
		mainWindow = createApplicationImpl(mdi);
		return mainWindow;
	}

	public void startApplication(SplashScreenInfo info, boolean mdi, Runnable initialiser) {
		ApplicationStarter.startApplication(this, mdi, initialiser, info);
	}

	public Application getApplication() {
		return mainWindow;
	}

	public void registerFileType(String extension, String appName, String exePath) throws Exception {
		// does not do anything by default
	}

	public PlatformDefaults getPlatformDefaults() {
		PlatformDefaults d = new PlatformDefaults();
		d.put(PlatformDefaults.OPEN_DOCS_MAXIMIZED, Boolean.TRUE);
		d.put(PlatformDefaults.REVERSE_BUTTON_ORDER	, Boolean.FALSE);
		d.put(PlatformDefaults.BOTTOM_INSET, new Integer(5));
		d.put(PlatformDefaults.MENU_ICONS, Boolean.TRUE);
		d.put(PlatformDefaults.POPUP_MENU_ACCELERATORS, Boolean.FALSE);
		d.put(PlatformDefaults.POPUP_MENU_MNEMONICS, Boolean.FALSE);
		return d;
	}
	
	public String getName() {
		return "Unix";
	}
}
