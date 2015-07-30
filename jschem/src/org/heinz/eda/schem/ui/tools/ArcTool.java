
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.heinz.eda.schem.model.ArcType;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Arc;
import org.heinz.eda.schem.ui.beans.ArcBean;

public class ArcTool extends OrientedNewTool {

	private final ArcBean arcTypeBean;

	public ArcTool() {
		super("circletool.png", true);

		constrainMouse = false;

		addToolbarObject(new JToolBar.Separator());

		arcTypeBean = new ArcBean();
		arcTypeBean.setArcType(ArcType.ARC_FULL);

		for(Iterator it = arcTypeBean.getGuiElements(false).iterator(); it.hasNext();) {
			addToolbarObject((JComponent) it.next());
		}
	}

	@Override
	protected AbstractComponent createComponent(int x, int y) {
		Arc arc = new Arc(x, y, 0, arcTypeBean.getArcType());
		arc.setOrientation(getOrientation());
		return arc;
	}

	@Override
	protected void dragToPosition(Point p) {
		Arc arc = (Arc) getNewComponent();
		int dx = p.x - startPosition.x;
		int dy = p.y - startPosition.y;
		int rad = (int) Math.sqrt(dx * dx + dy * dy);
		Point r = sheetPanel.constrainPoint(rad, 0, false);
		arc.setRadius(r.x);
	}

}
