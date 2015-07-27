package org.heinz.framework.crossplatform.platforms.unix;

import java.awt.Point;
import java.io.File;

import org.heinz.framework.crossplatform.platforms.basic.MultipleFrameApplication;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;
import org.heinz.framework.utils.ViewUtils;

public class MultipleFrameUnixApplication extends MultipleFrameApplication implements UnixApplication {
	public MultipleFrameUnixApplication() {
		super(false);
		
		Point defaultPosition = ViewUtils.getDefaultWindowPosition();
		windowStacker.setStartPosition(defaultPosition);
	}
	
	public File selectFile(File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
		return UnixFileSelection.selectFile(getDialogOwner(null), defaultFile, fileFilter, extEnsurer, save, title);
	}
}
