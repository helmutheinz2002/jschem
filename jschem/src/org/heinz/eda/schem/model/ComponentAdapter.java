
package org.heinz.eda.schem.model;

import java.awt.Point;
import java.util.Map;

import org.heinz.eda.schem.model.components.AbstractComponent;

public class ComponentAdapter implements ComponentListener {

	@Override
	public void componentChanged(AbstractComponent c) {
	}

	@Override
	public void handlesMoved(Map handles, Point offset, boolean dragging) {
	}

	@Override
	public void componentAdded(AbstractComponent c) {
	}

	@Override
	public void componentRemoved(AbstractComponent c) {
	}

	@Override
	public void componentWillChange(AbstractComponent c) {
	}

}
