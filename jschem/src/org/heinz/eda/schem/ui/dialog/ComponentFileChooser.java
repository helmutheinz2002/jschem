package org.heinz.eda.schem.ui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.heinz.eda.schem.model.Library;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.util.ComponentFileFilter;

public class ComponentFileChooser extends JFileChooser implements PropertyChangeListener {
	private ComponentPreviewPanel previewPanel;
	private AbstractComponent component;
	
	public ComponentFileChooser(String libDir, boolean save) {
		super();
		
		setFileHidingEnabled(false);
		setCurrentDirectory(new File(libDir));
		
		previewPanel = new ComponentPreviewPanel();
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		p.add(previewPanel, c);
		setAccessory(p);
		
		setFileFilter(ComponentFileFilter.instance(save));
		addPropertyChangeListener(this);
		
		setPreferredSize(new Dimension(800, 600));
	}
	
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
        	previewPanel.showComponent(null);
        	component = null;
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            File file = (File) e.getNewValue();
        	component = null;
        	
            if((file != null) && (file.isFile())) {
            	AbstractComponent c = Library.loadComponent(file);
            	previewPanel.showComponent(c);
            	component = c;
            }
        }
    }

    public AbstractComponent getSelectedComponent() {
    	return component;
    }
}
