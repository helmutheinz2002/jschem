package org.heinz.eda.schem.ui.beans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.heinz.framework.crossplatform.dialog.color.SimpleColorChooser;
import org.heinz.framework.crossplatform.utils.Translator;

public class ColorBean extends PropertyBean {
	private JPanel colorField;
	private JButton changeButton;
	private JButton transparentButton;
	private Color color;
	private String label;
	
	public ColorBean() {
		changeButton = new JButton(Translator.translate("PICK_COLOR"));
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = SimpleColorChooser.showColorDialog(changeButton, label, color);
				if(c != null)
					setColor(c);
			}
		});
		
		transparentButton = new JButton(Translator.translate("TRANSPARENT"));
		transparentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setColor(null);
			}
		});
		
		int fs = changeButton.getFont().getSize() + 2;
		
		colorField = new JPanel();
		colorField.setPreferredSize(new Dimension(fs, fs));
		colorField.setMaximumSize(new Dimension(fs, fs));
		colorField.setOpaque(true);
		colorField.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		setLabel("COLOR");
	}
	
	public void setLabel(String label) {
		this.label = Translator.translate(label);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		colorField.setBackground(color);
	}
	
	public int addTo(JComponent parent, int startRow, boolean withLabel, boolean enableTransparent) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = DEFAULT_INSETS;
		c.gridy = startRow;
		
		c.gridx = 0;
		c.gridwidth = 1;
		if(withLabel) {
			parent.add(new JLabel(label), c);
			c.gridx++;
		}
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		parent.add(colorField, c);
		c.gridx++;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		parent.add(changeButton, c);
		
		if(enableTransparent) {
			c.gridx += c.gridwidth;
			c.fill = GridBagConstraints.NONE;
			parent.add(transparentButton, c);
		}
		
		c.gridy++;
		
		return c.gridy;
	}

}
