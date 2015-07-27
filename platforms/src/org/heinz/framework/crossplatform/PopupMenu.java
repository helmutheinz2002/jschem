package org.heinz.framework.crossplatform;

import javax.swing.JPopupMenu;

import org.heinz.framework.crossplatform.utils.MenuHelper;

public class PopupMenu extends JPopupMenu {
	public void setVisible(boolean b) {
		preparePopupMenu(this);
		super.setVisible(b);
	}
	
	public static void preparePopupMenu(JPopupMenu menu) {
		boolean removeIcons = !CrossPlatform.getPlatform().getPlatformDefaults().getBool(PlatformDefaults.MENU_ICONS);
		boolean removeAccelerators = !CrossPlatform.getPlatform().getPlatformDefaults().getBool(PlatformDefaults.POPUP_MENU_ACCELERATORS);
		boolean removeMnemonics = !CrossPlatform.getPlatform().getPlatformDefaults().getBool(PlatformDefaults.POPUP_MENU_MNEMONICS);
		MenuHelper.cleanPopupMenu(menu, removeIcons, removeAccelerators, removeMnemonics);
		MenuHelper.compressMenu(menu);
	}
}
