
package org.heinz.eda.schem.ui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.SheetSize;
import org.heinz.eda.schem.ui.beans.UnitCombobox;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.AbstractOptions;

public class ViewEditor extends StandardDialogPanel {

	private JCheckBox snapGridButton;

	private UnitCombobox snapGridSizes;

	private JCheckBox showGridButton;

	private JCheckBox antiAliasingButton;

	private JCheckBox smartJunctionsButton;

	private JCheckBox smartJunctionsOutlineButton;

	private JCheckBox selectionContains;

	private UnitCombobox gridSizes;

	private JComboBox pageSizes;

	private final int[] DEFAULT_GRIDS = new int[]{
		10, 20, 25, 50,
		100, 200, 250, 500,
		1000, 2000, 2500, 5000
	};

	public ViewEditor() {
		super(Translator.translate("SHEET"));

		setLayout(new GridBagLayout());

		int r = 0;
		r = initUnitBox(showGridButton = new JCheckBox(Translator.translate("SHOW_GRID")),
				gridSizes = new UnitCombobox(), Translator.translate("VISIBLE_GRID_SPACING"), r);
		r = initUnitBox(snapGridButton = new JCheckBox(Translator.translate("SNAP_TO_GRID")),
				snapGridSizes = new UnitCombobox(), Translator.translate("SNAP_GRID_SPACING"), r);

		pageSizes = new JComboBox();
		for(SheetSize SIZES : SheetSize.SIZES) {
			pageSizes.addItem(SIZES);
		}

		antiAliasingButton = new JCheckBox(Translator.translate("USE_ANTI_ALIASING"));
		smartJunctionsButton = new JCheckBox(Translator.translate("SMART_JUNCTIONS"), true);
		smartJunctionsOutlineButton = new JCheckBox(Translator.translate("SMART_JUNCTIONS_OUTLINE"));
		selectionContains = new JCheckBox(Translator.translate("SELECTION_CONTAINS"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = r;
		c.gridx = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		add(new JLabel(Translator.translate("DEFAULT_SHEET_SIZE")), c);
		c.gridx++;
		c.fill = GridBagConstraints.BOTH;
		add(pageSizes, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		add(antiAliasingButton, c);

		c.gridy++;
		add(smartJunctionsButton, c);

		c.gridy++;
		add(smartJunctionsOutlineButton, c);

		smartJunctionsButton.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				smartJunctionsOutlineButton.setEnabled(smartJunctionsButton.isSelected());
			}

		});

		c.gridy++;
		add(selectionContains, c);

		JButton defaultButton = new JButton(Translator.translate("DEFAULT_VALUES"));
		c.fill = GridBagConstraints.NONE;
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		c.insets = new Insets(15, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		add(defaultButton, c);

		defaultButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractOptions o = SchemOptions.instance();
				snapGridSizes.setSelectedUnits(o.getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_GRID_SNAP_SPACING)));
				snapGridButton.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_GRID_SNAP)));
				gridSizes.setSelectedUnits(o.getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_GRID_SPACING)));
				showGridButton.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_GRID_VISIBLE)));
				int ss = SchemOptions.instance().getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_DEFAULT_SHEET_SIZE));
				pageSizes.setSelectedItem(SheetSize.SIZES[ss]);
				antiAliasingButton.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_ANTIALIASING)));
				smartJunctionsButton.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_SMART_JUNCTIONS)));
				smartJunctionsOutlineButton.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE)));
				selectionContains.setSelected(o.getBoolOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_SELECTION_CONTAINS)));
			}

		});

		snapGridSizes.setSelectedUnits(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING));
		snapGridButton.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_SNAP));
		gridSizes.setSelectedUnits(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SPACING));
		showGridButton.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_VISIBLE));
		int ss = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_DEFAULT_SHEET_SIZE);
		pageSizes.setSelectedItem(SheetSize.SIZES[ss]);
		antiAliasingButton.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_ANTIALIASING));
		smartJunctionsOutlineButton.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE));
		smartJunctionsButton.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SMART_JUNCTIONS));
		selectionContains.setSelected(SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_SELECTION_CONTAINS));
	}

	private int initUnitBox(final JCheckBox checkBox, final UnitCombobox unitBox, String labelText, int r) {
		for(int i = 0; i < DEFAULT_GRIDS.length; i++) {
			unitBox.addUnitsItem(DEFAULT_GRIDS[i]);
		}

		return init(checkBox, unitBox, labelText, r);
	}

	private int init(final JCheckBox checkBox, final JComponent subComponent, String labelText, int r) {
		final JLabel label = new JLabel(labelText);
		checkBox.setSelected(true);

		checkBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				subComponent.setEnabled(checkBox.isSelected());
				label.setEnabled(checkBox.isSelected());
			}

		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = r++;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		add(checkBox, c);

		c.gridy = r++;
		add(label, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 10, 5);
		add(subComponent, c);

		return r;
	}

	@Override
	public String check() {
		return null;
	}

	@Override
	public void ok() {
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP, snapGridButton.isSelected());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING, snapGridSizes.getSelectedUnits());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_VISIBLE, showGridButton.isSelected());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SPACING, gridSizes.getSelectedUnits());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_DEFAULT_SHEET_SIZE, ((SheetSize) (pageSizes.getSelectedItem())).key);
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_ANTIALIASING, antiAliasingButton.isSelected());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_SMART_JUNCTIONS, smartJunctionsButton.isSelected());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_SMART_JUNCTIONS_OUTLINE, smartJunctionsOutlineButton.isSelected());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_SELECTION_CONTAINS, selectionContains.isSelected());
	}

}
