package org.heinz.eda.schem.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GridHelper {
	private static final int MAX_GUESS_GRID = 200;
	private static final int MIN_HIT_PRECENTAGE = 90;
	private static final int[] GRID_FACTORS = { 10, 20, 25, 50, 100, 200, 250, 500, 1000 };
	private static final int MIN_VISIBLE_GRID = 300;
	
	public static int snapToGrid(int coord, int snapGrid) {
		int gh = snapGrid / 2;
		if(coord < 0)
			gh = -gh;
		return ((coord + gh) / snapGrid) * snapGrid;
	}
	
	public static int guessVisibleGrid(int snapGrid) {
		for(int i=0; i<GRID_FACTORS.length; i++) {
			int vg = snapGrid * GRID_FACTORS[i];
			if(vg >= MIN_VISIBLE_GRID)
				return vg;
		}
		
		// not reached
		return 500;
	}
	
	public static int guessGrid(Map pinCoords, List onGrid, List offGrid) {
		if(pinCoords.size() == 0)
			return 50;
		
		List coords = new ArrayList();
		for(Iterator it=pinCoords.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			Point p = (Point) pinCoords.get(key);
			coords.add(new CoordInfo(p.x, key));
			coords.add(new CoordInfo(p.y, key));
		}
		
		int l = coords.size();
		int requiredHits = (int) ((double) l * (double) MIN_HIT_PRECENTAGE / 100.0);
		
		for(int grid=MAX_GUESS_GRID; grid>1; grid--) {
			List hit = new ArrayList();
			List miss = new ArrayList();
			
			for(Iterator it=coords.iterator(); it.hasNext();) {
				CoordInfo ci = (CoordInfo) it.next();
				if((ci.coord % grid) == 0)
					hit.add(ci.key);
				else
					miss.add(ci.key);
			}
			
			if(hit.size() > requiredHits) {
				if(onGrid != null)
					onGrid.addAll(hit);
				if(offGrid != null)
					offGrid.addAll(miss);
				return grid;
			}
		}
		
		return 1;
	}
	
	//---------------------------------------------------
	
	private static class CoordInfo {
		final int coord;
		final Object key;
		
		public CoordInfo(final int coord, final Object key) {
			super();
			this.coord = coord;
			this.key = key;
		}
	}
}
