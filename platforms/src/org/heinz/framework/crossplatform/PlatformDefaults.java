package org.heinz.framework.crossplatform;

import java.util.HashMap;

public class PlatformDefaults extends HashMap {
	public static final String REVERSE_BUTTON_ORDER = "reverseButtonOrder";
	public static final String OPEN_DOCS_MAXIMIZED = "openDocsMaximized";
	public static final String BOTTOM_INSET = "bottomInset";
	public static final String MENU_ICONS = "menuIcons";
	public static final String POPUP_MENU_ACCELERATORS = "popupMenuAccelerators";
	public static final String POPUP_MENU_MNEMONICS = "popupMenuMnemonics";
	
	public int getInt(String key) {
		return ((Integer) get(key)).intValue();
	}
	
	public boolean getBool(String key) {
		return ((Boolean) get(key)).booleanValue();
	}
}
