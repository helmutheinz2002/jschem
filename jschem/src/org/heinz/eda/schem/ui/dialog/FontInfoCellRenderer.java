package org.heinz.eda.schem.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.heinz.eda.schem.ui.beans.FontBean;
import org.heinz.eda.schem.ui.beans.PropertyBean;

public class FontInfoCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor  {
    private JPanel panel;
    private FontBean fontBean;
	private static FontInfoCellRenderer renderer;
	private static FontInfoCellRenderer editor;
	
	public static FontInfoCellRenderer renderer() {
		if(renderer == null)
			renderer = new FontInfoCellRenderer();
		return renderer;
	}
	
	public static FontInfoCellRenderer editor() {
		if(editor == null)
			editor = new FontInfoCellRenderer();
		return editor;
	}
    
    private FontInfoCellRenderer() {
        panel = new JPanel(new GridBagLayout());
        fontBean = new FontBean();
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = PropertyBean.DEFAULT_INSETS;
        gc.gridx = 0;
        gc.gridy = 0;
        for(Iterator it=fontBean.getGuiElements(false).iterator(); it.hasNext();) {
            JComponent c = (JComponent) it.next();
            panel.add(c, gc);
            gc.gridx++;
        }
    }
    
    private void setup(JTable table, Object value, boolean isSelected) {
        Color bg = table.getBackground();
        if(isSelected)
            bg = table.getSelectionBackground();
        panel.setBackground(bg);
        
        SubTextComponentTableModel.FontInfo info = (SubTextComponentTableModel.FontInfo) value;
        fontBean.setFontName(info.fontName);
        fontBean.setFontSize(info.fontSize);
        fontBean.setFontStyle(info.fontStyle);
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
        return new SubTextComponentTableModel.FontInfo(fontBean.getFontName(), fontBean.getFontSize(), fontBean.getFontStyle());
    }

}
