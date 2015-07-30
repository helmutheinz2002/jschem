
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

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.ui.beans.OrientationBean;

public class OrientationCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	private final JPanel panel;

	private final OrientationBean orientationBean;

	private static OrientationCellRenderer renderer;

	private static OrientationCellRenderer editor;

	public static OrientationCellRenderer renderer() {
		if(renderer == null) {
			renderer = new OrientationCellRenderer();
		}
		return renderer;
	}

	public static OrientationCellRenderer editor() {
		if(editor == null) {
			editor = new OrientationCellRenderer();
		}
		return editor;
	}

	private OrientationCellRenderer() {
		panel = new JPanel(new GridBagLayout());
		orientationBean = new OrientationBean();
		orientationBean.addTo(panel, 0, false);
	}

	private void setup(JTable table, Object value, boolean isSelected) {
		Color bg = table.getBackground();
		if(isSelected) {
			bg = table.getSelectionBackground();
		}
		panel.setBackground(bg);

		orientationBean.setOrientation((Orientation) value);
	}

	public Dimension getPreferredSize() {
		return panel.getPreferredSize();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setup(table, value, isSelected);
		return panel;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setup(table, value, isSelected);
		return panel;
	}

	@Override
	public Object getCellEditorValue() {
		return orientationBean.getOrientation();
	}

}
