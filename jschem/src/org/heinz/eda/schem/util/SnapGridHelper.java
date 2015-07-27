package org.heinz.eda.schem.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.Schematics;
import org.heinz.framework.crossplatform.utils.Translator;

public class SnapGridHelper {
	public static void adjustSnapGrid(Schematics schematics, Component optionPaneParent) {
		List onGrid = new ArrayList();
		List offGrid = new ArrayList();
		int guessGrid = schematics.guessSnapGrid(onGrid, offGrid);
		boolean gridSnap = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_SNAP);
		int currentGrid = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING);
		
		if(guessGrid < 5) {
			if(gridSnap) {
				int res = JOptionPane.showConfirmDialog(optionPaneParent, Translator.translate("COULD_NOT_GUESS_GRID"), Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
				if(res == JOptionPane.YES_OPTION)
					SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP, new Boolean(false));
			}
			return;
		}
		
		if(!gridSnap || (gridSnap && ((guessGrid % currentGrid) != 0))) {
			String msg = Translator.translate("SNAP_GRID_DETECTED") + " " + ((double) guessGrid/100.0) + "mm\n" + Translator.translate("SWITCH_TO_GUESSED_GRID");
			int res = JOptionPane.showConfirmDialog(optionPaneParent, msg, Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.YES_OPTION) {
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP, new Boolean(true));
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING, new Integer(guessGrid));
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_VISIBLE, new Boolean(true));
				int visibleGrid = GridHelper.guessVisibleGrid(guessGrid);
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SPACING, new Integer(visibleGrid));
				
//				if(offGrid.size() > 0) {
//					res = JOptionPane.showConfirmDialog(this, "Grid erzwingen? "+offGrid.size(), Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
//					if(res == JOptionPane.YES_OPTION)
//						try {
//						schematics.enforceSnapGrid();
//						} catch(Exception ex) {
//							ex.printStackTrace();
//						}
//				}
			}
		}
	}

}
