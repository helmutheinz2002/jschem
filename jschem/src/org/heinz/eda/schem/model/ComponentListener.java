
package org.heinz.eda.schem.model;

import java.awt.Point;
import java.util.Map;

import org.heinz.eda.schem.model.components.AbstractComponent;

public interface ComponentListener {

	void componentAdded(AbstractComponent c);

	void componentRemoved(AbstractComponent c);

	void componentWillChange(AbstractComponent c);

	void componentChanged(AbstractComponent c);

	void handlesMoved(Map handles, Point offset, boolean dragging);

}
