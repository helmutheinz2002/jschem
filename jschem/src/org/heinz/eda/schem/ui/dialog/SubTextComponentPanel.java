
package org.heinz.eda.schem.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class SubTextComponentPanel extends JPanel {

	private JTable table;

	private SubTextComponentTableModel tableModel;

	private final JButton addButton;

	private JButton removeButton;

	private AbstractComponent component;

	public SubTextComponentPanel() {
		super(new BorderLayout());

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Orientation.class, OrientationCellRenderer.renderer());
		table.setDefaultRenderer(SubTextComponentTableModel.FontInfo.class, FontInfoCellRenderer.renderer());
		table.setDefaultRenderer(Color.class, ColorCellRenderer.renderer());
		table.setDefaultEditor(Orientation.class, OrientationCellRenderer.editor());
		table.setDefaultEditor(SubTextComponentTableModel.FontInfo.class, FontInfoCellRenderer.editor());
		table.setDefaultEditor(Color.class, ColorCellRenderer.editor());
		table.setRowHeight(30);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(table);
		sp.getViewport().setBackground(table.getBackground());
		add(BorderLayout.CENTER, sp);

		JPanel op = new JPanel(new BorderLayout());
		op.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
		add(BorderLayout.EAST, op);
		op.add(BorderLayout.NORTH, p);

		addButton = new JButton(Translator.translate("NEW"));
		removeButton = new JButton(Translator.translate("REMOVE"));
		removeButton.setEnabled(false);

		p.add(addButton);
		p.add(removeButton);

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.addText(null);
				int l = tableModel.getRowCount();
				table.getSelectionModel().addSelectionInterval(l - 1, l - 1);
				table.editCellAt(l - 1, SubTextComponentTableModel.COL_TEXT);
				table.getEditorComponent().requestFocusInWindow();
			}

		});
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int r = table.getSelectedRow();
				if(r >= 0) {
					tableModel.removeRow(r);
				}
			}

		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int s = table.getSelectedRow();
				boolean enable = (s >= 0);
				if(enable) {
					SubTextComponentTableModel.TextInfo info = (SubTextComponentTableModel.TextInfo) tableModel.getTexts().get(s);
					enable = (info.infoText == null);
				}
				removeButton.setEnabled(enable);
			}

		});

		setPreferredSize(new Dimension(600, 350));
	}

	public void setComponent(AbstractComponent c) {
		component = c;
		tableModel = new SubTextComponentTableModel(c);
		table.setModel(tableModel);

		for(int i = 0; i < SubTextComponentTableModel.DEFAULT_COL_WIDTH.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(SubTextComponentTableModel.DEFAULT_COL_WIDTH[i]);
		}

		table.getColumnModel().getColumn(SubTextComponentTableModel.COL_ORIENTATION).setPreferredWidth(OrientationCellRenderer.renderer().getPreferredSize().width + 10);
		table.getColumnModel().getColumn(SubTextComponentTableModel.COL_FONT).setPreferredWidth(FontInfoCellRenderer.renderer().getPreferredSize().width + 10);
		table.getColumnModel().getColumn(SubTextComponentTableModel.COL_COLOR).setPreferredWidth(ColorCellRenderer.renderer().getPreferredSize().width + 10);
	}

	public void ok() {
		TableCellEditor editor = table.getCellEditor();
		if(editor != null) {
			editor.stopCellEditing();
		}

		int y = 0;
		for(Iterator it = tableModel.getTexts().iterator(); it.hasNext();) {
			SubTextComponentTableModel.TextInfo info = (SubTextComponentTableModel.TextInfo) it.next();
			info.text.setText(info.newText);
			info.text.setOrientation(info.newOrientation);
			info.text.setVisible(info.isVisible);
			info.text.setFontName(info.fontInfo.fontName);
			info.text.setFontSize(info.fontInfo.fontSize);
			info.text.setFontStyle(info.fontInfo.fontStyle);
			info.text.setColor(info.color);
			boolean ok = true;
			boolean hasText = (info.text.getText() != null) && (info.text.getText().length() > 0);
			if(info.infoText == null) {
				ok = hasText;
			}

			if(info.isNew) {
				if(ok) {
					component.addComponent(info.text, true);
					y = AbstractComponent.placeNextText(info.text, y);
				}
			} else {
				if(!ok) {
					component.removeComponent(info.text);
				}
			}
		}

		for(Iterator it = tableModel.getDeletedTexts().iterator(); it.hasNext();) {
			SubTextComponentTableModel.TextInfo info = (SubTextComponentTableModel.TextInfo) it.next();
			component.removeComponent(info.text);
		}
	}

}
