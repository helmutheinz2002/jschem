package org.heinz.eda.schem.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.heinz.eda.schem.ui.beans.ColorButtonBean;

public class ColorCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
	private JPanel panel;
	private ColorButtonBean colorBean;
	private static ColorCellRenderer renderer;
	private static ColorCellRenderer editor;
	
	public static ColorCellRenderer renderer() {
		if(renderer == null)
			renderer = new ColorCellRenderer();
		return renderer;
	}
	
	public static ColorCellRenderer editor() {
		if(editor == null)
			editor = new ColorCellRenderer();
		return editor;
	}
	
	private ColorCellRenderer() {
		panel = new JPanel(new GridBagLayout());
		colorBean = new ColorButtonBean("foregroundcolor.png", "COLOR_TITLE", "COLOR_BUTTON_TOOLTIP", false);
		colorBean.addTo(panel, 0);
	}
	
	private void setup(JTable table, Object value, boolean isSelected) {
		Color bg = table.getBackground();
		if(isSelected)
			bg = table.getSelectionBackground();
		panel.setBackground(bg);
		colorBean.setColor((Color) value);
	}
	
    public Dimension getPreferredSize() {
    	return panel.getPreferredSize();
    }
    
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setup(table, value, isSelected);
		return panel;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setup(table, value, isSelected);
		return panel;
	}

	public Object getCellEditorValue() {
		return colorBean.getColor();
	}

}
