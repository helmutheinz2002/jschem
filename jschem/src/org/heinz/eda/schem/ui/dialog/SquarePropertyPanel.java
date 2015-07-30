
package org.heinz.eda.schem.ui.dialog;

import java.awt.Point;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Square;
import org.heinz.eda.schem.ui.beans.PositionBean;
import org.heinz.framework.crossplatform.utils.Translator;

public class SquarePropertyPanel extends AbstractComponentPropertyPanel {

	private final PositionBean endPosBean;

	@SuppressWarnings("LeakingThisInConstructor")
	public SquarePropertyPanel() {
		super(Translator.translate("SQUARE_PROPERTIES"), true);

		endPosBean = new PositionBean();
		endPosBean.setLabels("WIDTH", "HEIGHT");
		nextRow = endPosBean.addTo(this, nextRow);

		addFiller();
	}

	@Override
	public void setComponent(AbstractComponent c) {
		super.setComponent(c);

		Square square = (Square) getComponent();
		int dx = square.getWidth();
		int dy = square.getHeight();
		endPosBean.setPosition(new Point(dx, dy));
		fillColorBean.setColor(square.getFillColor());
	}

	@Override
	public void ok() {
		super.ok();

		Square square = (Square) getComponent();
		Point p = endPosBean.getPosition();
		square.setWidth(p.x);
		square.setHeight(p.y);
		square.setFillColor(fillColorBean.getColor());
	}

	@Override
	public void prepareToShow() {
		endPosBean.x.requestFocusInWindow();
	}

}
