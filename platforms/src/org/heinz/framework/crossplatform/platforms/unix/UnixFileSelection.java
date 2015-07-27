package org.heinz.framework.crossplatform.platforms.unix;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.heinz.framework.crossplatform.platforms.basic.AbstractFileSelection;
import org.heinz.framework.crossplatform.utils.UniversalFileFilter;
import org.heinz.framework.utils.FileExtensionEnsurer;

public abstract class UnixFileSelection extends AbstractFileSelection {
	public static File selectFile(Component owner, File defaultFile, UniversalFileFilter fileFilter, FileExtensionEnsurer extEnsurer, boolean save, String title) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
    	fc.setFileFilter(fileFilter);
    	
    	if(defaultFile != null)
    		fc.setSelectedFile(defaultFile);
    	if(save) {
    		if(fc.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
    			File f = fc.getSelectedFile();
    			if(extEnsurer != null)
    				f = extEnsurer.ensureExtension(f);
    			
    			if(f.exists() && !confirmOverwrite(owner, f))
    					return null;

    			return f;
    		}
    	} else {
    		if(fc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION)
    			return fc.getSelectedFile();
    	}
    	
    	return null;
	}
}
