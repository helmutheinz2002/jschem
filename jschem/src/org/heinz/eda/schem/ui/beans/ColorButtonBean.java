package org.heinz.eda.schem.ui.beans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.heinz.framework.crossplatform.dialog.color.SimpleColorChooser;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class ColorButtonBean extends PropertyBean implements ActionListener {
	private static final int BAR_SIZE = 4;
	private JButton button;
	private BufferedImage image;
	private Image baseImage;
	private Color color;
	private JPopupMenu popupMenu;
	private JMenuItem pickColorItem;
	private JMenuItem transparentItem;
	private String dialogTitle;
	
	public ColorButtonBean(String baseImageName, String title, String toolTip, final boolean allowTransparent) {
		this.dialogTitle = Translator.translate(title);
		
		popupMenu = new JPopupMenu();
		popupMenu.add(pickColorItem = new JMenuItem(Translator.translate("PICK_COLOR")));
		popupMenu.add(transparentItem = new JMenuItem(Translator.translate("TRANSPARENT")));
		pickColorItem.addActionListener(this);
		transparentItem.addActionListener(this);
		
		ImageIcon icon = (ImageIcon) IconLoader.instance().loadIcon("menu/" + baseImageName);
		baseImage = icon.getImage();
		button = new JButton();
		button.setToolTipText(Translator.translate(toolTip));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(allowTransparent) {
					openPopup();
				} else {
					Color c = SimpleColorChooser.showColorDialog(button, dialogTitle, color);
					if(c != null)
						setColor(c);
				}
			}
		});
		createImage();
	}
	
	public int addTo(JComponent parent, int startRow) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = startRow;
		c.insets = DEFAULT_INSETS;
		parent.add(button, c);
		return c.gridy + 1;
	}
	
	private void openPopup() {
		Component parent = button.getParent();
		Point p = button.getLocation();
		Dimension d = button.getSize();
		popupMenu.show(parent, p.x, p.y + d.height);
	}
	
	public JButton getButton() {
		return button;
	}
	
	public void setColor(Color color) {
		this.color = color;
		createImage();
	}
	
	public Color getColor() {
		return color;
	}
	
	private void createImage() {
		int w = baseImage.getWidth(null);
		int h = baseImage.getHeight(null);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		if(color == null) {
			g.setColor(Color.black);
			g.drawRect(0, h - BAR_SIZE, w - 1, BAR_SIZE - 1);
		} else {
			g.setColor(color);
			g.fillRect(0, h - BAR_SIZE, w, BAR_SIZE);
		}
		g.drawImage(baseImage, 0, 0, null);
		g.dispose();
		
		button.setIcon(new ImageIcon(image));
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pickColorItem) {
			Color c = SimpleColorChooser.showColorDialog(button, dialogTitle, color);
			if(c != null)
				setColor(c);
		} else {
			setColor(null);
		}
	}
}
