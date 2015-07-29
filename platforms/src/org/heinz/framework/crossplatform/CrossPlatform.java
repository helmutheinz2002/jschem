
package org.heinz.framework.crossplatform;

import java.awt.Toolkit;

import org.heinz.framework.crossplatform.platforms.macos.MacOsPlatform;
import org.heinz.framework.crossplatform.platforms.macos.MacOsProperties;
import org.heinz.framework.crossplatform.platforms.unix.UnixPlatform;
import org.heinz.framework.crossplatform.platforms.windows.WindowsPlatform;
import org.heinz.framework.crossplatform.utils.Translator;

public abstract class CrossPlatform {

	private static Platform platform;

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public static Platform getPlatform(String applicationName, String[] args, int portNr) {
		if(platform == null) {
			String os = System.getProperty("os.name").toLowerCase();
			new Translator("Application");

			if(os.startsWith("mac os x")) {
				// Set the properties before starting AWT/Swing
				MacOsProperties.setProperties(applicationName);
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				platform = new MacOsPlatform(applicationName, args);
			} else if(os.startsWith("windows")) {
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				platform = new WindowsPlatform(applicationName, args, portNr);
			} else {
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				platform = new UnixPlatform(applicationName, args, portNr);
			}
		}

		return platform;
	}

	public static Platform getPlatform() {
		if(platform == null) {
			throw new IllegalStateException("No platform created yet");
		}

		return platform;
	}

	public static String getOsInfo() {
		String s = "";
		s += System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version");
		return s;
	}

	public static String getJavaInfo() {
		String s = "";
		s += System.getProperty("java.version") + " " + System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name");
		return s;
	}

	public static String getOsJavaInfo() {
		String s = "";
		s += getOsInfo() + "\n" + getJavaInfo();
		return s;
	}

}
