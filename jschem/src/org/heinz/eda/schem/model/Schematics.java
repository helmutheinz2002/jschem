
package org.heinz.eda.schem.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.util.GridHelper;

public class Schematics {

	public static final String PROPERTY_DIRTY = "Schematics.dirty";

	private final List sheets = new ArrayList();

	private final List listeners = new ArrayList();

	private boolean sheetsDirty;

	private boolean dirty;

	private boolean original = true;

	private PropertyChangeListener propertyChangeListener;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Schematics() {
		init();
	}

	public Schematics(String sheetName) {
		init();
		addSheet(sheetName, true);
		setMeDirty(false);
	}

	private void init() {
		propertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(Sheet.PROPERTY_DIRTY)) {
					boolean oldVal = isDirty();
					for(Iterator it = sheets(); it.hasNext();) {
						Sheet sheet = (Sheet) it.next();
						if(sheet.isDirty()) {
							sheetsDirty = true;
							break;
						}
					}

					pcs.firePropertyChange(PROPERTY_DIRTY, (Boolean) oldVal, (Boolean) isDirty());
				}
			}

		};
	}


	public static List getComponentList(List sheets, Class clazz) {
		List objects = new ArrayList();

		for(Iterator sit = sheets.iterator(); sit.hasNext();) {
			Sheet sheet = (Sheet) sit.next();

			for(Iterator it = sheet.components(); it.hasNext();) {
				AbstractComponent ac = (AbstractComponent) it.next();

				if(ac.getClass().equals(clazz)) {
					objects.add(ac);
				}
			}
		}

		return objects;
	}

	public void setAuthorInfo() {
		for(Iterator it = sheets.iterator(); it.hasNext();) {
			Sheet s = (Sheet) it.next();
			s.setAuthorInfo();
		}
	}

	public int getSheetCount() {
		return sheets.size();
	}

	public int guessSnapGrid(List onGridPins, List offGridPins) {
		Map pinCoords = new HashMap();

		for(Iterator it = sheets.iterator(); it.hasNext();) {
			Sheet s = (Sheet) it.next();
			Sheet.collectPins(s.getComponents(), pinCoords);
		}

		return GridHelper.guessGrid(pinCoords, onGridPins, offGridPins);
	}

	public void enforceSnapGrid() {
		int snapGrid = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING);

		for(Iterator it = sheets.iterator(); it.hasNext();) {
			Sheet s = (Sheet) it.next();
			s.enforceSnapGrid(s.getComponents(), snapGrid);
		}
	}

	public final Sheet addSheet(String title, boolean init) {
		int ss = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_DEFAULT_SHEET_SIZE);
		Sheet sheet = new Sheet(title, SheetSize.SIZES[ss], true);
		addSheet(sheet);
		return sheet;
	}

	public void addSheet(Sheet sheet) {
		addSheet(sheet, sheets.size());
	}

	public void addSheet(Sheet sheet, int index) {
		sheets.add(index, sheet);
		setMeDirty(true);
		sheet.addPropertyChangeListener(propertyChangeListener);
		fireSheetAdded(sheet, index);
	}

	public void addSheets(List sheets) {
		for(Iterator it = sheets.iterator(); it.hasNext();) {
			Sheet s = (Sheet) it.next();
			addSheet(s);
		}
	}

	public boolean containsSheet(Sheet sheet) {
		return sheets.contains(sheet);
	}

	public int removeSheet(Sheet sheet) {
		return removeSheet(sheet, true);
	}

	public int removeSheet(Sheet sheet, boolean release) {
		if(sheets.size() <= 1) {
			return -1;
		}

		int idx = sheets.indexOf(sheet);
		sheets.remove(sheet);
		setMeDirty(true);
		sheet.removePropertyChangeListener(propertyChangeListener);
		fireSheetRemoved(sheet, idx);

		if(release) {
			sheet.release();
		}
		return idx;
	}

	public void moveSheet(Sheet sheet, boolean left) {
		int idx = sheets.indexOf(sheet);
		if((idx == 0) && left) {
			return;
		}
		if((idx == (sheets.size() - 1)) && !left) {
			return;
		}

		setMeDirty(true);
		int oldIdx = idx;
		if(left) {
			idx--;
		} else {
			idx++;
		}
		sheets.remove(sheet);
		sheets.add(idx, sheet);

		fireSheetMoved(sheet, oldIdx, idx);
	}

	public Iterator sheets() {
		return sheets.iterator();
	}

	public Sheet getSheetAt(int index) {
		return (Sheet) sheets.get(index);
	}

	public void addSchematicsListener(SchematicsListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removeSchematicsListener(SchematicsListener listener) {
		listeners.remove(listener);
	}

	private void updateSheet(Sheet sheet, int index) {
		sheet.setIndexInfo(index);
	}

	private void updateAllSheets() {
		int idx = 0;
		for(Iterator it = sheets.iterator(); it.hasNext(); idx++) {
			Sheet s = (Sheet) it.next();
			updateSheet(s, idx);
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireSheetAdded(Sheet sheet, int newIndex) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			SchematicsListener l = (SchematicsListener) it.next();
			try {
				l.sheetAdded(sheet, newIndex);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireSheetRemoved(Sheet sheet, int oldIndex) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			SchematicsListener l = (SchematicsListener) it.next();
			try {
				l.sheetRemoved(sheet, oldIndex);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireSheetMoved(Sheet sheet, int oldIndex, int newIndex) {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			SchematicsListener l = (SchematicsListener) it.next();
			try {
				l.sheetMoved(sheet, oldIndex, newIndex);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isDirty() {
		return dirty || sheetsDirty;
	}

	public boolean isOriginal() {
		return original && !isDirty();
	}

	private void setMeDirty(boolean dirty) {
		original = !dirty;

		boolean oldVal = isDirty();
		this.dirty = dirty;
		boolean d = isDirty();
		pcs.firePropertyChange(PROPERTY_DIRTY, (Boolean) oldVal, (Boolean) d);
	}

	public void setDirty(boolean dirty) {
		boolean oldVal = isDirty();

		this.dirty = dirty;
		for(Iterator it = sheets(); it.hasNext();) {
			Sheet sheet = (Sheet) it.next();
			sheet.setDirty(dirty);
		}

		sheetsDirty = dirty;
		if(dirty) {
			original = false;
		}

		pcs.firePropertyChange(PROPERTY_DIRTY, (Boolean) oldVal, (Boolean) isDirty());
	}

}
