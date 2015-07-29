
package org.heinz.framework.crossplatform.dialog.color;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.heinz.framework.crossplatform.utils.Translator;

public class SimpleColorChooserPanel64 extends AbstractColorChooserPanel {

	private static final Color[] COLORS = buildColorMap();

	@Override
	protected void buildChooser() {
		add(new SimpleColorChooserSwatchPanel(COLORS, 8, 8, 20) {

			@Override
			protected void colorChanged(Color color) {
				getColorSelectionModel().setSelectedColor(color);
			}

		});
	}

	@Override
	public String getDisplayName() {
		return Translator.translate("64_COLORS");
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public void updateChooser() {
	}

	private static Color[] buildColorMap() {
		double fact = 255.0 / 3.0;
		Color[] colors = new Color[64];
		for(int i = 0; i < 64; i++) {
			double r = (i & 3);
			double g = ((i >> 2) & 3);
			double b = ((i >> 4) & 3);
			colors[i] = new Color((int) (r * fact), (int) (g * fact), (int) (b * fact));
		}
		return colors;
	}

}
