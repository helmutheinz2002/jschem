
package org.heinz.eda.schem.model;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.heinz.eda.schem.util.ExtRect;

public class Orientation implements Serializable {

	private static final Orientation[] ORIENTATIONS = buildOrientations();

	public static final Orientation RIGHT = ORIENTATIONS[0];

	public static final Orientation UP = ORIENTATIONS[1];

	public static final Orientation LEFT = ORIENTATIONS[2];

	public static final Orientation DOWN = ORIENTATIONS[3];

	public static final Orientation[] DIRECTIONS = {RIGHT, UP, LEFT, DOWN};

	public static final Orientation FLIP_LEFT_RIGHT = new Orientation("FLIP_HORIZ", -1, 0, 0, 1, 0, "down.png", false, false, 0);

	public static final Orientation FLIP_TOP_BOTTOM = new Orientation("FLIP_VERT", 1, 0, 0, -1, 0, "down.png", false, false, 0);

	public final String name;

	public final int t11;

	public final int t12;

	public final int t21;

	public final int t22;

	public final String icon;

	public final int angle;

	public final int key;

	public final boolean flipHorizontal;

	public final boolean flipVertical;

	private Orientation(String name, int t11, int t12, int t21, int t22, int angle, String icon, boolean flipH, boolean flipV, int key) {
		this.name = name;
		this.t11 = t11;
		this.t12 = t12;
		this.t21 = t21;
		this.t22 = t22;
		this.icon = icon;
		this.angle = angle;
		this.key = key;
		this.flipHorizontal = flipH;
		this.flipVertical = flipV;
	}

	public AffineTransform getAffineTransform() {
		AffineTransform affineTransform = new AffineTransform((double) t11, (double) t21, (double) t12, (double) t22, 0, 0);
		if(flipHorizontal) {
			affineTransform.concatenate(Orientation.FLIP_LEFT_RIGHT.getAffineTransform());
		}
		if(flipVertical) {
			affineTransform.concatenate(Orientation.FLIP_TOP_BOTTOM.getAffineTransform());
		}
		return affineTransform;
	}

	public Orientation flipVertical() {
		int k = key ^ 8;
		return ORIENTATIONS[k];
	}

	public Orientation flipHorizontal() {
		int k = key ^ 4;
		return ORIENTATIONS[k];
	}

	public Orientation getDirection() {
		return ORIENTATIONS[key & 3];
	}

	public static Orientation getDirection(Orientation o) {
		return ORIENTATIONS[o.key & 3];
	}

	public static Orientation getOrientation(int key) {
		return ORIENTATIONS[key];
	}

	public static Orientation getOrientation(int direction, boolean flipH, boolean flipV) {
		return ORIENTATIONS[direction + (flipH ? 4 : 0) + (flipV ? 8 : 0)];
	}

	public int transformAngle(int angle) {
		return (this.angle + angle) % 360;
	}

	public Point transform(Point p) {
		return transform(p.x, p.y);
	}

	public ExtRect transform(ExtRect r) {
		Point loc = transform(r.x, r.y);
		Point edge = transform(r.width, r.height);
		return new ExtRect(loc.x, loc.y, edge.x, edge.y);
	}

	public Point transform(int x, int y) {
		Point p = new Point(x, y);

		if(flipHorizontal) {
			p = FLIP_LEFT_RIGHT.transform(p);
		}
		if(flipVertical) {
			p = FLIP_TOP_BOTTOM.transform(p);
		}

		p = new Point(t11 * p.x + t12 * p.y, t21 * p.x + t22 * p.y);
		return p;
	}

	public Point unTransform(int x, int y) {
		Point p = new Point(x, y);

		int det = t11 * t22 - t12 * t21;
		int px = (t22 * p.x - t12 * y) / det;
		int py = (-t21 * p.x + t11 * y) / det;
		p = new Point(px, py);

		if(flipVertical) {
			p = FLIP_TOP_BOTTOM.unTransform(p);
		}
		if(flipHorizontal) {
			p = FLIP_LEFT_RIGHT.unTransform(p);
		}

		return p;
	}

	public Point unTransform(Point p) {
		return unTransform(p.x, p.y);
	}

	public Orientation getNextOrientation() {
		int baseKey = key & 3;
		int flips = key & 12;
		baseKey = (baseKey + 1) & 3;
		return ORIENTATIONS[baseKey + flips];
	}

	public Orientation getPrevOrientation() {
		int baseKey = key & 3;
		int flips = key & 12;
		baseKey = (baseKey - 1) & 3;
		return ORIENTATIONS[baseKey + flips];
	}

	public Orientation transform(Orientation o) {
		Point p = new Point(1, 2);
		Point tp = transform(p);
		tp = o.transform(tp);

		for(Orientation r : ORIENTATIONS) {
			if(r.transform(p).equals(tp)) {
				return r;
			}
		}
		return null;
	}

	private static Orientation[] buildOrientations() {
		List l = new ArrayList();
		for(int flipV = 0; flipV < 2; flipV++) {
			boolean fv = (flipV != 0);
			for(int flipH = 0; flipH < 2; flipH++) {
				boolean fh = (flipH != 0);
				int key = 4 * flipH + 8 * flipV;
				l.add(new Orientation("ORIENTATION_RIGHT", 1, 0, 0, 1, 0, "right.png", fh, fv, key));
				l.add(new Orientation("ORIENTATION_UP", 0, 1, -1, 0, 90, "up.png", fh, fv, key + 1));
				l.add(new Orientation("ORIENTATION_LEFT", -1, 0, 0, -1, 180, "left.png", fh, fv, key + 2));
				l.add(new Orientation("ORIENTATION_DOWN", 0, -1, 1, 0, 270, "down.png", fh, fv, key + 3));
			}
		}

		return (Orientation[]) l.toArray(new Orientation[l.size()]);
	}

}
