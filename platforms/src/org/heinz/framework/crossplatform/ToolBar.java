
package org.heinz.framework.crossplatform;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JToolBar;

public class ToolBar extends JToolBar {

	private List extraObjects;

	public ToolBar() {
		setFloatable(false);
	}

	public void addExtraObjects(List extraObjects) {
		if(this.extraObjects != null) {
			for(Iterator it = this.extraObjects.iterator(); it.hasNext();) {
				Component c = (Component) it.next();
				remove(c);
			}
		}

		this.extraObjects = extraObjects;

		if(this.extraObjects != null) {
			for(Iterator it = this.extraObjects.iterator(); it.hasNext();) {
				Component c = (Component) it.next();
				add(c);
			}
		}

		//validate();
		setVisible(false);
		setVisible(true);
	}

}
