package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;

import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.utils.ApplicationUndoListener;
import org.heinz.framework.crossplatform.utils.ApplicationUndoManager;
import org.heinz.framework.crossplatform.utils.DocumentUndoManager;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.clipboard.SystemClipBoard;
import org.heinz.framework.utils.clipboard.SystemClipboardListener;


public class ApplicationActions extends AbstractActions implements ActionListener, SystemClipboardListener, ApplicationUndoListener {
	public static final String DEFAULT_ICON_PATH = "data/icons/menu/";
	
	private static ApplicationActions instance;
    
    public final ApplicationAction fileMenu;
    public final ApplicationAction viewMenu;
    public final ApplicationAction editMenu;
    public final ApplicationAction helpMenu;
    public final ApplicationAction windowMenu;
    
    public final ApplicationAction newItem;
    public final ApplicationAction openItem;
    public final ApplicationAction saveItem;
    public final ApplicationAction saveAsItem;
    public final ApplicationAction closeItem;
    public final ApplicationAction exitItem;
    public final ApplicationAction printItem;
    
    public final ApplicationAction undoItem;
    public final ApplicationAction redoItem;
    public final ApplicationAction cutItem;
    public final ApplicationAction copyItem;
    public final ApplicationAction pasteItem;
    public final ApplicationAction duplicateItem;
    public final ApplicationAction deleteItem;
    public final ApplicationAction selectAllItem;
    public final ApplicationAction deselectAllItem;
    public final ApplicationAction propertiesItem;
    
    public final ApplicationAction zoomInItem;
    public final ApplicationAction zoomOutItem;
    public final ApplicationAction zoomFitItem;
    public final ApplicationAction preferencesItem;

    public final ApplicationAction tutorialItem;
    public final ApplicationAction aboutItem;
    
    public final ApplicationAction nextWindowItem;
    public final ApplicationAction prevWindowItem;
    public final ApplicationAction miniWindowItem;
    public final ApplicationAction maxiWindowItem;
    public final ApplicationAction allToFrontItem;
    
    private List stateInfoProviders = new ArrayList();
    
    public ApplicationActions() {
    	super();

    	if(instance != null)
    		throw new IllegalStateException("Instance already set");
    	
    	instance = this;
    	addCustomActions(this);
    	
    	int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    	
    	newItem = createAction("MENU_ITEM_NEW", DEFAULT_ICON_PATH + "filenew", KeyStroke.getKeyStroke(KeyEvent.VK_N, menuMask), new Integer(KeyEvent.VK_N), this);
    	openItem = createAction("MENU_ITEM_OPEN", DEFAULT_ICON_PATH + "fileopen", KeyStroke.getKeyStroke(KeyEvent.VK_O, menuMask), new Integer(KeyEvent.VK_O), this);
    	saveItem = createAction("MENU_ITEM_SAVE", DEFAULT_ICON_PATH + "filesave", KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask), new Integer(KeyEvent.VK_S), this);
    	saveAsItem = createAction("MENU_ITEM_SAVE_AS", DEFAULT_ICON_PATH + "filesaveas", null, new Integer(KeyEvent.VK_A), this);
        exitItem = createAction("MENU_ITEM_EXIT", DEFAULT_ICON_PATH + "exit", null, new Integer(KeyEvent.VK_X), this);
        closeItem = createAction("MENU_ITEM_CLOSE", DEFAULT_ICON_PATH + "fileclose", null, new Integer(KeyEvent.VK_C), this);
        printItem = createAction("MENU_ITEM_PRINT", DEFAULT_ICON_PATH + "fileprint", null, new Integer(KeyEvent.VK_P), this);

