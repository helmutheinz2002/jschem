package org.heinz.eda.schem.ui;

import org.heinz.framework.crossplatform.ToolBar;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;


public class SchemToolbar extends ToolBar {
	public SchemToolbar() {
		super();
		setFloatable(false);
		init();
	}
	
	private void init() {
		ApplicationActions actions = ApplicationActions.instance();
		
		add(actions.newItem);
		add(actions.openItem);
		add(actions.saveItem);
		addSeparator();
		add(actions.undoItem);
		add(actions.redoItem);
		addSeparator();
		add(actions.zoomInItem);
		add(actions.zoomOutItem);
		add(actions.zoomFitItem);
		addSeparator();
		add(actions.preferencesItem);
		add(actions.propertiesItem);
		addSeparator();
	}
}
