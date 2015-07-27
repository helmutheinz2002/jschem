package org.heinz.eda.schem.ui.beans;

import java.awt.Component;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.heinz.eda.schem.util.UnitConverter;
import org.heinz.eda.schem.util.UnitDocumentFilter;

public class UnitCombobox extends JComboBox {
	public UnitCombobox() {
		super();
		setEditable(true);
		JTextField tf = getTextField();
		((AbstractDocument) tf.getDocument()).setDocumentFilter(new UnitDocumentFilter());
	}

	public JTextField getTextField() {
		ComboBoxEditor editor = getEditor();
		Component comp = editor.getEditorComponent();
		JTextField tf = (JTextField) comp;
		return tf;
	}
	
	public void addUnitsItem(int units) {
		addItem(getEntry(units));
	}
	
	private Entry getEntry(int units) {
		return new Entry(UnitConverter.getStringValue(units)+"mm", units);
	}
	
	public void setSelectedUnits(int units) {
		for(int i=0; i<getItemCount(); i++) {
			Entry entry = (Entry) getItemAt(i);
			if(entry.value == units) {
				setSelectedIndex(i);
				return;
			}
		}
		
		getEditor().setItem(getEntry(units));
	}
	
	public int getSelectedUnits() {
		JTextField tf = getTextField();
		int units = UnitConverter.getUnitValue(tf.getText());
		return units;
	}
	
	public static class Entry {
		public final int value;
		public final String label;
		
		public Entry(String label, int value) {
			this.label = label;
			this.value = value;
		}
		
		public String toString() {
			return label;
		}
	}
}
