
package org.heinz.framework.crossplatform.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;

import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;

public class MenuHelper {

	private static final Map actionListenerByAction = new HashMap();

	public static JMenuItem addMenuItem(JMenu menu, Action action, boolean separatorBefore, final Runnable runnable) {
		return addMenuItem(menu, action, separatorBefore, false, runnable);
	}

	public static JMenuItem addMenuItem(JMenu menu, final Action action, boolean separatorBefore, boolean separatorAfter, final Runnable runnable) {
		JMenuItem item = new JMenuItem(action);
		ActionListener actionListener = (ActionListener) actionListenerByAction.get(action);
		if(actionListener == null) {
			actionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getSource() == action) {
						runnable.run();
					}
				}

			};
			actionListenerByAction.put(action, actionListener);
		}
		ApplicationActions.instance().addActionListener(actionListener);
		if(separatorBefore) {
			menu.addSeparator();
		}

		menu.add(item);

		if(separatorAfter) {
			menu.addSeparator();
		}

		return item;
	}

	public static void cleanMenus(JMenuBar menuBar, boolean removeIcons, boolean removeAccelerators, boolean removeMnemonics) {
		int menus = menuBar.getMenuCount();
		for(int i = 0; i < menus; i++) {
			JMenu menu = menuBar.getMenu(i);
			cleanMenus(menu, removeIcons, removeAccelerators, removeMnemonics);
		}
	}

	private static void cleanMenus(JMenuItem item, boolean removeIcons, boolean removeAccelerators, boolean removeMnemonics) {
		item.setToolTipText(null);
		if(removeIcons && (item.getIcon() != null)) {
			item.setIcon(null);
		}
		if(removeAccelerators && (item.getAccelerator() != null)) {
			item.setAccelerator(null);
		}
		if(removeMnemonics && (item.getMnemonic() != 0)) {
			item.setMnemonic(0);
		}

		if(item instanceof JMenu) {
			JMenu menu = (JMenu) item;
			int items = menu.getItemCount();
			for(int i = 0; i < items; i++) {
				JMenuItem it = menu.getItem(i);
				if(it != null) {
					cleanMenus(it, removeIcons, removeAccelerators, removeMnemonics);
				}
			}
		}
	}

	public static void cleanPopupMenu(JPopupMenu popupMenu, boolean removeIcons, boolean removeAccelerators, boolean removeMnemonics) {
		MenuElement[] items = popupMenu.getSubElements();
		for(MenuElement item1 : items) {
			try {
				JMenuItem item = (JMenuItem) item1;
				cleanMenus(item, removeIcons, removeAccelerators, removeMnemonics);
			}catch(ClassCastException cex) {
				// no menu item
			}
		}
	}

	public static void activateMenuDisabledState(JMenuBar menuBar) {
		int menus = menuBar.getMenuCount();
		for(int i = 0; i < menus; i++) {
			JMenu menu = menuBar.getMenu(i);
			activateMenuDisabledState(menu);
		}
	}

	public static void activateMenuDisabledState(final JMenu menu) {
		int items = menu.getItemCount();
		for(int i = 0; i < items; i++) {
			JMenuItem it = menu.getItem(i);

			// Separator?
			if(it == null) {
				continue;
			}

			AccessibleContext ac = it.getAccessibleContext();
			ac.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(AccessibleContext.ACCESSIBLE_STATE_PROPERTY)) {
						boolean b = hasActiveEntries(menu);
						menu.setEnabled(b);
					}
				}

			});
		}
	}

	public static boolean hasActiveEntries(JMenu menu) {
		int items = menu.getItemCount();
		for(int i = 0; i < items; i++) {
			JMenuItem it = menu.getItem(i);
			if(it == null) {
				continue;
			}

			if(it.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public static void compressMenu(Container menu) {
		int numVisibleItems = 0;
		Component[] children = menu.getComponents();
		if(menu instanceof JMenu) {
			children = ((JMenu) menu).getMenuComponents();
		}
		// set menu items in/visible, set separators visible
		for(Component children1 : children) {
			if(children1 instanceof JMenu) {
				compressMenu((JMenu) children1);
			}
			if(children1 instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) children1;
				Action action = item.getAction();
				if(action != null) {
					item.setVisible(action.isEnabled());
				}
				if(item.isVisible()) {
					numVisibleItems++;
				}
			}
			if(children1 instanceof JSeparator) {
				((JSeparator) children1).setVisible(true);
			}
		}

		// suppress double separators
		int numSeparators = 0;
		boolean firstInMenu = true;
		boolean smallMenu = (numVisibleItems <= 2);
		for(Component children1 : children) {
			if(children1 instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) children1;
				if(item.isVisible()) {
					numSeparators = 0;
					firstInMenu = false;
				}
			}
			if(children1 instanceof JSeparator) {
				JSeparator sep = (JSeparator) children1;
				if((numSeparators > 0) || firstInMenu || smallMenu) {
					sep.setVisible(false);
				}
				numSeparators++;
			}
		}

		// remove trailing separator
		for(int i = children.length - 1; i >= 0; i--) {
			if(!children[i].isVisible()) {
				continue;
			}

			if(children[i] instanceof JSeparator) {
				children[i].setVisible(false);
			} else {
				break;
			}
		}

		if(menu instanceof JPopupMenu) {
			((JPopupMenu) menu).pack();
		}
	}

}
