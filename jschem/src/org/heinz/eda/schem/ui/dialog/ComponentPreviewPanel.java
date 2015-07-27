package org.heinz.eda.schem.ui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.util.ExtRect;

public class ComponentPreviewPanel extends JPanel {
	private AbstractComponent component;
	private double zoom = -1;
	
	public ComponentPreviewPanel() {
		super();
		setOpaque(true);
		setBackground(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_SHEET_COLOR));
		setPreferredSize(new Dimension(300, 300));
		Border ib = BorderFactory.createLineBorder(Color.gray);
		setBorder(ib);
	}
	
	public void showComponent(AbstractComponent c) {
		component = c;
		zoom = -1;
		if(component != null)
			component.setPosition(0, 0);
		repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if(component == null)
			return;
		
		if(zoom < 0) {
			BufferedImage bi = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
			Graphics bg = bi.getGraphics();
			component.drawAll(bg, g.getClipBounds(), 0.03, false);
			bg.dispose();
			ExtRect r = component.getBoundingBox();
			
			Dimension d = getSize();
			int dx = d.width * 9 / 10;
			int dy = d.height * 9 / 10;
			
			double zx = Math.abs((double) dx  / (double) r.width);
			double zy = Math.abs((double) dy / (double) r.height);
			zoom = zx;
			if(zy < zoom)
				zoom = zy;
			
			int rx = ((int) ((double) d.width / zoom) - r.width) / 2; 
			int ry = ((int) ((double) d.height / zoom) - r.height) / 2; 
			
			component.setPosition(rx - r.x, ry - r.y);
			repaint();
			return;
		}
		
		component.drawAll(g, g.getClipBounds(), zoom, false);
	}
}
