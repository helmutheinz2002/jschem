
package org.heinz.eda.schem.ui.tools;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.heinz.eda.schem.model.Library;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.util.ComponentFileFilter;

public class LibraryTool extends SchemEditTool {

	private static final int MAX_RECENT_COMPONENTS = 30;

	private final JComboBox components;

	private AbstractComponent newComponent;

	private File newCompFile;

	public LibraryTool() {
		super("librarytool.png");

		components = new JComboBox();
		components.setRenderer(new FileNameRenderer());
		buildComboBox();
		addToolbarObject(components);
	}

	private void buildComboBox() {
		components.removeAllItems();

		List compList = SchemOptions.instance().getListOption(SchemOptions.PROPERTY_BASE_RECENT_COMPONENT);
		for(Iterator it = compList.iterator(); it.hasNext();) {
			String s = (String) it.next();
			components.addItem(s);
		}
	}

	public void addToRecentComponents(File file) {
		List compList = SchemOptions.instance().getListOption(SchemOptions.PROPERTY_BASE_RECENT_COMPONENT);

		String path = file.getAbsolutePath();
		int idx = compList.indexOf(path);
		if(idx >= 0) {
			compList.remove(idx);
		}

		compList.add(0, path);
		if(compList.size() > MAX_RECENT_COMPONENTS) {
			compList = compList.subList(0, MAX_RECENT_COMPONENTS);
		}

		SchemOptions.instance().setOption(SchemOptions.PROPERTY_BASE_RECENT_COMPONENT, compList);
		buildComboBox();
	}

	@Override
	protected void handleCancel() {
		if(newComponent != null) {
			sheetPanel.getSheet().removeComponent(newComponent, true);
		}
	}

	@Override
	protected void handleMouseDown(MouseEvent e) {
		Point s = e.getPoint();
		Point p = sheetPanel.constrainScreenPoint(s.x, s.y, false);
		String compName = (String) components.getSelectedItem();

		newCompFile = new File(compName);
		newComponent = Library.loadComponent(newCompFile);
		newComponent.setPosition(p.x, p.y, false, true);
		sheetPanel.getSheet().addComponent(newComponent, true);
	}

	@Override
	protected void handleMouseDrag(MouseEvent e) {
		Point s = e.getPoint();
		Point p = sheetPanel.constrainScreenPoint(s.x, s.y, false);

		newComponent.setPosition(p.x, p.y, false, true);
	}

	@Override
	protected boolean handleKey(KeyEvent e) {
		return false;
	}

	@Override
	protected void handleMouseUp(MouseEvent e) {
		sheetPanel.getSheet().removeComponent(newComponent, false);
		sheetPanel.getSheet().addComponent(newComponent);
		addToRecentComponents(newCompFile);
		done();
	}

	@Override
	protected void done() {
		newComponent = null;
		newCompFile = null;
		super.done();
	}

	//----------------------------------------------------------------

	class FileNameRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			String s = getText();
			int idx = s.lastIndexOf(File.separator);
			s = s.substring(idx + 1);
			String ext = "." + ComponentFileFilter.COMPONENT_EXTENSION_JSCHEM;
			if(s.endsWith(ext)) {
				s = s.substring(0, s.length() - ext.length());
			}
			setText(s);
			return c;
		}

	}

}
