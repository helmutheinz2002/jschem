package org.heinz.framework.crossplatform.platforms.macos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JMenu;

import org.ben.macos.OSXApp;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.ApplicationListenerSupport;

public class MacOsApplicationSupport {
	private Method prefsEnableMethod;
	private Class osxAdapter;
	
	public MacOsApplicationSupport(final ApplicationListenerSupport als) {
		try {
			osxAdapter = ClassLoader.getSystemClassLoader().loadClass("org.ben.macos.OSXAdapter");
			prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs", new Class[] { boolean.class });
			setPrefsEnabled(true);
		} catch (Exception e) {
			System.err.println("Exception while loading the OSXAdapter:");
			e.printStackTrace();
		}
		
		ApplicationActions.instance().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == ApplicationActions.instance().preferencesItem)
					als.firePreferences();
			}
		});
		
		ApplicationActions.instance().preferencesItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("enabled"))
					setPrefsEnabled(ApplicationActions.instance().preferencesItem.isEnabled());
			}
		});
	}
	
	public static void registerOSXApp(OSXApp osXApp) {
		try {
			Class osxAdapter = ClassLoader.getSystemClassLoader().loadClass("org.ben.macos.OSXAdapter");
			
			Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", new Class[] { OSXApp.class });
			if (registerMethod != null) {
				Object[] a = { osXApp };
				registerMethod.invoke(osxAdapter, a);
			}
		} catch (Exception e) {
			System.err.println("Exception while loading the OSXAdapter:");
			e.printStackTrace();
		}
	}
	
	private void setPrefsEnabled(boolean b) {
		if (prefsEnableMethod == null)
			return;

		try {
			prefsEnableMethod.invoke(osxAdapter, new Object[] { new Boolean(b) });
		} catch(Exception e) {
			// impossible
		}
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

}
