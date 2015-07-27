package org.heinz.eda.schem.ui.beans;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import org.heinz.eda.schem.model.ArcType;
import org.heinz.eda.schem.util.UnitConverter;
import org.heinz.eda.schem.util.UnitDocumentFilter;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class ArcBean extends PropertyBean {
	private int arcType;
	private final JToggleButton[] arcButtons;
	public final JTextField radiusField;
	
	public ArcBean() {
		ButtonGroup group = new ButtonGroup();
		arcButtons = new JToggleButton[ArcType.ARC_TYPES.length];
		
		for(int i=0; i<ArcType.ARC_TYPES.length; i++) {
			ArcType at = ArcType.ARC_TYPES[i];
			JToggleButton tb = new JToggleButton(IconLoader.instance().loadIcon("menu/" + at.icon));
			tb.setToolTipText(Translator.translate(at.name));
			group.add(tb);
			arcButtons[i] = tb;
			
			final int fi = i;
			tb.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					arcType = fi;
				}
			});
		}
		
		setArcType(ArcType.ARC_FULL);
		
		radiusField = new JTextField();
		AbstractDocument doc = (AbstractDocument) radiusField.getDocument();
		doc.setDocumentFilter(new UnitDocumentFilter());
	}

	public List getGuiElements(boolean withRadius) {
		List ret = new ArrayList();
		ret.addAll(Arrays.asList(arcButtons));
		if(withRadius)
			ret.add(radiusField);
		
		return ret;
	}
	
	public void setRadius(int radius) {
		radiusField.setText(UnitConverter.getStringValue(radius));
	}
	
	public int getRadius() {
		return UnitConverter.getUnitValue(radiusField.getText());
	}
	
	public void setArcType(int arcType) {
		arcButtons[arcType].setSelected(true);
	}
	
	public int getArcType() {
		return arcType;
	}
	
	public int addTo(JComponent parent, int startRow) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = DEFAULT_INSETS;
		c.gridy = startRow;
		c.gridx = 0;
		
		c.gridwidth = 1;
		parent.add(new JLabel(Translator.translate("ARC_TYPE")), c);
		for(int i=0; i<ArcType.ARC_TYPES.length; i++) {
			c.gridx++;
			parent.add(arcButtons[i], c);
		}
		c.gridy++;
		
		c.gridx = 0;
		c.gridwidth = 1;
		parent.add(new JLabel(Translator.translate("ARC_RADIUS")), c);
		c.gridx++;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		parent.add(radiusField, c);
		c.gridy++;
		
		return c.gridy;
	}
}
