
package org.heinz.framework.utils;

import java.io.File;

public class DefaultFileExtensionEnsurer implements FileExtensionEnsurer {

	private final String extension;

	public DefaultFileExtensionEnsurer(String extension) {
		if(!extension.startsWith(".")) {
			extension = "." + extension;
		}
		this.extension = extension;
	}

	@Override
	public File ensureExtension(File f) {
		String fn = f.getName();
		if(fn.indexOf('.') < 0) {
			return new File(f.getAbsoluteFile() + extension);
		}
		return f;
	}

}
