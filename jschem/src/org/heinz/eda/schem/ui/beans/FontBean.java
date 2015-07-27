package org.heinz.eda.schem.ui.beans;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.heinz.eda.schem.model.components.Text;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class FontBean extends PropertyBean {
	private final JComboBox fontTypeBox;
	private final UnitCombobox fontSizeBox;
	public final JToggleButton boldButton;
	public final JToggleButton italicButton;
	public final JTextField textField;
	public final JPanel fontSizePanel;
	public final JPanel fontTypePanel;
	
	public FontBean() {
		boldButton = new JToggleButton(IconLoader.instance().loadIcon("menu/text_bold.png"));
		boldButton.setToolTipText(Translator.translate("BOLD"));
		italicButton = new JToggleButton(IconLoader.instance().loadIcon("menu/text_italic.png"));
		italicButton.setToolTipText(Translator.translate("ITALIC"));

		textField = new JTextField();
		
		fontTypeBox = new JComboBox();
		fontTypeBox.addItem("Monospaced");
		fontTypeBox.addItem("SansSerif");
		fontTypeBox.addItem("Serif");
		
		fontSizeBox = new UnitCombobox();
		for(int i=0; i<Text.FONT_SIZES.length; i++)
			fontSizeBox.addUnitsItem(Text.FONT_SIZES[i]);
		
		fontSizePanel = createPanel(fontSizeBox);
		fontTypePanel = createPanel(fontTypeBox);
	}
	
	private JPanel createPanel(JComponent comp) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 0);
		p.add(comp, c);
		Dimension cd = comp.getPreferredSize();
		Dimension pd = p.getPreferredSize();
		pd.width = cd.width + 5;
		p.setMaximumSize(pd);
		return p;
	}
	
    public List getGuiElements() {
        return getGuiElements(true);
    }
    
    public List getGuiElements(boolean withTextField) {
        List list = new ArrayList();
        list.add(boldButton);
        list.add(italicButton);
        list.add(fontTypePanel);
        list.add(fontSizePanel);
        if(withTextField)
            list.add(textField);
        return list;
    }
    
    public int addTo(JComponent parent, int startRow, boolean withTextField) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = DEFAULT_INSETS;
        c.gridy = startRow;
        
        c.gridx = 0;
        
        if(withTextField) {
            c.gridwidth = 1;
            c.fill = GridBagConstraints.BOTH;
            parent.add(new JLabel(Translator.translate("FONT_TEXT")), c);
            c.gridx++;
            c.gridwidth = 6;
            c.weightx = 1.0;
            parent.add(textField, c);
            c.weightx = 0;
            c.gridy++;
            c.gridx = 0;
        }
        
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        parent.add(new JLabel(Translator.translate("FONT_NAME")), c);
        c.gridx++;
        c.gridwidth = 4;
        parent.add(fontTypeBox, c);
        c.gridy++;
        
        c.gridx = 0;
        c.gridwidth = 1;
        parent.add(new JLabel(Translator.translate("FONT_SIZE")), c);
        c.gridx++;
        c.gridwidth = 4;
        parent.add(fontSizePanel, c);
        c.gridy++;
        
        c.gridx = 0;
        c.gridwidth = 1;
        parent.add(new JLabel(Translator.translate("FONT_STYLE")), c);
        c.gridx++;
        c.fill = GridBagConstraints.NONE;
        parent.add(boldButton, c);
        c.gridx++;
        parent.add(italicButton, c);
        c.gridy++;
        
        return c.gridy;
    }
    
	public String getFontName() {
		return (String) fontTypeBox.getSelectedItem();
	}
	
	public void setFontName(String fontName) {
		fontTypeBox.setSelectedItem(fontName);
	}
	
	public int getFontSize() {
		return fontSizeBox.getSelectedUnits();
	}
	
	public void setFontSize(int size) {
		fontSizeBox.setSelectedUnits(size);
	}
	
	public int getFontStyle() {
		int style = Font.PLAIN;
		if(boldButton.isSelected())
			style = style | Font.BOLD;
		if(italicButton.isSelected())
			style = style | Font.ITALIC;
		return style;
	}
	
	public void setFontStyle(int style) {
		boldButton.setSelected((style & Font.BOLD) == Font.BOLD);
		italicButton.setSelected((style & Font.ITALIC) == Font.ITALIC);
	}
	
	public String getText() {
		return textField.getText();
	}
	
	public void setText(String text) {
		textField.setText(text);
	}
	
	//-----------------------------------------------
	
	class FontSize {
		public final int size;
		
		public FontSize(int size) {
			this.size = size;
		}
		
		public String toString() {
			return "" + (size/100.0) + "mm";
		}
		
		public boolean equals(Object o) {
			FontSize fs = (FontSize) o;
			return fs.size == size;
		}
	}
}
