package org.heinz.eda.schem.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationListener;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.RecentMenuHelper;
import org.heinz.framework.crossplatform.utils.Translator;


public class SchemMenuBar extends JMenuBar implements PropertyChangeListener {
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu sheetMenu;
    private JMenu componentMenu;
    private JMenu helpMenu;
    
    private RecentMenuHelper recentMenuHelper;
    
    public SchemMenuBar(ApplicationListener applicationListener) {
        super();
        init();
        recentMenuHelper = new RecentMenuHelper(SchemOptions.instance(), fileMenu, applicationListener);
    }
    
    private void init() {
        SchemActions sa = SchemActions.instance();
        ApplicationActions aa = ApplicationActions.instance();
        
        Application application = CrossPlatform.getPlatform().getApplication();
        
        add(fileMenu = new JMenu(Translator.translate("MENU_FILE")));
        fileMenu.add(aa.newItem);
        fileMenu.add(aa.openItem);
        fileMenu.add(aa.saveItem);
        fileMenu.add(aa.saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(sa.exportSheetItem);
        fileMenu.add(sa.exportSchematicsItem);
        application.addCloseMenuItem(fileMenu, aa.closeItem, true);
        application.addQuitMenuItem(fileMenu, aa.exitItem, true);

        add(editMenu = new JMenu(Translator.translate("MENU_EDIT")));
        editMenu.add(aa.undoItem);
        editMenu.add(aa.redoItem);
        editMenu.addSeparator();
        editMenu.add(aa.cutItem);
        editMenu.add(aa.copyItem);
        editMenu.add(aa.pasteItem);
        editMenu.add(aa.duplicateItem);
        editMenu.add(aa.deleteItem);
        editMenu.add(aa.selectAllItem);
        editMenu.add(aa.deselectAllItem);
        editMenu.addSeparator();
        JMenu stackingMenu = new JMenu(sa.stackingMenu);
        stackingMenu.add(sa.toFrontItem);
        stackingMenu.add(sa.toBackItem);
        editMenu.add(stackingMenu);
        editMenu.addSeparator();
        editMenu.add(sa.autoNumberItem);
        editMenu.addSeparator();
        editMenu.add(aa.propertiesItem);
        
        add(viewMenu = new JMenu(Translator.translate("MENU_VIEW")));
        viewMenu.add(aa.zoomInItem);
        viewMenu.add(aa.zoomOutItem);
        viewMenu.add(aa.zoomFitItem);
       	application.addPreferencesMenuItem(viewMenu, aa.preferencesItem, true);
        
        add(sheetMenu = new JMenu(Translator.translate("MENU_SHEET")));
        sheetMenu.add(sa.newSheetItem);
        sheetMenu.add(sa.renameSheetItem);
        sheetMenu.add(sa.deleteSheetItem);
        sheetMenu.addSeparator();
        sheetMenu.add(sa.sheetLeftItem);
        sheetMenu.add(sa.sheetRightItem);
        sheetMenu.addSeparator();
        sheetMenu.add(sa.sheetSizeItem);
        
        add(componentMenu = new JMenu(Translator.translate("MENU_COMPONENT")));
        componentMenu.add(sa.rotateNoTextItem);
        componentMenu.add(sa.rotateItem);
        componentMenu.add(sa.flipLeftRightItem);
        componentMenu.add(sa.flipLeftRightNoTextItem);
        componentMenu.add(sa.flipTopBottomItem);
        componentMenu.add(sa.flipTopBottomNoTextItem);
        componentMenu.addSeparator();
        componentMenu.add(sa.groupComponentItem);
        componentMenu.add(sa.groupSymbolItem);
        componentMenu.add(sa.ungroupItem);
        componentMenu.addSeparator();
        componentMenu.add(sa.saveComponentItem);
        componentMenu.add(sa.libraryItem);
        componentMenu.addSeparator();
        componentMenu.add(sa.updateLibraryItem);
        
        JMenu windowMenu = application.getWindowMenu();
        add(windowMenu);
        
    	helpMenu = new JMenu(Translator.translate("MENU_HELP"));
        application.addAboutMenuItem(helpMenu, aa.aboutItem, false);
        if(helpMenu.getItemCount() > 0)
        	add(helpMenu);
        
        SchemOptions.instance().addPropertyChangeListener(this);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SchemOptions.PROPERTY_BASE_RECENT_FILE)) {
			recentMenuHelper.buildRecentMenu();
		}
	}
}
