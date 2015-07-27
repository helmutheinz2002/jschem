package org.heinz.eda.schem.ui.dialog.library;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.heinz.eda.schem.model.Library;
import org.heinz.framework.crossplatform.utils.Translator;

public class LibraryUpdateTableModel extends AbstractTableModel {
	private static final int COL_FILE = 0;
	private static final int COL_STATUS = 1;
	private static final int COL_ACTION = 2;
	private static final int COL_INFO = 3;
	private static final int COL_LAST = 4;
	
	public static final int[] DEFAULT_COLUMN_WIDTHS = { 300, 90, 90, 500 };
	
	private List updateActions;
	private List filterList;
	private boolean filterSkip;
	
	public LibraryUpdateTableModel(List updateActions) {
		this.updateActions = updateActions;
		filter();
	}

	public void filterSkipActions(boolean filterSkip) {
		this.filterSkip = filterSkip;
		filter();
	}
	
	private void filter() {
		if(filterSkip) {
			filterList = new ArrayList();
			for(Iterator it=updateActions.iterator(); it.hasNext();) {
				Library.UpdateAction action = (Library.UpdateAction) it.next();
				if(action.defaultAction != Library.UPDATE_ACTION_SKIP)
					filterList.add(action);
			}
		} else
			filterList = new ArrayList(updateActions);
		
		fireTableDataChanged();
	}
	
	public Iterator getUnfilteredActions() {
		return updateActions.iterator();
	}
	
	public void setActionAt(int rowIndex, int action) {
		Library.UpdateAction row = (Library.UpdateAction) filterList.get(rowIndex);
		row.action = action;
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	public Library.UpdateAction getRowAt(int rowIndex) {
		Library.UpdateAction row = (Library.UpdateAction) filterList.get(rowIndex);
		return row;
	}
	
	public int getColumnCount() {
		return COL_LAST;
	}

	public int getRowCount() {
		return filterList.size();
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
			case COL_FILE: return Translator.translate("LIBRARY_UPDATE_FILE");
			case COL_STATUS: return Translator.translate("LIBARAY_UPDATE_STATUS");
			case COL_ACTION: return Translator.translate("LIBARAY_UPDATE_ACTION");
			case COL_INFO: return Translator.translate("LIBARAY_UPDATE_INFO");
			default:
				break;
		}
		return "ERROR";
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Library.UpdateAction action = (Library.UpdateAction) filterList.get(rowIndex);
		
		switch(columnIndex) {
			case COL_FILE:
				return action.fileName;
			case COL_STATUS:
				return Translator.translate(Library.getUpdateText(action.result));
			case COL_ACTION:
				return Translator.translate(Library.getUpdateText(action.action));
			case COL_INFO: {
				String s = action.error;
				try {
					s = Translator.translate(action.error);
				} catch(Exception ex) {
				}
				if(s == null)
					s = "";
				if(action.errorData != null)
					s += action.errorData;
				return s;
			}
			default:
				break;
		}
		return "ERROR";
	}

}
