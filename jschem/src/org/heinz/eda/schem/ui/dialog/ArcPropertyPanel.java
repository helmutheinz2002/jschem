package org.heinz.eda.schem.ui.dialog;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Arc;
import org.heinz.eda.schem.ui.beans.ArcBean;
import org.heinz.framework.crossplatform.utils.Translator;

public class ArcPropertyPanel extends AbstractComponentPropertyPanel {
	private ArcBean arcBean;

	public ArcPropertyPanel() {
		this(Translator.translate("ARC_PROPERTIES"), true);
	}
	
	protected ArcPropertyPanel(String title, boolean fill) {
		super(title, true);

		arcBean = new ArcBean();
		nextRow = arcBean.addTo(this, nextRow);

		if(fill)
			addFiller();
	}

	public void setComponent(AbstractComponent c) {
		super.setComponent(c);
		
		Arc arc = (Arc) getComponent();
		arcBean.setArcType(arc.getArcType());
		arcBean.setRadius(arc.getRadius());
		fillColorBean.setColor(arc.getFillColor());
	}

	public void ok() {
		super.ok();

		Arc arc = (Arc) getComponent();
		arc.setArcType(arcBean.getArcType());
		arc.setRadius(arcBean.getRadius());
		arc.setFillColor(fillColorBean.getColor());
	}
	
	public void prepareToShow() {
		arcBean.radiusField.requestFocusInWindow();
	}
}
