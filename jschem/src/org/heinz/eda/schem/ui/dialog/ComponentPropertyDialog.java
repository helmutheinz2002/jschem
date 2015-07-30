
package org.heinz.eda.schem.ui.dialog;

import java.awt.Dimension;
import java.awt.Frame;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Arc;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Line;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Polygon;
import org.heinz.eda.schem.model.components.Square;
import org.heinz.eda.schem.model.components.Symbol;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.ui.undo.UndoPropertyChange;
import org.heinz.framework.crossplatform.dialog.StandardDialog;

public class ComponentPropertyDialog {

	public static void openDialog(Frame owner, AbstractComponent c) {
		AbstractComponentPropertyPanel editPanel = null;

		if(c instanceof Text) {
			editPanel = new TextPropertyPanel();
		} else if(c instanceof Pin) {
			editPanel = new PinPropertyPanel();
		} else if(c instanceof Arc) {
			editPanel = new ArcPropertyPanel();
		} else if(c instanceof Line) {
			editPanel = new LinePropertyPanel();
		} else if(c instanceof Square) {
			editPanel = new SquarePropertyPanel();
		} else if(c instanceof Component) {
			editPanel = new ComponentPropertyPanel(c instanceof Symbol);
		} else if(c instanceof Polygon) {
			editPanel = new PolygonPropertyPanel();
		}

		if(editPanel == null) {
			throw new IllegalArgumentException("Unknown component type");
		}

		editPanel.setComponent(c);

		UndoPropertyChange undo = new UndoPropertyChange(c);
		StandardDialog.showDialog(owner, editPanel, new Dimension(400, 200));
		undo.stopListening();
	}

}
