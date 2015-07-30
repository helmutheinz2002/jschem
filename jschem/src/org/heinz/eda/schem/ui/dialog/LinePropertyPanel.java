
package org.heinz.eda.schem.ui.dialog;

import java.awt.Point;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Line;
import org.heinz.eda.schem.ui.beans.PositionBean;
import org.heinz.framework.crossplatform.utils.Translator;

public class LinePropertyPanel extends AbstractComponentPropertyPanel {

	private final PositionBean endPosBean;

	@SuppressWarnings("LeakingThisInConstructor")
	public LinePropertyPanel() {
		super(Translator.translate("LINE_PROPERTIES"), false);

		endPosBean = new PositionBean();
		endPosBean.setLabels("LINE_END_OFFSET_X", "LINE_END_OFFSET_Y");
		nextRow = endPosBean.addTo(this, nextRow);

		addFiller();
	}

	@Override
	public void setComponent(AbstractComponent c) {
		super.setComponent(c);

		Line line = (Line) getComponent();
		int dx = line.getDx();
		int dy = line.getDy();
		endPosBean.setPosition(new Point(dx, dy));
	}

	@Override
	public void ok() {
		super.ok();

		Line line = (Line) getComponent();
		Point p = endPosBean.getPosition();
		line.setOffset(p.x, p.y, true);
	}

	@Override
	public void prepareToShow() {
		endPosBean.x.requestFocusInWindow();
	}

}
