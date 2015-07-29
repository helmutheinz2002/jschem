
package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.JMenuBar;

import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.EditToolBarFactory;
import org.heinz.framework.crossplatform.MenuBarFactory;
import org.heinz.framework.crossplatform.PlatformDefaults;
import org.heinz.framework.crossplatform.StatusBar;
import org.heinz.framework.crossplatform.StatusBarFactory;
import org.heinz.framework.crossplatform.ToolBar;
import org.heinz.framework.crossplatform.ToolBarFactory;
import org.heinz.framework.crossplatform.utils.MenuHelper;

public class AccessoryHelper {

	public static EditToolBar createEditToolBar(EditToolBarFactory factory, Container parent) {
		EditToolBar toolBar = factory.createEditToolBar();
		toolBar.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.PAGE_START;
		parent.add(toolBar, c);

		return toolBar;
	}

	public static StatusBar createStatusBar(StatusBarFactory factory, Container parent) {
		StatusBar statusBar = factory.createStatusBar();
		statusBar.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		int bottom = CrossPlatform.getPlatform().getPlatformDefaults().getInt(PlatformDefaults.BOTTOM_INSET);
		c.insets = new Insets(2, 2, bottom, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_END;
		parent.add(statusBar, c);

		return statusBar;
	}

	public static ToolBar createToolBar(ToolBarFactory factory, Container parent) {
		ToolBar toolBar = factory.createToolBar();
		toolBar.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_END;
		parent.add(toolBar, c);

		return toolBar;
	}

	public static JMenuBar createMenuBar(MenuBarFactory menuBarFactory, boolean withMenuDisable) {
		JMenuBar menuBar = menuBarFactory.createMenuBar();
		MenuHelper.cleanMenus(menuBar, false, false, false);
		if(withMenuDisable) {
			MenuHelper.activateMenuDisabledState(menuBar);
		}
		menuBar.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		return menuBar;
	}

}
