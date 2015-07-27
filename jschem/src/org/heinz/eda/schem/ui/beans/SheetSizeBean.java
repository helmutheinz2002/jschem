package org.heinz.eda.schem.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.heinz.eda.schem.model.SheetSize;
import org.heinz.eda.schem.util.UnitConverter;
import org.heinz.eda.schem.util.UnitDocumentFilter;
import org.heinz.framework.crossplatform.utils.Translator;

public class SheetSizeBean extends PropertyBean {
	private JComboBox sheetSizes;
	private JTextField widthField;
	private JTextField heightField;
	private String label;
	
	public SheetSizeBean() {
		sheetSizes = new JComboBox();
		for(int i=0; i<SheetSize.SIZES.length; i++)
			sheetSizes.addItem(SheetSize.SIZES[i].label);
		
		sheetSizes.addItem(Translator.translate("USER_DEFINED"));
		sheetSizes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFields();
			}
		});
		
		widthField = new JTextField();
		heightField = new JTextField();
		
		((AbstractDocument) widthField.getDocument()).setDocumentFilter(new UnitDocumentFilter());
		((AbstractDocument) heightField.getDocument()).setDocumentFilter(new UnitDocumentFilter());
		
		label = Translator.translate("SHEET_SIZE");
		updateFields();
	}
	
	public void setLabel(String label) {
		this.label = Translator.translate(label);
	}
	
	public void setSheetSize(SheetSize sheetSize) {
		if(sheetSize.key >= 0)
			sheetSizes.setSelectedIndex(sheetSize.key);
		else {
			sheetSizes.setSelectedIndex(SheetSize.SIZES.length);
			showValues(sheetSize);
		}
	}
	
	public SheetSize getSheetSize() {
		int idx = sheetSizes.getSelectedIndex();
		if(idx < SheetSize.SIZES.length)
			return SheetSize.SIZES[idx];
		
		int px = UnitConverter.getUnitValue(widthField.getText());
		int py = UnitConverter.getUnitValue(heightField.getText());
		return new SheetSize(px, py);
	}
	
	public int addTo(JComponent parent, int startRow) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = DEFAULT_INSETS;
		c.gridy = startRow;
		
		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(label), c);
		c.gridx++;
		//c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(sheetSizes, c);
		c.gridy++;
		
		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(Translator.translate("WIDTH")), c);
		c.gridx++;
		//c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(widthField, c);
		c.gridy++;
		
		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(Translator.translate("HEIGHT")), c);
		c.gridx++;
		//c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(heightField, c);
		c.gridy++;
		
		return c.gridy;
	}
	
	private void updateFields() {
		int idx = sheetSizes.getSelectedIndex();
		boolean userDef = (idx == SheetSize.SIZES.length);
		widthField.setEnabled(userDef);
		heightField.setEnabled(userDef);
		
		if(!userDef)
			showValues(SheetSize.SIZES[idx]);
	}
	
	private void showValues(SheetSize ss) {
		widthField.setText(UnitConverter.getStringValue(ss.width));
		heightField.setText(UnitConverter.getStringValue(ss.height));
	}
}
