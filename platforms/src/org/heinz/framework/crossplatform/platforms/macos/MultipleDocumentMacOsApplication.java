
package org.heinz.framework.crossplatform.platforms.macos;

import java.awt.Point;
import java.io.File;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.ben.macos.OSXApp;
import org.heinz.framework.crossplatform.platforms.basic.MultipleDocumentApplication;
import org.heinz.framework.crossplatform.utils.MenuHelper;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public class MultipleDocumentMacOsApplication extends MultipleDocumentApplication implements OSXApp {

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public MultipleDocumentMacOsApplication() {
		super(true, true);
		windowStacker.setStartPosition(new Point(10, 30));
		new MacOsApplicationSupport(als);
	}

	@Override
	public File selectFile(File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
		return MacOsFileSelection.selectFile(null, getOptionPaneOwner(), defaultFile, fileFilter, extEnsurer, save, title);
	}

	@Override
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = super.createMenuBar();
		MenuHelper.cleanMenus(menuBar, true, false, false);
		return menuBar;
	}

	@Override
	public void addAboutMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

	@Override
	public void addPreferencesMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

	@Override
	public void addQuitMenuItem(JMenu menu, Action action, boolean separator) {
		// Menu item is built-in on Mac OS X
	}

}
