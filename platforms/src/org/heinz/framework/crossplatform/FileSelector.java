package org.heinz.framework.crossplatform;

import java.io.File;

import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public interface FileSelector {
	File selectFile(File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title);
}
