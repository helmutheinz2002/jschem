package org.heinz.framework.crossplatform.platforms.macos;

public class MacOsProperties {
	public static void setProperties(String applicationName) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.antialiasing", "on");
		System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
		System.setProperty("com.apple.mrj.application.live-resize", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);
	}
}
