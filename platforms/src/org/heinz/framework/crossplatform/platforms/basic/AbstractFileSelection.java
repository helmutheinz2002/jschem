
package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

import org.heinz.framework.crossplatform.utils.Translator;

public class AbstractFileSelection {

	public static boolean confirmOverwrite(Component owner, File f) {
		return confirmOverwrite(owner, f, "FILE_EXISTS_CONFIRMATION", "FILE_EXISTS_TITLE");
	}

	public static boolean confirmOverwrite(Component owner, File f, String message, String title) {
		int r = JOptionPane.showConfirmDialog(owner, f.getAbsolutePath() + "\n" + Translator.translate(message),
				Translator.translate(title), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return r == JOptionPane.YES_OPTION;
	}

}
