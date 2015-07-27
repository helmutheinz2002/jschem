package org.heinz.eda.schem.ui.dialog.library;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

import org.heinz.eda.schem.model.Library;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.dialog.StandardDialog;
import org.heinz.framework.crossplatform.utils.Translator;

public class LibraryUpdatePanel extends StandardDialogPanel implements ActionListener {
	private JTable table;
	private LibraryUpdateTableModel tableModel;
	private JCheckBox hideBox;
	private JButton overwriteButton;
	private JButton renameButton;
	private JButton defaultButton;
	private JButton skipButton;
	private JButton diffButton;
	private Map buttons = new HashMap();
	
	public LibraryUpdatePanel() {
		super(Translator.translate("LIBRARY_UPDATE"));
		init();
	}
	
	public void display(List updateActions) {
		tableModel = new LibraryUpdateTableModel(updateActions);
		table.setModel(tableModel);
		setColumnWidths();
		hideBox.setSelected(true);
		tableModel.filterSkipActions(hideBox.isSelected());
		checkButtons();
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		
		table = new JTable(new LibraryUpdateTableModel(new ArrayList()));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setColumnWidths();
		
		JScrollPane tableScroller = new JScrollPane(table);
		tableScroller.getViewport().setBackground(table.getBackground());
		table.setGridColor(table.getBackground());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 10;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		add(tableScroller, c);
		
		hideBox = new JCheckBox(Translator.translate("HIDE_UP_TO_DATE_FILES"));
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 11;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		add(hideBox, c);
		
		defaultButton = new JButton(Translator.translate("UPDATE_ACTION_DEFAULT"));
		defaultButton.setToolTipText(Translator.translate("UPDATE_ACTION_DEFAULT_TOOLTIP"));
		overwriteButton = new JButton(Translator.translate("UPDATE_ACTION_OVERWRITE"));
		overwriteButton.setToolTipText(Translator.translate("UPDATE_ACTION_OVERWRITE_TOOLTIP"));
		renameButton = new JButton(Translator.translate("UPDATE_ACTION_MOVE"));
		renameButton.setToolTipText(Translator.translate("UPDATE_ACTION_MOVE_TOOLTIP"));
		skipButton = new JButton(Translator.translate("UPDATE_ACTION_IGNORE"));
		skipButton.setToolTipText(Translator.translate("UPDATE_ACTION_IGNORE_TOOLTIP"));
		diffButton = new JButton(Translator.translate("COMPARE_COMPONENT_VERSIONS"));
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		addButton(defaultButton, -1, c);
		add(new JSeparator(), c);
		c.gridy ++;
		addButton(overwriteButton, Library.UPDATE_ACTION_OVERWRITE, c);
		addButton(renameButton, Library.UPDATE_ACTION_RENAME, c);
		addButton(skipButton, Library.UPDATE_ACTION_SKIP, c);
		add(new JSeparator(), c);
		c.gridy ++;
		addButton(diffButton, Library.UPDATE_ACTION_SKIP, c);
		
		hideBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.filterSkipActions(hideBox.isSelected());
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				checkButtons();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
					compare(table.getSelectedRow());
			}
		});
	}
	
	private void checkButtons() {
		int sel = table.getSelectedRowCount();
		for(Iterator it=buttons.keySet().iterator(); it.hasNext();) {
			JButton b = (JButton) it.next();
			b.setEnabled(sel > 0);
		}
		diffButton.setEnabled(sel == 1);
	}

	private void addButton(JButton b, int action, GridBagConstraints c) {
		buttons.put(b, new Integer(action));
		add(b, c);
		b.addActionListener(this);
		c.gridy ++;
	}
	
	private void setColumnWidths() {
		JTableHeader th = table.getTableHeader();
		for(int i=0; i<LibraryUpdateTableModel.DEFAULT_COLUMN_WIDTHS.length; i++)
			th.getColumnModel().getColumn(i).setPreferredWidth(LibraryUpdateTableModel.DEFAULT_COLUMN_WIDTHS[i]);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == diffButton) {
			compare(table.getSelectedRow());
			return;
		}
		
		Integer rawAction = (Integer) buttons.get(e.getSource());
		int action = rawAction.intValue();
		int[] selRows = table.getSelectedRows();
		for(int i=0; i<selRows.length; i++) {
			if(action != -1)
				tableModel.setActionAt(selRows[i], action);
			else {
				int a = tableModel.getRowAt(selRows[i]).defaultAction;
				tableModel.setActionAt(selRows[i], a);
			}
		}
	}

	private void compare(int row) {
		if(row < 0)
			return;
		
		ComponentDiffPanel diffPanel = new ComponentDiffPanel();
		Library.UpdateAction action = tableModel.getRowAt(row);
		diffPanel.compare(action.file, new ByteArrayInputStream(action.data));
		StandardDialog.showDialog((Window) getTopLevelAncestor(), diffPanel, StandardDialog.CLOSE_BUTTON);
	}

	public String check() {
		return null;
	}

	public void ok() {
	}
}
