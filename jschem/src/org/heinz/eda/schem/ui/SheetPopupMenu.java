package org.heinz.eda.schem.ui;

import javax.swing.JMenu;

import org.heinz.framework.crossplatform.PopupMenu;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;

public class SheetPopupMenu extends PopupMenu {
	public SheetPopupMenu() {
		add(ApplicationActions.instance().cutItem);
		add(ApplicationActions.instance().copyItem);
		add(ApplicationActions.instance().pasteItem);
		add(ApplicationActions.instance().deleteItem);
		add(ApplicationActions.instance().selectAllItem);
		addSeparator();
		add(SchemActions.instance().groupComponentItem);
		add(SchemActions.instance().ungroupItem);
		addSeparator();
		add(SchemActions.instance().rotateItem);
		add(SchemActions.instance().rotateNoTextItem);
		add(SchemActions.instance().flipLeftRightItem);
		add(SchemActions.instance().flipTopBottomItem);
		addSeparator();
		JMenu stackingMenu = new JMenu(SchemActions.instance().stackingMenu);
		stackingMenu.add(SchemActions.instance().toFrontItem);
		stackingMenu.add(SchemActions.instance().toBackItem);
		add(stackingMenu);
		addSeparator();
		add(SchemActions.instance().libraryItem);
		add(SchemActions.instance().saveComponentItem);
		addSeparator();
		add(ApplicationActions.instance().propertiesItem);
	}
}
