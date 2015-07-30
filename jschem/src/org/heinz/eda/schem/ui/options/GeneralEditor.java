
package org.heinz.eda.schem.ui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.IntegerDocumentFilter;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;

public class GeneralEditor extends StandardDialogPanel {

	private final JTextField authorField;

	private final JTextField companyField;

	private final JTextField autoNumberPageOffsetField;

	public GeneralEditor() {
		super(Translator.translate("GENERAL"));
		setLayout(new GridBagLayout());

		autoNumberPageOffsetField = new JTextField();
		AbstractDocument doc = (AbstractDocument) autoNumberPageOffsetField.getDocument();
		doc.setDocumentFilter(new IntegerDocumentFilter());

		int row = 0;
		row = addField(Translator.translate("DESIGNER"), authorField = new JTextField(), SchemOptions.PROPERTY_AUTHOR, row);
		row = addField(Translator.translate("COMPANY_NAME"), companyField = new JTextField(), SchemOptions.PROPERTY_COMPANY, row);
		row = addField(Translator.translate("AUTONUMBER_PAGE_OFFSET"), autoNumberPageOffsetField, SchemOptions.PROPERTY_AUTONUMBER_PAGE_OFFSET, row);
		autoNumberPageOffsetField.setToolTipText(Translator.translate("AUTONUMBER_PAGE_OFFSET_TOOLTIP"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = row;
		c.gridx = 0;
		c.insets = new Insets(15, 5, 5, 5);
		c.gridwidth = 5;
		c.anchor = GridBagConstraints.CENTER;
		JButton defaultButton = new JButton(Translator.translate("DEFAULT_VALUES"));
		add(defaultButton, c);
		defaultButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setDefaults();
			}

		});
	}

	private int addField(String label, JTextField textField, String propertyName, int row) {
		textField.setText("" + SchemOptions.instance().getOption(propertyName));

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = row;
		c.gridx = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;

		add(new JLabel(label), c);

		c.gridx++;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		add(textField, c);

		return row + 1;
	}

	private void setDefaults() {
		autoNumberPageOffsetField.setText("" + SchemOptions.instance().getIntOption(SchemOptions.getDefaultOptionName(SchemOptions.PROPERTY_AUTONUMBER_PAGE_OFFSET)));
	}

	@Override
	public String check() {
		return null;
	}

	@Override
	public void ok() {
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_AUTHOR, authorField.getText());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_COMPANY, companyField.getText());
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_AUTONUMBER_PAGE_OFFSET, new Integer(autoNumberPageOffsetField.getText()));
	}

}
