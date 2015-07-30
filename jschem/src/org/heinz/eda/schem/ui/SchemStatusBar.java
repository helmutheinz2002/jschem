
package org.heinz.eda.schem.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.framework.crossplatform.StatusBar;
import org.heinz.framework.crossplatform.utils.Translator;

public class SchemStatusBar extends StatusBar implements PropertyChangeListener, MouseMotionListener {

	private final JLabel snapGridInfoLabel;

	private final JLabel showGridInfoLabel;

	private final JLabel xInfoLabel;

	private final JLabel yInfoLabel;

	private final DecimalFormat format = new DecimalFormat("0.00");

	@SuppressWarnings("LeakingThisInConstructor")
	public SchemStatusBar() {
		snapGridInfoLabel = addInfo(false);
		setSnapGridInfoLabel("XXXXXX mm");
		setFixedSize(snapGridInfoLabel);
		showGridInfoLabel = addInfo(false);
		setShowGridInfoLabel("XXXXXX mm");
		setFixedSize(showGridInfoLabel);
		xInfoLabel = addInfo(false);
		yInfoLabel = addInfo(false);
		setPositionInfoLabel(new Point2D.Double(1000000, 1000000));
		setFixedSize(xInfoLabel);
		setFixedSize(yInfoLabel);
		addInfo(true);

		SchemOptions.instance().addPropertyChangeListener(this);
		setSnapGridInfoLabel(null);
		setShowGridInfoLabel(null);
		setPositionInfoLabel(new Point(0, 0));
	}

	private void setSnapGridInfoLabel(String info) {
		double i = (double) SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING);
		boolean b = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_SNAP);

		i = i / 100;
		String snapGridSpacingTitle = Translator.translate("SNAP_GRID_INFO");
		if(info == null) {
			String val = b ? format.format(i) + "mm" : Translator.translate("OFF");
			snapGridInfoLabel.setText(snapGridSpacingTitle + " " + val);
		} else {
			snapGridInfoLabel.setText(snapGridSpacingTitle + " " + info);
		}
	}

	private void setShowGridInfoLabel(String info) {
		double i = (double) SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SPACING);
		boolean b = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_VISIBLE);

		i = i / 100;
		String snapGridSpacingTitle = Translator.translate("VISIBLE_GRID_INFO");
		if(info == null) {
			String val = b ? format.format(i) + "mm" : Translator.translate("OFF");
			showGridInfoLabel.setText(snapGridSpacingTitle + " " + val);
		} else {
			showGridInfoLabel.setText(snapGridSpacingTitle + " " + info);
		}
	}

	private void setPositionInfoLabel(Point2D p) {
		xInfoLabel.setText(Translator.translate("POSITION_X") + " " + format.format(p.getX()) + " mm");
		yInfoLabel.setText(Translator.translate("POSITION_Y") + " " + format.format(p.getY()) + " mm");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SchemOptions.PROPERTY_GRID_SNAP)) {
			setSnapGridInfoLabel(null);
		} else if(evt.getPropertyName().equals(SchemOptions.PROPERTY_GRID_SNAP_SPACING)) {
			setSnapGridInfoLabel(null);
		} else if(evt.getPropertyName().equals(SchemOptions.PROPERTY_GRID_SPACING)) {
			setShowGridInfoLabel(null);
		} else if(evt.getPropertyName().equals(SchemOptions.PROPERTY_GRID_VISIBLE)) {
			setShowGridInfoLabel(null);
		} else if(evt.getPropertyName().equals(SchemTabbook.PROPERTY_MOUSE_POSITION)) {
			setPositionInfoLabel((Point2D) (evt.getNewValue()));
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setPositionInfoLabel(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setPositionInfoLabel(e.getPoint());
	}

}
