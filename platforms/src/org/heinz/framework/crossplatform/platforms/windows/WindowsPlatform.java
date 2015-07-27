package org.heinz.framework.crossplatform.platforms.windows;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.heinz.framework.crossplatform.PlatformDefaults;
import org.heinz.framework.crossplatform.platforms.unix.UnixPlatform;
import org.heinz.framework.utils.JarLibraryLoader;

import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class WindowsPlatform extends UnixPlatform {
	public WindowsPlatform(String applicationName, String[] args, int portNr) {
		super(applicationName, args, portNr);
	}
	
	protected void setLookAndFeel() {
        try {
    		String lnf = UIManager.getSystemLookAndFeelClassName();
    		UIManager.setLookAndFeel(lnf);
        } catch(Throwable ex) {
        	ex.printStackTrace();
        }
        
		UIManager.put("Button.margin", new Insets(1, 4, 1, 4));
		UIManager.put("ToggleButton.margin", new Insets(1, 4, 1, 4));
		Border orgBorder = UIManager.getBorder("PopupMenu.border");
		Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		UIManager.put("PopupMenu.border", BorderFactory.createCompoundBorder(orgBorder, emptyBorder));
		orgBorder = UIManager.getBorder("Menu.border");
		UIManager.put("Menu.border", BorderFactory.createCompoundBorder(orgBorder, emptyBorder));
	}
	
	public void registerFileType(String extension, String appName, String exePath) throws Exception {
		JarLibraryLoader.instance().loadLibrary("ICE_JNIRegistry");

		String regExePath = "\"" + exePath + "\" \"%1\"";
		String regExeKey = appName + "\\shell\\open\\command";
		
		String regAppKey = "." + extension;
		
		boolean isSetup = false;
		try {
			String curApp = getKey(regAppKey);
			String curExe = getKey(regExeKey);
			boolean appSetup = appName.equals(curApp);
			boolean exeSetup = regExePath.equals(curExe); 
			isSetup = appSetup && exeSetup;
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		if(!isSetup) {
			setKey("." + extension, appName);
	
			setKey(appName + "\\shell", "open");
			setKey(appName + "\\shell\\open\\command", regExePath);
		}
	}
	
	private static void setKey(String key, String value) throws RegistryException {
		String topLevelKey = "HKCR";
		RegistryKey topKey = Registry.getTopLevelKey(topLevelKey);
		RegistryKey localKey = topKey.createSubKey(key, "", RegistryKey.ACCESS_WRITE);
		RegStringValue val = new RegStringValue(localKey, "", value);
		localKey.setValue("", val);
		localKey.flushKey();
	}
	
	private String getKey(String key) throws RegistryException {
		RegistryKey localKey = Registry.openSubkey(Registry.HKEY_CLASSES_ROOT, key, RegistryKey.ACCESS_READ);
		return localKey.getStringValue("");
	}

	public PlatformDefaults getPlatformDefaults() {
		PlatformDefaults d = super.getPlatformDefaults();
		d.put(PlatformDefaults.OPEN_DOCS_MAXIMIZED, Boolean.FALSE);
		return d;
	}
	
	public String getName() {
		return "Windows";
	}
}
