
package org.heinz.eda.schem.ui.tools;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.ui.beans.ColorButtonBean;
import org.heinz.eda.schem.util.ExtRect;

public abstract class AbstractNewTool extends SchemEditTool implements PropertyChangeListener {

	private AbstractComponent newComp;

	protected boolean constrainMouse = true;

	protected ColorButtonBean colorBean;

	protected ColorButtonBean fillColorBean;

	@SuppressWarnings("LeakingThisInConstructor")
	protected AbstractNewTool(String icon, boolean fillColor) {
		super(icon);

		colorBean = new ColorButtonBean("foregroundcolor.png", "COLOR_TITLE", "COLOR_BUTTON_TOOLTIP", false);
		colorBean.setColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR));

		fillColorBean = new ColorButtonBean("backgroundcolor.png", "FILL_COLOR_TITLE", "FILL_COLOR_BUTTON_TOOLTIP", true);
		fillColorBean.setColor(null);

		addToolbarObject(colorBean.getButton());
		if(fillColor) {
			addToolbarObject(fillColorBean.getButton());
		}

		SchemOptions.instance().addPropertyChangeListener(this);
	}

	@Override
	protected boolean handleKey(KeyEvent e) {
		return false;
	}

	@Override
	protected void setup() {
		sheetPanel.clearSelection();
	}

	@Override
	protected void handleCancel() {
		if(newComp != null) {
			sheetPanel.getSheet().removeComponent(newComp, true);
		}
		done();
	}

	protected AbstractComponent getNewComponent() {
		return newComp;
	}

	@Override
	protected void handleMouseDown(MouseEvent e) {
		Point p = e.getPoint();
		p = sheetPanel.constrainScreenPoint(p.x, p.y, false);
		newComp = createComponent(p.x, p.y);
		newComp.setColor(colorBean.getColor());
		newComp.setFillColor(fillColorBean.getColor());
		sheetPanel.getSheet().addComponent(newComp, true);
	}

	@Override
	protected void handleMouseDrag(MouseEvent e) {
		Point p = e.getPoint();
		p = sheetPanel.constrainScreenPoint(p.x, p.y, false);
		dragToPosition(p);
	}

	@Override
	protected void handleMouseUp(MouseEvent e) {
		Point p = e.getPoint();
		p = sheetPanel.constrainScreenPoint(p.x, p.y, !constrainMouse);
		dragToPosition(p);
		p = new Point(p.x - startPosition.x, p.y - startPosition.y);
		checkSize(p);
		done();
	}

	protected abstract AbstractComponent createComponent(int x, int y);

	protected void dragToPosition(Point p) {
		newComp.setPosition(p.x, p.y);
	}

	protected void checkSize(Point p) {
		if(isUnacceptable(newComp, p)) {
			sheetPanel.getSheet().removeComponent(newComp, true);
			newComp = null;
		}
	}

	protected boolean isUnacceptable(AbstractComponent newComponent, Point p) {
		ExtRect r = newComponent.getBoundingBox();
		boolean zero = (r.width == 0) && (r.height == 0);
		return zero;
	}

	@Override
	protected void done() {
		super.done();
		newComp = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SchemOptions.PROPERTY_COMPONENT_COLOR)) {
			Color c = (Color) evt.getNewValue();
			colorBean.setColor(c);
		}
	}

}
