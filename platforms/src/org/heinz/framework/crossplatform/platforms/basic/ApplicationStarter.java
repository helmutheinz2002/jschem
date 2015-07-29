
package org.heinz.framework.crossplatform.platforms.basic;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationFactory;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.utils.SplashScreen;
import org.heinz.framework.crossplatform.utils.SplashScreenInfo;
import org.heinz.framework.utils.OutputStreamWindow;

public class ApplicationStarter {

	public static void startApplication(final ApplicationFactory appFactory, final boolean mdi, final Runnable initialiser, SplashScreenInfo info) {
		if(info == null) {
			appFactory.createApplication(mdi);
			initialiser.run();
		} else {
			final SplashScreen splash = new SplashScreen(info);
			splash.startSplash(new Runnable() {

				@SuppressWarnings({"ResultOfObjectAllocationIgnored", "SleepWhileInLoop"})
				public void run() {
					Application app = appFactory.createApplication(mdi);
					if(app instanceof MultipleDocumentApplication) {
						splash.setMainWindow((MultipleDocumentApplication) app);
					}

					new OutputStreamWindow("Console", CrossPlatform.getOsJavaInfo());
					initialiser.run();

					if(app instanceof MultipleFrameApplication) {
						if(app.getDialogOwner(null) != null) {
							splash.setMainWindow(app.getDialogOwner(null));
						} else {
							while(app.getDocuments().isEmpty()) {
								try {
									Thread.sleep(200);
								} catch(InterruptedException e) {
								}
							}
							splash.setMainWindow((FrameDocument) app.getDocuments().get(0));
						}
					}
				}

			});
		}
	}

}
