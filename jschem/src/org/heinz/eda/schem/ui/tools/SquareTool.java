
package org.heinz.eda.schem.ui.tools;

import java.awt.Dimension;
import java.awt.Point;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Square;

public class SquareTool extends AbstractNewTool {

	public SquareTool() {
		super("squaretool.png", true);
	}

	@Override
	protected AbstractComponent createComponent(int x, int y) {
		Square sq = new Square(x, y, 0, 0);
		return sq;
	}

	@Override
	protected void dragToPosition(Point p) {
		Square square = (Square) getNewComponent();
		square.setSize(new Dimension(p.x - startPosition.x, p.y - startPosition.y));
	}

}
