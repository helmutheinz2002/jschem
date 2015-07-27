package org.heinz.eda.schem.ui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.ui.beans.FontBean;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;

public class FontEditor extends StandardDialogPanel {
	private FontBean fontBean;
	
	public FontEditor() {
		super(Translator.translate("FONTS"));
		setLayout(new GridBagLayout());

		fontBean = new FontBean();
		int row = fontBean.addTo(this, 0, false);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = row;
		c.gridx = 0;
		c.insets = new Insets(15, 5, 5, 5);
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JButton defaultButton = new JButton(Translator.translate("DEFAULT_FONT_SETTINGS"));
		add(defaultButton, c);
		defaultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDefaults();
			}
		});
		
		fontBean.setFontSize(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE));
		fontBean.setFontStyle(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_STYLE));
		fontBean.setFontName(SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME));
	}

	private void setDefaults() {
		fontBean.setFontSize(SchemOptions.instance().getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_TEXT_FONT_SIZE)));
		fontBean.setFontStyle(SchemOptions.instance().getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_TEXT_FONT_STYLE)));
		fontBean.setFontName(SchemOptions.instance().getStringOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_TEXT_FONT_NAME)));
	}

	public String check() {
		return null;
	}

	public void ok() {
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE, new Integer(fontBean.getFontSize()));
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_TEXT_FONT_STYLE, new Integer(fontBean.getFontStyle()));
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_TEXT_FONT_NAME, fontBean.getFontName());
	}
}
