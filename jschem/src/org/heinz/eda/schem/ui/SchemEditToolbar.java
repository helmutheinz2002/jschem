package org.heinz.eda.schem.ui;

import org.heinz.eda.schem.ui.tools.ArcTool;
import org.heinz.eda.schem.ui.tools.LibraryTool;
import org.heinz.eda.schem.ui.tools.LineTool;
import org.heinz.eda.schem.ui.tools.PinTool;
import org.heinz.eda.schem.ui.tools.PolygonTool;
import org.heinz.eda.schem.ui.tools.SelectionTool;
import org.heinz.eda.schem.ui.tools.SquareTool;
import org.heinz.eda.schem.ui.tools.TextTool;
import org.heinz.eda.schem.ui.tools.WireSplitTool;
import org.heinz.eda.schem.ui.tools.WireTool;
import org.heinz.eda.schem.ui.tools.ZoomTool;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.EditToolListener;

public class SchemEditToolbar extends EditToolBar implements EditToolListener {
	public SchemEditToolbar() {
		super(SelectionTool.class);
		SchemActions actions = SchemActions.instance();
		
		addEditTool(actions.selectionToolItem, new SelectionTool());
		addEditTool(actions.zoomToolItem, new ZoomTool());
		addSeparator();
		addEditTool(actions.libToolItem, new LibraryTool());
		addEditTool(actions.wireToolItem, new WireTool());
		addEditTool(actions.newCornerToolItem, new WireSplitTool());
		addSeparator();
		addEditTool(actions.pinToolItem, new PinTool());
		addEditTool(actions.lineToolItem, new LineTool());
		addEditTool(actions.squareToolItem, new SquareTool());
		addEditTool(actions.arcToolItem, new ArcTool());
		addEditTool(actions.polygonToolItem, new PolygonTool());
		addEditTool(actions.textToolItem, new TextTool());
	}
	
	protected Class getNextTool(Class toolClass) {
		if((toolClass == WireSplitTool.class) || (toolClass == ZoomTool.class) || (toolClass == LibraryTool.class))
			return SelectionTool.class;
		return super.getNextTool(toolClass);
	}
}
