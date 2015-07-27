package org.heinz.eda.schem.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.expresssch.ExpressImport;
import org.heinz.eda.schem.model.xml.XmlComponentReader;
import org.heinz.eda.schem.model.xml.XmlComponentWriter;

public class Library {
	private static final String LIBRARY_DIR = "library";
	
	public static final int UPDATE_CHECK_FILE_NEW = 0;
	public static final int UPDATE_CHECK_FILE_EXISTS_UNCHANGED = 1;
	public static final int UPDATE_CHECK_FILE_EXISTS_CHANGED = 2;
	public static final int UPDATE_ACTION_SKIP = 3;
	public static final int UPDATE_ACTION_OVERWRITE = 4;
	public static final int UPDATE_ACTION_RENAME = 5;
	
	private String libDir;

	public Library(String workDir) {
		libDir = workDir + File.separator + LIBRARY_DIR;
	}
	
	public String getLibraryDir() {
		return libDir;
	}

	public List updateLibrary(List updateActions) {
		for(Iterator it= updateActions.iterator(); it.hasNext();) {
			UpdateAction action = (UpdateAction) it.next();
			
			if(action.action == UPDATE_ACTION_SKIP) {
				it.remove();
				continue;
			}
			
			File dir = action.file.getParentFile();
			if(!dir.exists()) {
				if(!dir.mkdirs()) {
					action.error = "CANNOT_CREATE_LIBRARY_DIRECTORY";
					action.errorData = dir.getAbsolutePath();
					continue;
				}
			}
			
			if(action.action == UPDATE_ACTION_RENAME) {
				String fn = action.file.getName();
				File newFile = null;
				for(int i=0; true; i++) {
					int idx = fn.lastIndexOf(".");
					String ext = fn.substring(idx + 1);
					String base = fn.substring(0, idx);
					
					String newName = base + "_" + i + "." + ext;
					newFile = new File(dir, newName);
					if(!newFile.exists())
						break;
				}
				
				if(!action.file.renameTo(newFile)) {
					action.error = "CANNOT_RENAME_LIBRARY_FILE";
					action.errorData = action.file.getAbsolutePath();
					continue;
				}
			}
				
			try {
				FileOutputStream out = new FileOutputStream(action.file);
				out.write(action.data);
				out.close();
			} catch (Exception e) {
				action.error = "CANNOT_WRITE_LIBRARY_FILE";
				action.errorData = action.file.getAbsolutePath();
				continue;
			}
			it.remove();
		}
	
		return updateActions;
	}
	
	public List checkLibrary() {
		List result = new ArrayList();
		
		InputStream is = getClass().getResourceAsStream("/library.zip");
		if(is == null)
			return result;
		
		try {
			ZipInputStream zis = new ZipInputStream(is);

			int read;
			byte[] data = new byte[1024];
			while (true) {
				ZipEntry entry = zis.getNextEntry();
				if (entry == null)
					break;

				if(!entry.isDirectory()) {
					File compFile = new File(libDir + File.separator + entry.getName());
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					while ((read = zis.read(data, 0, 1024)) != -1)
						out.write(data, 0, read);
					out.close();
					byte[] bytes = out.toByteArray();
					
					int res = UPDATE_CHECK_FILE_NEW;
					if(compFile.exists()) {
						long fileSize = compFile.length();
						long compSize = out.size();
						
						if(fileSize != compSize)
							res = UPDATE_CHECK_FILE_EXISTS_CHANGED;
						else {
							ByteArrayOutputStream compBytes = new ByteArrayOutputStream();
							FileInputStream fin = new FileInputStream(compFile);
							while ((read = fin.read(data, 0, 1024)) != -1)
								compBytes.write(data, 0, read);
							fin.close();
							out.close();
							
							byte[] cBytes = compBytes.toByteArray();
							res = UPDATE_CHECK_FILE_EXISTS_UNCHANGED;
							
							for(int i=0; i<cBytes.length; i++)
								if(cBytes[i] != bytes[i]) {
									res = UPDATE_CHECK_FILE_EXISTS_CHANGED;
									break;
								}
						}
					}
					result.add(new UpdateAction(res, compFile, entry.getName(), bytes));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			is.close();
		} catch (IOException e) {
		}
		
		return result;
	}

    public static AbstractComponent loadComponent(File file) {
		try {
			return loadComponent(new FileInputStream(file));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    	return null;
    }

    public static AbstractComponent loadComponent(InputStream fis) {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	try {
    		int ch;
    		while((ch = fis.read()) >= 0)
    			bos.write(ch);
    	} catch(IOException iex) {
    		return null;
    	}
    	
		try {
	    	ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			XmlComponentReader reader = new XmlComponentReader(true);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			reader.reset();
			p.parse(bis, reader.getHandler());
			List l = reader.getComponents();
			AbstractComponent ac = (AbstractComponent) l.get(0);
			return ac;
		} catch(Exception ex) {
	    	ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			try {
				return ExpressImport.importComponent(bis);
			} catch(Exception ex2) {
			}
		}
    	return null;
    }

    public static void saveComponent(AbstractComponent c, File file) throws Exception {
    	XmlComponentWriter writer = new XmlComponentWriter();
		String xml = writer.toXml(c);

		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fos);
		pw.print(xml);
		pw.close();
		fos.close();
	}
    
    public static String getUpdateText(int result) {
    	switch(result) {
    		case UPDATE_CHECK_FILE_NEW:
    			return "UPDATE_CHECK_FILE_NEW";
    		case UPDATE_CHECK_FILE_EXISTS_UNCHANGED:
    			return "UPDATE_CHECK_FILE_EXISTS_UNCHANGED";
    		case UPDATE_CHECK_FILE_EXISTS_CHANGED:
				return "UPDATE_CHECK_FILE_EXISTS_CHANGED";
    		case UPDATE_ACTION_SKIP:
				return "UPDATE_ACTION_IGNORE";
    		case UPDATE_ACTION_OVERWRITE:
				return "UPDATE_ACTION_OVERWRITE";
    		case UPDATE_ACTION_RENAME:
				return "UPDATE_ACTION_MOVE";
    		default:
    			break;
    	}
    	return "ERROR";
    }

	public static boolean hasPendingUpdates(List updateActions) {
		for(Iterator it=updateActions.iterator(); it.hasNext();) {
			UpdateAction action = (UpdateAction) it.next();
			if(action.action != UPDATE_ACTION_SKIP)
				return true;
		}
		return false;
	}
    
	public static boolean isAllNew(List updateActions) {
		for(Iterator it=updateActions.iterator(); it.hasNext();) {
			UpdateAction action = (UpdateAction) it.next();
			if(action.result != UPDATE_CHECK_FILE_NEW)
				return false;
		}
		return true;
	}
    
    //------------------------------------------------------
    
    public class UpdateAction {
    	public final int result;
    	public final File file;
    	public final String fileName;
    	public final byte[] data;
    	public int action;
    	public int defaultAction;
    	public String error;
    	public String errorData;
    	
		public UpdateAction(int result, File file, String fileName, byte[] data) {
			this.result = result;
			this.file = file;
			this.fileName = fileName;
			this.data = data;
			action = UPDATE_ACTION_SKIP;
			
			if(result == UPDATE_CHECK_FILE_EXISTS_CHANGED)
				action = UPDATE_ACTION_RENAME;
			else if(result == UPDATE_CHECK_FILE_NEW)
				action = UPDATE_ACTION_OVERWRITE;
			
			defaultAction = action;
		}
    }
}
