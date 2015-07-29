
package org.heinz.framework.crossplatform.platforms.unix;

import java.io.File;

import org.heinz.framework.crossplatform.platforms.basic.MultipleDocumentApplication;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public class MultipleDocumentUnixApplication extends MultipleDocumentApplication implements UnixApplication {

	public MultipleDocumentUnixApplication(boolean openDocsMaximized) {
		super(openDocsMaximized, false);
	}

	@Override
	public File selectFile(File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
		return UnixFileSelection.selectFile(getDialogOwner(null), defaultFile, fileFilter, extEnsurer, save, title);
	}

}
