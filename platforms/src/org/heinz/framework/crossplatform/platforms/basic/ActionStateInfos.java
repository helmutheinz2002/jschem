package org.heinz.framework.crossplatform.platforms.basic;

import java.util.HashMap;

public class ActionStateInfos extends HashMap {
	public static final String STATE_INFO_NUM_SELECTED = "ApplicationActions.numSelected";
	public static final String STATE_INFO_ACTIVE_DOCUMENT = "ApplicationActions.activeDocument";
	public static final String STATE_INFO_CAN_UNDO = "ApplicationActions.canUndo";
	public static final String STATE_INFO_CAN_REDO = "ApplicationActions.canRedo";
	public static final String STATE_INFO_UNDO_ACTION = "ApplicationActions.undoAction";
	public static final String STATE_INFO_REDO_ACTION = "ApplicationActions.redoAction";
	public static final String STATE_INFO_NUM_OBJECTS_TO_SELECT = "ApplicationActions.numObjectsToSelect";
	
	public Object put(Object key, Object val) {
		if(get(key) != null)
			throw new IllegalArgumentException("Property '" + key + "' already defined");
		return super.put(key, val);
	}
	
	public boolean getBool(Object key) {
		try {
			return ((Boolean) super.get(key)).booleanValue();
		} catch(Exception ex) {
		}
		return false;
	}
}
