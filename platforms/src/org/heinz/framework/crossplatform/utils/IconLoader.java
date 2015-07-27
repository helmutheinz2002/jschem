package org.heinz.framework.crossplatform.utils;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;

import org.heinz.framework.crossplatform.Application;

public class IconLoader {
	public static final String DEFAULT_ICON_PATH = Application.DEFAULT_DATA_PATH + "icons/";
	
    private static IconLoader instance;
    
    private ImageIcon loadIconWithFullPath(String resource) {
        try {
        	if(resource.indexOf('.') < 0)
        			resource += ".png";
            InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
            int ch;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
            while((ch = is.read()) >= 0)
                bos.write(ch);
            
            is.close();
            bos.close();
            return new ImageIcon(bos.toByteArray(), resource);
        } catch(Exception e) {
        }
        return null;
    }
    
    public ImageIcon loadIcon(String resource) {
        ImageIcon icon = loadIconWithFullPath(DEFAULT_ICON_PATH + resource);
        if(icon == null)
        	icon = loadIconWithFullPath(resource);
        return icon;
    }
    
    public Image loadImage(String resource) {
    	ImageIcon icon = loadIcon(resource);
    	if(icon == null)
    		return null;
    	return icon.getImage();
    }
    
    public static IconLoader instance() {
        if(instance == null)
            instance = new IconLoader();
        return instance;
    }
}
