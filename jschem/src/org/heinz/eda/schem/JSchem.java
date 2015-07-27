package org.heinz.eda.schem;

import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.Platform;
import org.heinz.framework.crossplatform.utils.SplashScreenInfo;

public class JSchem {
	public static void main(final String[] args) {
		//java.util.Locale.setDefault(new java.util.Locale("iw"));
		
		final Platform platform = CrossPlatform.getPlatform(SchemConstants.PROGRAM_NAME, args, SchemConstants.LAUNCH_MGR_PORT_NR);
		SplashScreenInfo si = new SplashScreenInfo("/data/splash/splash.png", SchemConstants.PROGRAM_NAME, "A Free Cross Platform Schematic Editor", "\u00A9 2007 Bernhard Walter");

		boolean mdi = platform.isDefaultMDI();
		//boolean mdi = false;
		platform.startApplication(si, mdi, new Runnable() {
			public void run() {
				new SchemApplication(args);
			}
		});
	}
}
