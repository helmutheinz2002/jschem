package org.heinz.eda.schem.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.ui.beans.ColorBean;
import org.heinz.eda.schem.ui.beans.OrientationBean;
import org.heinz.eda.schem.ui.beans.PositionBean;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;


public abstract class AbstractComponentPropertyPanel extends StandardDialogPanel {
	protected PositionBean positionBean;
	protected OrientationBean orientationBean;
	protected ColorBean colorBean;
	protected ColorBean fillColorBean;
	protected int nextRow;
	private AbstractComponent component;
	
	public AbstractComponentPropertyPanel(String title, boolean fillColor) {
		super(title);
		
		setLayout(new GridBagLayout());
		
		nextRow = 0;
		orientationBean = new OrientationBean();
		nextRow = orientationBean.addTo(this, nextRow, true);
		
		positionBean = new PositionBean();
		nextRow = positionBean.addTo(this, nextRow);
		
		colorBean = new ColorBean();
		nextRow = colorBean.addTo(this, nextRow, true, false);
		
		fillColorBean = new ColorBean();
		fillColorBean.setLabel("FILL_COLOR");
		if(fillColor)
			nextRow = fillColorBean.addTo(this, nextRow, true, true);
	}
	
	protected void addExtension(JComponent comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = nextRow;
		c.gridwidth = 7;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(10, 0, 0, 0);
		
		add(comp, c);
	}
	
	public AbstractComponent getComponent() {
		return component;
	}
	
	public void setComponent(AbstractComponent c) {
		component = c;
		orientationBean.setOrientation(c.getOrientation());
		positionBean.setPosition(c.getPosition());
		colorBean.setColor(c.getColor());
		fillColorBean.setColor(c.getFillColor());
	}

	public void ok() {
		component.setOrientation(orientationBean.getOrientation());
		Point p = positionBean.getPosition();
		component.setPosition(p.x, p.y);
		component.setColor(colorBean.getColor());
		component.setFillColor(fillColorBean.getColor());
	}
	
	public String check() {
		return null;
	}
	
	protected void addFiller() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = nextRow;
		c.gridx = 20;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		add(new JLabel(), c);
	}
	
	public void prepareToShow() {
		positionBean.x.requestFocusInWindow();
	}
}
