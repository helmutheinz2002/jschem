/*
 * ExtandableFileFilter.java
 *
 * Created on February 21, 2005, 12:18 PM
 */

package org.heinz.framework.crossplatform.utils;

import java.io.File;


public class CustomFileFilter extends UniversalFileFilter {
	private String[] extensions;
	private String description;
	private Object formatObject;
	
	public CustomFileFilter(String[] extensions, String description) {
		this(extensions, description, null);
	}
	
	public CustomFileFilter(String[] extensions, String description, Object formatObject) {
		super();
		this.extensions = extensions;
		this.description = description;
		this.formatObject = formatObject;
		
		String s = "";
		for(int i=0; i<extensions.length; i++) {
			if(i > 0)
				s += ",";
			s += extensions[i];
		}
		
		this.description += " (" + s + ")";
	}
	
	public boolean hasExtension(String fileName) {
		String extension = getExtension(new File(fileName));
		if (extension != null) {
			for(int i=0; i<extensions.length; i++)
				if(extension.equals(extensions[i]))
					return true;
		}
		return false;
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		
		return hasExtension(f.getAbsolutePath());
	}
	
	public String getDescription() {
		return description;
	}
	
	public Object getFormatObject() {
		return formatObject;
	}
	
	private static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		
		if (i > 0 &&  i < s.length() - 1)
			ext = s.substring(i+1).toLowerCase();

		return ext;
	}

	public boolean accept(File dir, String name) {
		return hasExtension(name);
	}
}
