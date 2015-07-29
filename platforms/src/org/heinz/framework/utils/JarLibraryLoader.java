
package org.heinz.framework.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JarLibraryLoader {

	private static JarLibraryLoader instance;

	public static JarLibraryLoader instance() {
		if(instance == null) {
			instance = new JarLibraryLoader();
		}
		return instance;
	}

	public void loadLibrary(String lib) throws IOException {
		String libFileName = System.mapLibraryName(lib);
		String libFileFileName = "" + System.currentTimeMillis() + "_" + libFileName;
		File libFile = new File(System.getProperty("java.io.tmpdir") + File.separator + libFileFileName);

		InputStream is = getClass().getResourceAsStream("/" + libFileName);
		try (FileOutputStream out = new FileOutputStream(libFile)) {
			int read;
			byte[] data = new byte[1024];
			while((read = is.read(data, 0, 1024)) != -1) {
				out.write(data, 0, read);
			}
		}

		System.load(libFile.getAbsolutePath());
	}

}
