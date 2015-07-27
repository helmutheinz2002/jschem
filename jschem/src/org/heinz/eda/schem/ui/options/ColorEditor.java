package org.heinz.eda.schem.ui.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.dialog.color.SimpleColorChooser;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.AbstractOptions;

public class ColorEditor extends StandardDialogPanel {
	private List colors = new ArrayList();
	
	public ColorEditor() {
		super(Translator.translate("COLORS"));
		
		setLayout(new GridBagLayout());
		colors.add(new ColorInfo(SchemOptions.PROPERTY_COMPONENT_COLOR, "COMPONENTS"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_SELECTED_COLOR, "SELECTED_OBJECTS"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_SHEET_COLOR, "SHEET"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_PAGE_BORDER_COLOR, "SHEET_BORDER"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_SELECTION_BOX_COLOR, "SELECTION_FRAME"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_GRID_COLOR, "GRID"));
		colors.add(new ColorInfo(SchemOptions.PROPERTY_HANDLE_FILL_COLOR, "HANDLES"));
		
		int r = 0;
		for(Iterator it=colors.iterator(); it.hasNext(); r++) {
			final ColorInfo ci = (ColorInfo) it.next();
			ci.panel = new JPanel();
			ci.panel.setOpaque(true);
			ci.panel.setBackground(ci.color);
			ci.panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			ci.panel.setPreferredSize(new Dimension(16, 16));
			
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = r;
			c.insets = new Insets(5, 5, 5, 5);
			add(ci.panel, c);
			
			final String cn = Translator.translate(ci.displayName);
			JButton b = new JButton(cn);
			c.gridx = 1;
			c.fill = GridBagConstraints.BOTH;
			add(b, c);
			
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color col = SimpleColorChooser.showColorDialog(ColorEditor.this, cn, ci.color);
					if(col != null) {
						ci.color = col;
						ci.panel.setBackground(col);
					}
				}
			});
		}

		JButton defaults = new JButton(Translator.translate("DEFAULT_COLORS"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = r;
		c.gridwidth = 2;
		c.insets = new Insets(15, 5, 5, 5);
		add(defaults, c);
		
		final AbstractOptions o = SchemOptions.instance();
		defaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Iterator it=colors.iterator(); it.hasNext();) {
					final ColorInfo ci = (ColorInfo) it.next();
					Color col = o.getColorOption(SchemOptions.getDefaultOptionName(ci.optionName));
					ci.color = col;
					ci.panel.setBackground(col);
				}
			}
		});
	}

	public String check() {
		return null;
	}

	public void ok() {
		for(Iterator it=colors.iterator(); it.hasNext();) {
			ColorInfo ci = (ColorInfo) it.next();
			SchemOptions.instance().setOption(ci.optionName, ci.color);
		}
	}

	class ColorInfo {
		ColorInfo(String optionName, String displayName) {
			this.optionName = optionName;
			this.displayName = displayName;
			color = SchemOptions.instance().getColorOption(optionName);
		}

		String optionName;
		Color color;
		String displayName;
		JPanel panel;
	}
}
