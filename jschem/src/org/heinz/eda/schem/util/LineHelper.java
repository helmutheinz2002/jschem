
package org.heinz.eda.schem.util;


public class LineHelper {

	public static boolean contains(int x, int y, int px, int py, int dx, int dy, int clickTolerance) {
		ExtRect r = new ExtRect(0, 0, dx, dy);

		x -= px;
		y -= py;

		ExtRect bounds = r.normalize();
		bounds = new ExtRect(bounds.x - clickTolerance, bounds.y - clickTolerance, bounds.width + 2 * clickTolerance, bounds.height + 2 * clickTolerance);
		if(!bounds.contains(x, y)) {
			return false;
		}

		int a = r.height;
		int b = -r.width;
		int c = -(a * r.x + b * r.y);

		int d = (int) ((a * x + b * y + c) / Math.sqrt(a * a + b * b));
		d = Math.abs(d);
		return d < clickTolerance;
	}

}