        undoItem = createAction("MENU_ITEM_UNDO", DEFAULT_ICON_PATH + "undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask), new Integer(KeyEvent.VK_U), this);
        redoItem = createAction("MENU_ITEM_REDO", DEFAULT_ICON_PATH + "redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuMask), new Integer(KeyEvent.VK_R), this);
        cutItem = createAction("MENU_ITEM_CUT", DEFAULT_ICON_PATH + "editcut", KeyStroke.getKeyStroke(KeyEvent.VK_X, menuMask), new Integer(KeyEvent.VK_T), this);
        copyItem = createAction("MENU_ITEM_COPY", DEFAULT_ICON_PATH + "editcopy", KeyStroke.getKeyStroke(KeyEvent.VK_C, menuMask), new Integer(KeyEvent.VK_C), this);
        pasteItem = createAction("MENU_ITEM_PASTE", DEFAULT_ICON_PATH + "editpaste", KeyStroke.getKeyStroke(KeyEvent.VK_V, menuMask), new Integer(KeyEvent.VK_P), this);
        duplicateItem = createAction("MENU_ITEM_DUPLICATE", DEFAULT_ICON_PATH + "editduplicate", null, new Integer(KeyEvent.VK_D), this);
        selectAllItem = createAction("MENU_ITEM_SELECT_ALL", DEFAULT_ICON_PATH + "empty", KeyStroke.getKeyStroke(KeyEvent.VK_A, menuMask), new Integer(KeyEvent.VK_S), this);
        deselectAllItem = createAction("MENU_ITEM_DESELECT_ALL", DEFAULT_ICON_PATH + "empty", KeyStroke.getKeyStroke(KeyEvent.VK_D, menuMask), new Integer(KeyEvent.VK_E), this);
        deleteItem = createAction("MENU_ITEM_DELETE", DEFAULT_ICON_PATH + "editdelete", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new Integer(KeyEvent.VK_D), this);
        propertiesItem = createAction("MENU_ITEM_PROPERTIES", DEFAULT_ICON_PATH + "edit", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), new Integer(KeyEvent.VK_P), this);

        zoomInItem = createAction("MENU_ITEM_ZOOM_IN", DEFAULT_ICON_PATH + "viewmag+", KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, menuMask), new Integer(KeyEvent.VK_I), this);
        zoomOutItem = createAction("MENU_ITEM_ZOOM_OUT", DEFAULT_ICON_PATH + "viewmag-", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, menuMask), new Integer(KeyEvent.VK_O), this);
        zoomFitItem = createAction("MENU_ITEM_ZOOM_TO_FIT", DEFAULT_ICON_PATH + "viewmagfit", KeyStroke.getKeyStroke(KeyEvent.VK_0, menuMask), new Integer(KeyEvent.VK_F), this);
        preferencesItem = createAction("MENU_ITEM_PREFERENCES", DEFAULT_ICON_PATH + "configure", KeyStroke.getKeyStroke(KeyEvent.VK_O, menuMask), new Integer(KeyEvent.VK_P), this);

        aboutItem = createAction("MENU_ITEM_ABOUT", DEFAULT_ICON_PATH + "info", null, new Integer(KeyEvent.VK_A), this);
        tutorialItem = createAction("MENU_ITEM_TUTORIAL", DEFAULT_ICON_PATH + "empty", null, new Integer(KeyEvent.VK_T), this);
        
        nextWindowItem = createAction("MENU_ITEM_NEXT_WINDOW", DEFAULT_ICON_PATH + "empty", null, new Integer(KeyEvent.VK_N), this);
        prevWindowItem = createAction("MENU_ITEM_PREV_WINDOW", DEFAULT_ICON_PATH + "empty", null, new Integer(KeyEvent.VK_P), this);
        miniWindowItem = createAction("MENU_ITEM_MINIMIZE_WINDOW", DEFAULT_ICON_PATH + "empty", KeyStroke.getKeyStroke(KeyEvent.VK_M, menuMask), new Integer(KeyEvent.VK_M), this);
        maxiWindowItem = createAction("MENU_ITEM_MAXIMIZE_WINDOW", DEFAULT_ICON_PATH + "empty", null, new Integer(KeyEvent.VK_X), this);
        allToFrontItem = createAction("MENU_ITEM_ALL_TO_FRONT", DEFAULT_ICON_PATH + "empty", null, new Integer(KeyEvent.VK_A), this);

        fileMenu = createAction("MENU_FILE", null, null, null, this);
        viewMenu = createAction("MENU_VIEW", null, null, null, this);
        editMenu = createAction("MENU_EDIT", null, null, null, this);
        helpMenu = createAction("MENU_HELP", null, null, null, this);
        windowMenu = createAction("MENU_WINDOW", null, null, null, this);
        
        SystemClipBoard.abstractInstance().addClipboardListener(this);
        
		undoItem.setEnabled(false);
		redoItem.setEnabled(false);
		
        clipboardChanged(SystemClipBoard.abstractInstance().canPaste());
        
        addStateInfoProvider(new UndoInfoProvider());
        ApplicationUndoManager.instance().addApplicationUndoListener(this);
    }
    
    public static final ApplicationActions instance() {
        return instance;
    }
    
    public void addStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
    	if(!stateInfoProviders.contains(stateInfoProvider))
    		stateInfoProviders.add(stateInfoProvider);
    }
    
    public void removeStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
   		stateInfoProviders.remove(stateInfoProvider);
    }
    
    private ActionStateInfos getStateInfos() {
    	ActionStateInfos stateInfos = new ActionStateInfos();
    	
    	for(Iterator it=stateInfoProviders.iterator(); it.hasNext();) {
    		ActionStateInfoProvider sp = (ActionStateInfoProvider) it.next();
    		sp.addActionStateInfos(stateInfos);
    	}
    	
    	return stateInfos;
    }
    
    public void setActionStates() {
    	ActionStateInfos stateInfos = getStateInfos();
    	if(stateInfos != null)
    		setCustomActionStates(stateInfos);
    }
    
	public void actionPerformed(ActionEvent e) {
		fireAction((Action) e.getSource());
	}

	public void clipboardChanged(boolean canPaste) {
        pasteItem.setEnabled(canPaste);
	}

	public void undoableEditHappened(Document document, UndoableEditEvent e) {
		setActionStates();
	}

	protected void setActionStates(ActionStateInfos stateInfos) {
		Integer ns = (Integer) stateInfos.get(ActionStateInfos.STATE_INFO_NUM_SELECTED);
		if(ns != null) {
			int numSelected = ns.intValue();
	        boolean one = (numSelected == 1);
	        boolean oneOrMore = (numSelected > 0);
	        
	        cutItem.setEnabled(oneOrMore);
	        copyItem.setEnabled(oneOrMore);
	        duplicateItem.setEnabled(oneOrMore);
	        deleteItem.setEnabled(oneOrMore);
	        propertiesItem.setEnabled(one);
	        deselectAllItem.setEnabled(oneOrMore);
		}
		
		Document document = (Document) stateInfos.get(ActionStateInfos.STATE_INFO_ACTIVE_DOCUMENT);
		boolean b = (document != null);
		Integer objectsToSelect = (Integer) stateInfos.get(ActionStateInfos.STATE_INFO_NUM_OBJECTS_TO_SELECT);
		int numObjectsToSelect = (objectsToSelect != null) ? objectsToSelect.intValue() : 1;
		saveItem.setEnabled(b);
		saveAsItem.setEnabled(b);
		selectAllItem.setEnabled(b && (numObjectsToSelect > 0));
		zoomFitItem.setEnabled(b);
		zoomInItem.setEnabled(b);
		zoomOutItem.setEnabled(b);
		closeItem.setEnabled(b);
		
		undoItem.setEnabled(stateInfos.getBool(ActionStateInfos.STATE_INFO_CAN_UNDO));
		redoItem.setEnabled(stateInfos.getBool(ActionStateInfos.STATE_INFO_CAN_REDO));
		undoItem.putValue(Action.SHORT_DESCRIPTION, stateInfos.get(ActionStateInfos.STATE_INFO_UNDO_ACTION));
		redoItem.putValue(Action.SHORT_DESCRIPTION, stateInfos.get(ActionStateInfos.STATE_INFO_REDO_ACTION));
	}
	
	//------------------------------------------------------------
	
	private class UndoInfoProvider implements ActionStateInfoProvider {
		public void addActionStateInfos(ActionStateInfos stateInfos) {
			DocumentUndoManager undoManager = null;
			try {
				undoManager = ApplicationUndoManager.instance().getCurrentUndoManager();
			} catch(Exception ex) {
				return;
			}
			
			boolean canUndo = undoManager.canUndo();
			String undoAction = Translator.translate("MENU_ITEM_UNDO");
			if(undoManager.canUndo())
				undoAction = undoManager.getUndoPresentationName();
			else {
				try {
					undoAction = Translator.translate("MENU_ITEM_UNDO_TOOLTIP");
				} catch(Exception ex) {
				}
			}
			
			boolean canRedo = undoManager.canRedo();
			String redoAction = Translator.translate("MENU_ITEM_REDO");
			if(undoManager.canRedo())
				redoAction = undoManager.getRedoPresentationName();
			else {
				try {
					redoAction = Translator.translate("MENU_ITEM_REDO_TOOLTIP");
				} catch(Exception ex) {
				}
			}

			stateInfos.put(ActionStateInfos.STATE_INFO_CAN_UNDO, new Boolean(canUndo));
			stateInfos.put(ActionStateInfos.STATE_INFO_CAN_REDO, new Boolean(canRedo));
			stateInfos.put(ActionStateInfos.STATE_INFO_UNDO_ACTION, undoAction);
			stateInfos.put(ActionStateInfos.STATE_INFO_REDO_ACTION, redoAction);
		}
	}
}
