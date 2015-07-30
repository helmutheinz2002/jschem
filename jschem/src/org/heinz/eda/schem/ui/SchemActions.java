
package org.heinz.eda.schem.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.platforms.basic.AbstractActions;
import org.heinz.framework.crossplatform.platforms.basic.ActionStateInfos;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class SchemActions extends AbstractActions {

	public static final String CURSOR_IMAGE_KEY = "SchemActions.CursorImage";

	public static final String STATE_INFO_NUM_SHEETS = "SchemActions.numSheets";

	public static final String STATE_INFO_CAN_MOVE_LEFT = "SchemActions.canMoveLeft";

	public static final String STATE_INFO_CAN_MOVE_RIGHT = "SchemActions.canMoveRight";

	public static final String STATE_INFO_NUM_GROUPS = "SchemActions.numGroups";

	public static final String STATE_INFO_NUM_PINS = "SchemActions.numPins";

	private static SchemActions instance;

	public final Action exportSheetItem;

	public final Action exportSchematicsItem;

	public final Action newSheetItem;

	public final Action renameSheetItem;

	public final Action deleteSheetItem;

	public final Action sheetLeftItem;

	public final Action sheetRightItem;

	public final Action sheetSizeItem;

	public final Action saveComponentItem;

	public final Action groupComponentItem;

	public final Action groupSymbolItem;

	public final Action ungroupItem;

	public final Action libraryItem;

	public final Action updateLibraryItem;

	public final Action selectionToolItem;

	public final Action zoomToolItem;

	public final Action libToolItem;

	public final Action pinToolItem;

	public final Action textToolItem;

	public final Action arcToolItem;

	public final Action squareToolItem;

	public final Action lineToolItem;

	public final Action wireToolItem;

	public final Action polygonToolItem;

	public final Action newCornerToolItem;

	public final Action rotateItem;

	public final Action rotateNoTextItem;

	public final Action flipLeftRightItem;

	public final Action flipTopBottomItem;

	public final Action flipLeftRightNoTextItem;

	public final Action flipTopBottomNoTextItem;

	public final Action stackingMenu;

	public final Action toFrontItem;

	public final Action toBackItem;

	public final Action autoNumberItem;

	private SchemActions() {
		int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		exportSheetItem = new SchemAction("MENU_EXPORT_SHEET", "empty", null, KeyEvent.VK_E);
		exportSchematicsItem = new SchemAction("MENU_EXPORT_SCHEMATICS", "empty", null, KeyEvent.VK_P);

		newSheetItem = new SchemAction("MENU_NEW_SHEET", "empty", null, KeyEvent.VK_N);
		renameSheetItem = new SchemAction("MENU_RENAME_SHEET", "empty", null, KeyEvent.VK_R);
		deleteSheetItem = new SchemAction("MENU_DELETE_SHEET", "empty", null, KeyEvent.VK_D);
		sheetLeftItem = new SchemAction("MENU_SHEET_TAB_LEFT", "empty", null, KeyEvent.VK_L);
		sheetRightItem = new SchemAction("MENU_SHEET_TAB_RIGHT", "empty", null, KeyEvent.VK_I);
		sheetSizeItem = new SchemAction("MENU_SHEET_SIZE", "empty", null, KeyEvent.VK_G);

		saveComponentItem = new SchemAction("MENU_ADD_TO_LIBRARY", "empty", null, KeyEvent.VK_A);
		groupComponentItem = new SchemAction("MENU_GROUP_COMPONENT", "empty", KeyStroke.getKeyStroke(KeyEvent.VK_G, menuMask), KeyEvent.VK_G);
		groupSymbolItem = new SchemAction("MENU_GROUP_SYMBOL", "empty", null, KeyEvent.VK_S);
		ungroupItem = new SchemAction("MENU_UNGROUP", "empty", KeyStroke.getKeyStroke(KeyEvent.VK_U, menuMask), KeyEvent.VK_U);
		libraryItem = new SchemAction("MENU_LOAD_FROM_LIBRARY", "empty", KeyStroke.getKeyStroke(KeyEvent.VK_L, menuMask), KeyEvent.VK_L);
		updateLibraryItem = new SchemAction("MENU_LIBRARY_UPDATE", "empty", null, KeyEvent.VK_I);

		stackingMenu = new SchemAction("MENU_STACKING", "empty", null, KeyEvent.VK_S);
		toFrontItem = new SchemAction("MENU_TO_FRONT", "empty", null, KeyEvent.VK_F);
		toBackItem = new SchemAction("MENU_TO_BACK", "empty", null, KeyEvent.VK_B);

		autoNumberItem = new SchemAction("MENU_AUTO_NUMBER", "empty", null, null);

		String iconPath = "tool/";
		selectionToolItem = new SchemAction("MENU_TOOL_SELECTION", iconPath, "selecttool", null, null);
		zoomToolItem = new SchemAction("MENU_TOOL_ZOOM", iconPath, "zoomtool", null, null);
		libToolItem = new SchemAction("MENU_TOOL_LIBRARY", iconPath, "librarytool", null, null);
		pinToolItem = new SchemAction("MENU_TOOL_PIN", iconPath, "pintool", null, null);
		textToolItem = new SchemAction("MENU_TOOL_TEXT", iconPath, "texttool", null, null);
		arcToolItem = new SchemAction("MENU_TOOL_ARC", iconPath, "circletool", null, null);
		squareToolItem = new SchemAction("MENU_TOOL_SQUARE", iconPath, "squaretool", null, null);
		lineToolItem = new SchemAction("MENU_TOOL_LINE", iconPath, "linetool", null, null);
		wireToolItem = new SchemAction("MENU_TOOL_WIRE", iconPath, "wiretool", null, null);
		polygonToolItem = new SchemAction("MENU_TOOL_POLYGON", iconPath, "polygontool", null, null);
		newCornerToolItem = new SchemAction("MENU_TOOL_INSERT_CORNER", iconPath, "newcornertool", null, null);

		rotateItem = new SchemAction("MENU_ROTATE_ALL_LEFT", "rotate", KeyStroke.getKeyStroke(KeyEvent.VK_R, menuMask), KeyEvent.VK_R);
		rotateNoTextItem = new SchemAction("MENU_ROTATE_BODY_LEFT", "rotatenotext", null, KeyEvent.VK_T);
		flipLeftRightItem = new SchemAction(Orientation.FLIP_LEFT_RIGHT.name, "flipleftright", null, KeyEvent.VK_H);
		flipTopBottomItem = new SchemAction(Orientation.FLIP_TOP_BOTTOM.name, "fliptopbottom", null, KeyEvent.VK_V);
		flipLeftRightNoTextItem = new SchemAction("MENU_FLIP_BODY_HORIZONTALLY", "flipleftrightnotext", null, KeyEvent.VK_L);
		flipTopBottomNoTextItem = new SchemAction("MENU_FLIP_BODY_VERTICALLY", "fliptopbottomnotext", null, KeyEvent.VK_B);
	}

	public static final SchemActions instance() {
		if(instance == null) {
			instance = new SchemActions();
		}
		return instance;
	}

	@Override
	protected void setActionStates(ActionStateInfos stateInfos) {
		try {
			int numSheets = ((Integer) stateInfos.get(STATE_INFO_NUM_SHEETS));
			boolean canMoveLeft = ((Boolean) stateInfos.get(STATE_INFO_CAN_MOVE_LEFT));
			boolean canMoveRight = ((Boolean) stateInfos.get(STATE_INFO_CAN_MOVE_RIGHT));
			boolean twoSheetsOrMore = (numSheets > 1);
			deleteSheetItem.setEnabled(twoSheetsOrMore);
			sheetLeftItem.setEnabled(twoSheetsOrMore && canMoveLeft);
			sheetRightItem.setEnabled(twoSheetsOrMore && canMoveRight);
		} catch(Exception e) {
		}

		try {
			int numSelected = ((Integer) stateInfos.get(ActionStateInfos.STATE_INFO_NUM_SELECTED));
			int numGroups = ((Integer) stateInfos.get(STATE_INFO_NUM_GROUPS));
			int numPins = ((Integer) stateInfos.get(STATE_INFO_NUM_PINS));
			boolean one = (numSelected == 1);
			boolean oneOrMore = (numSelected > 0);
			boolean twoOrMore = (numSelected > 1);
			boolean oneGroup = one && (numGroups == 1);

			saveComponentItem.setEnabled(oneGroup);
			groupComponentItem.setEnabled(twoOrMore);
			groupSymbolItem.setEnabled(twoOrMore && (numPins == 1));
			ungroupItem.setEnabled(oneGroup);

			rotateItem.setEnabled(oneOrMore);
			rotateNoTextItem.setEnabled(oneOrMore);
			flipLeftRightItem.setEnabled(oneOrMore);
			flipTopBottomItem.setEnabled(oneOrMore);
			flipLeftRightNoTextItem.setEnabled(oneOrMore);
			flipTopBottomNoTextItem.setEnabled(oneOrMore);

			stackingMenu.setEnabled(one);
		} catch(Exception e) {
		}

		try {
			Document document = (Document) stateInfos.get(ActionStateInfos.STATE_INFO_ACTIVE_DOCUMENT);
			boolean ad = (document != null);
			exportSchematicsItem.setEnabled(ad);
			exportSheetItem.setEnabled(ad);
			newSheetItem.setEnabled(ad);
			deleteSheetItem.setEnabled(ad);
			renameSheetItem.setEnabled(ad);
			sheetSizeItem.setEnabled(ad);
			libraryItem.setEnabled(ad);
		} catch(Exception e) {
		}
	}

	//----------------------------------------------

	class SchemAction extends AbstractAction {

		public SchemAction(String text, String iconPath, String iconName, KeyStroke accelerator, Integer mnemonic) {
			super(Translator.translate(text), IconLoader.instance().loadIcon(iconPath + iconName + ".png"));
			if(accelerator != null) {
				putValue(ACCELERATOR_KEY, accelerator);
			}
			if(mnemonic != null) {
				putValue(MNEMONIC_KEY, mnemonic);
			}
			putValue(SHORT_DESCRIPTION, Translator.translate(text + "_TOOLTIP"));
			putValue(CURSOR_IMAGE_KEY, "icons/cursor/" + iconName + ".png");
		}

		public SchemAction(String text, String iconName, KeyStroke accelerator, Integer mnemonic) {
			this(text, "menu/", iconName, accelerator, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireAction(this);
		}

	}

}
