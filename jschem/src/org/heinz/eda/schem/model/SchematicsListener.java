
package org.heinz.eda.schem.model;


public interface SchematicsListener {

	void sheetAdded(Sheet sheet, int newIndex);

	void sheetRemoved(Sheet sheet, int oldIndex);

	void sheetMoved(Sheet sheet, int oldIndex, int newIndex);

}
