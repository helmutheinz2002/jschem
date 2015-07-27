package org.heinz.framework.utils;

import java.io.File;

public class DefaultFileExtensionEnsurer implements FileExtensionEnsurer {
	private String extension;
	
	public DefaultFileExtensionEnsurer(String extension) {
		if(!extension.startsWith("."))
			extension = "." + extension;
		this.extension = extension;
	}
	
	public File ensureExtension(File f) {
		String fn = f.getName();
		if(fn.indexOf('.') < 0)
			return new File(f.getAbsoluteFile() + extension);
		return f;
	}

}
