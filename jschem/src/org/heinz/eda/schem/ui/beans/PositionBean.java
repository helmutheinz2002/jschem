
package org.heinz.eda.schem.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.heinz.eda.schem.util.UnitConverter;
import org.heinz.eda.schem.util.UnitDocumentFilter;
import org.heinz.framework.crossplatform.utils.Translator;

public class PositionBean extends PropertyBean {

	public final JTextField x;

	public final JTextField y;

	private String labelX;

	private String labelY;

	public PositionBean() {
		x = new JTextField();
		y = new JTextField();

		((AbstractDocument) x.getDocument()).setDocumentFilter(new UnitDocumentFilter());
		((AbstractDocument) y.getDocument()).setDocumentFilter(new UnitDocumentFilter());

		setLabels("POSITION_X", "POSITION_Y");
	}

	public final void setLabels(String lx, String ly) {
		labelX = Translator.translate(lx);
		labelY = Translator.translate(ly);
	}

	public Point getPosition() {
		int px = UnitConverter.getUnitValue(x.getText());
		int py = UnitConverter.getUnitValue(y.getText());
		return new Point(px, py);
	}

	public void setPosition(Point p) {
		x.setText(UnitConverter.getStringValue(p.x));
		y.setText(UnitConverter.getStringValue(p.y));
	}

	public int addTo(JComponent parent, int startRow) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = DEFAULT_INSETS;
		c.gridy = startRow;

		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(labelX), c);
		c.gridx++;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(x, c);
		c.gridy++;

		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(labelY), c);
		c.gridx++;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(y, c);
		c.gridy++;

		return c.gridy;
	}

}
