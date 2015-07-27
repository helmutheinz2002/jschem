package org.heinz.eda.schem.ui.dialog.library;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JLabel;

import org.heinz.eda.schem.model.Library;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.ui.dialog.ComponentPreviewPanel;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;

public class ComponentDiffPanel extends StandardDialogPanel {
	private ComponentPreviewPanel orgCompPanel;
	private ComponentPreviewPanel newCompPanel;
	
	public ComponentDiffPanel() {
		super(Translator.translate("COMPARE_COMPONENTS"));
		init();
	}

	public void compare(File orgCompFile, InputStream newCompStream) {
		try {
			AbstractComponent orgComp = Library.loadComponent(new FileInputStream(orgCompFile));
			orgCompPanel.showComponent(orgComp);
		} catch (FileNotFoundException e) {
		}
		
		AbstractComponent newComp = Library.loadComponent(newCompStream);
		newCompPanel.showComponent(newComp);
	}
	
	private void init() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);
		
		JLabel l1 = new JLabel(Translator.translate("ORIGINAL_LIBRARY_COMPONENT"));
		JLabel l2 = new JLabel(Translator.translate("NEW_LIBRARY_COMPONENT"));
		
		add(l1, c);
		c.gridx ++;
		add(l2, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		orgCompPanel = new ComponentPreviewPanel();
		newCompPanel = new ComponentPreviewPanel();
		
		add(orgCompPanel, c);
		c.gridx ++;
		add(newCompPanel, c);
	}

	public String check() {
		return null;
	}

	public void ok() {
	}
}
