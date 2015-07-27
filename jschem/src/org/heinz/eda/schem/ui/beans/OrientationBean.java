package org.heinz.eda.schem.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class OrientationBean {
	protected int direction = Orientation.RIGHT.key;
	protected boolean flipH;
	protected boolean flipV;
	private JToggleButton[] orientationButtons;
	private JToggleButton flipLeftRightButton;
	private JToggleButton flipTopBottomButton;
	
	public OrientationBean() {
		ButtonGroup group = new ButtonGroup();
		orientationButtons = new JToggleButton[Orientation.DIRECTIONS.length];
		
		for(int i=0; i<Orientation.DIRECTIONS.length; i++) {
			final Orientation o = Orientation.DIRECTIONS[i];
			Icon icon = IconLoader.instance().loadIcon("menu/" + o.icon);
			JToggleButton tb = new JToggleButton(icon);
			tb.setToolTipText(Translator.translate(o.name));
			group.add(tb);
			orientationButtons[i] = tb;
	
			final int oi = i;
			tb.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					direction = oi;
				}
			});
		}
		
		flipLeftRightButton = new JToggleButton(IconLoader.instance().loadIcon("menu/flipleftright.png"));
		flipLeftRightButton.setToolTipText(Translator.translate(Orientation.FLIP_LEFT_RIGHT.name));
		flipLeftRightButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				flipH = flipLeftRightButton.isSelected();
			}
		});
		flipTopBottomButton = new JToggleButton(IconLoader.instance().loadIcon("menu/fliptopbottom.png"));
		flipTopBottomButton.setToolTipText(Translator.translate(Orientation.FLIP_TOP_BOTTOM.name));
		flipTopBottomButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				flipV = flipTopBottomButton.isSelected();
			}
		});
		
		setOrientation(Orientation.RIGHT);
	}
	
	public List getGuiElements() {
		List l = new ArrayList(Arrays.asList(orientationButtons));
		l.add(new JToolBar.Separator());
		l.add(flipLeftRightButton);
		l.add(flipTopBottomButton);
		return l;
	}
	
	public Orientation getOrientation() {
		return Orientation.getOrientation(direction, flipH, flipV);
	}
	
	public void setOrientation(Orientation orientation) {
		orientationButtons[orientation.getDirection().key].setSelected(true);
		flipLeftRightButton.setSelected(orientation.flipHorizontal);
		flipTopBottomButton.setSelected(orientation.flipVertical);
	}
	
	public int addTo(JComponent parent, int startRow, boolean withLabel) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridy = 0;
		c.gridx = 0;
		
		if(withLabel) {
			c.gridwidth = 1;
			c.fill = GridBagConstraints.BOTH;
			parent.add(new JLabel(Translator.translate("ORIENTATION")), c);
			c.gridx++;
		}
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		
		for(Iterator it=getGuiElements().iterator(); it.hasNext();) {
			JComponent jc = (JComponent) it.next();
			if(jc instanceof JSeparator)
				continue;
			parent.add(jc, c);
			c.gridx++;
		}
		
		if(!withLabel) {
			c.weightx = 1.0;
			parent.add(new JLabel());
		}
		
		c.gridy++;
		return c.gridy;
	}
}
