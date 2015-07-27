package org.heinz.framework.crossplatform;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.heinz.framework.utils.OutputStreamWindow;

public class StatusBar extends JPanel {
	private int count;
	
	public StatusBar() {
		setLayout(new GridBagLayout());
		addButton();
	}
	
	public void setFixedSize(JComponent c) {
		c.setPreferredSize(c.getPreferredSize());
	}
	
	public JLabel addInfo(boolean fill) {
		Border innerBorder = BorderFactory.createEmptyBorder(2, 10, 2, 10); 
		Border outerBorder = BorderFactory.createEtchedBorder();
		Border infoBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
		
		JLabel l = new JLabel();
		l.setBorder(infoBorder);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = count++;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);
		if(fill) {
			c.weightx = 1.0;
			c.weighty = 1.0;
		}

		add(l, c);

		return l;
	}
	
	private void addButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 2, 1, 2);
		c.gridx = 100;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		
		add(OutputStreamWindow.instance().createButton(), c);
	}
}
