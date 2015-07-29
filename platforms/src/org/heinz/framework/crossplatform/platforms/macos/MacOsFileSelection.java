
package org.heinz.framework.crossplatform.platforms.macos;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import org.heinz.framework.crossplatform.platforms.basic.AbstractFileSelection;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public abstract class MacOsFileSelection extends AbstractFileSelection {

	public static File selectFile(Frame owner, Component optionPaneOwner, File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
		FileDialog dialog = new FileDialog(owner, title, save ? FileDialog.SAVE : FileDialog.LOAD);
		dialog.setFilenameFilter(fileFilter);
		if(defaultFile != null) {
			dialog.setDirectory(defaultFile.getParentFile().getAbsolutePath());
			dialog.setFile(defaultFile.getName());
		}
		dialog.setVisible(true);
		if(dialog.getFile() == null) {
			return null;
		}

		File selection = new File(dialog.getDirectory() + File.separator + dialog.getFile());
		if(extEnsurer != null) {
			File of = selection;
			selection = extEnsurer.ensureExtension(selection);
			if(!of.getAbsolutePath().equals(selection.getAbsolutePath())) {
				if(selection.exists() && !confirmOverwrite(optionPaneOwner, selection)) {
					return null;
				}
			}
		}
		return selection;
	}

}
