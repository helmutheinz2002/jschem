
package org.heinz.eda.schem.ui.tools;

import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.ui.beans.OrientationBean;

public abstract class OrientedNewTool extends AbstractNewTool {

	protected OrientationBean orientationBean;

	public OrientedNewTool(String icon, boolean fillColor) {
		super(icon, fillColor);

		orientationBean = new OrientationBean();
		orientationBean.setOrientation(Orientation.RIGHT);

		addToolbarObject(new JToolBar.Separator());
		for(Iterator it = orientationBean.getGuiElements().iterator(); it.hasNext();) {
			addToolbarObject((JComponent) it.next());
		}
	}

	protected Orientation getOrientation() {
		return orientationBean.getOrientation();
	}

}
