package org.heinz.framework.crossplatform.dialog.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class SimpleColorChooser extends JColorChooser {
	public SimpleColorChooser() {
		super(Color.white);
	}

	public SimpleColorChooser(Color initial) {
		super(initial);
		AbstractColorChooserPanel[] panels = getChooserPanels();
		AbstractColorChooserPanel[] simplePanels = { new SimpleColorChooserPanel16(), new SimpleColorChooserPanel64() };
		
		AbstractColorChooserPanel[] newPanels = new AbstractColorChooserPanel[panels.length + simplePanels.length];
		
		for(int i=0; i<simplePanels.length; i++)
			newPanels[i] = simplePanels[i];
		
		for(int i=0; i<panels.length; i++)
			newPanels[i+simplePanels.length] = panels[i];
		setChooserPanels(newPanels);
	}

	public static Color showColorDialog(Component component, String title, Color initialColor) throws HeadlessException {

		final JColorChooser pane = new SimpleColorChooser(initialColor != null ? initialColor : Color.white);

		final Color[] selection = new Color[1];
		ActionListener okListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection[0] = pane.getColor();
			}
		};
		component = SwingUtilities.getWindowAncestor(component);
		// Java bug: parent container must be null, otherwise things are unpredictable  
		JDialog dialog = createDialog(null, title, true, pane, okListener, null);

		dialog.setVisible(true);

		return selection[0];
	}
}
