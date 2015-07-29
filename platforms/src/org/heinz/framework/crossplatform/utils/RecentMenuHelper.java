
package org.heinz.framework.crossplatform.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.heinz.framework.crossplatform.ApplicationListener;
import org.heinz.framework.utils.AbstractOptions;

public class RecentMenuHelper {

	private static final int MAX_FILENAME_LENGTH = 40;

	private final Map recentMenuItems = new HashMap();

	private final JMenu fileMenu;

	private final ApplicationListener applicationListener;

	private final AbstractOptions options;

	public RecentMenuHelper(AbstractOptions options, JMenu fileMenu, ApplicationListener applicationListener) {
		this.options = options;
		this.fileMenu = fileMenu;
		this.applicationListener = applicationListener;

		buildRecentMenu();
	}

	public final void buildRecentMenu() {
		List recentFiles = options.getListOption(AbstractOptions.PROPERTY_BASE_RECENT_FILE);
		for(Iterator it = recentMenuItems.keySet().iterator(); it.hasNext();) {
			Component item = (Component) it.next();
			fileMenu.remove(item);
		}
		recentMenuItems.clear();

		if(recentFiles.size() > 0) {
			fileMenu.addSeparator();
			Component sep = fileMenu.getMenuComponent(fileMenu.getMenuComponentCount() - 1);
			recentMenuItems.put(sep, "");
		}

		for(Iterator it = recentFiles.iterator(); it.hasNext();) {
			final String file = (String) it.next();
			JMenuItem item = new JMenuItem(getShortFilename(file));
			item.setToolTipText(file);
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					applicationListener.openFile(file);
				}

			});
			fileMenu.add(item);
			recentMenuItems.put(item, file);
		}
	}

	private String getShortFilename(String fn) {
		if(fn.length() < MAX_FILENAME_LENGTH) {
			return fn;
		}

		File f = new File(fn);
		String base = File.separator + f.getName();
		String path = "";
		try {
			path = f.getAbsolutePath().substring(0, MAX_FILENAME_LENGTH - 3 - base.length());
		} catch(Exception e) {
		}

		return path + "..." + base;
	}

}
