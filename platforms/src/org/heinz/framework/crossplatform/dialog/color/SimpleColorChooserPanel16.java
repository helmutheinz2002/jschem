package org.heinz.framework.crossplatform.dialog.color;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.heinz.framework.crossplatform.utils.Translator;

public class SimpleColorChooserPanel16 extends AbstractColorChooserPanel {
	private static final Color[] COLORS = {
		Color.black,
		Color.gray,
		Color.lightGray,
		Color.white,
		
		new Color(170, 0, 0),
		Color.red,
		new Color(255, 100, 100),
		//Color.orange,
		Color.magenta,
		
		new Color(0, 0, 170),
		Color.blue,
		new Color(120, 200, 255),
		Color.cyan,
		
		new Color(0, 170, 0),
		Color.green,
		new Color(130, 255, 130),
		Color.yellow,
	};
	
	protected void buildChooser() {
		add(new SimpleColorChooserSwatchPanel(COLORS, 4, 4, 20) {
			protected void colorChanged(Color color) {
				getColorSelectionModel().setSelectedColor(color);
			}
		});
	}
	
	public String getDisplayName() {
		return Translator.translate("16_COLORS");
	}

	public Icon getLargeDisplayIcon() {
		return null;
	}

	public Icon getSmallDisplayIcon() {
		return null;
	}

	public void updateChooser() {
	}
}
