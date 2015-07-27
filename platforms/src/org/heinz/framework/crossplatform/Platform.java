package org.heinz.framework.crossplatform;

import org.heinz.framework.crossplatform.utils.SplashScreenInfo;

public interface Platform {
	boolean isDefaultMDI();
	void startApplication(SplashScreenInfo info, boolean mdi, Runnable initialiser);
	Application getApplication();
	void registerFileType(String extension, String appName, String exePath) throws Exception;
	PlatformDefaults getPlatformDefaults();
	String getName();
}
