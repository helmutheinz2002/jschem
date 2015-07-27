package org.heinz.eda.schem.model;

import java.awt.Point;
import java.util.Map;

import org.heinz.eda.schem.model.components.AbstractComponent;

public class ComponentAdapter implements ComponentListener {
	public void componentChanged(AbstractComponent c) {
	}

	public void handlesMoved(Map handles, Point offset, boolean dragging) {
	}

	public void componentAdded(AbstractComponent c) {
	}

	public void componentRemoved(AbstractComponent c) {
	}

	public void componentWillChange(AbstractComponent c) {
	}
}
