
package org.heinz.eda.schem.util;

import java.awt.Point;


public class ExtRect {

	public int x;

	public int y;

	public int width;

	public int height;

	public ExtRect(ExtRect rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public ExtRect(Point p, int width, int height) {
		this(p.x, p.y, width, height);
	}

	public ExtRect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void add(int px, int py) {
		add(new ExtRect(px, py, 0, 0));
	}

	public void add(Point p) {
		add(p.x, p.y);
	}

	public void add(ExtRect r) {
		r = r.normalize();
		int rex = r.x + r.width;
		int rey = r.y + r.height;
		int rx = x + width;
		int ry = y + height;

		if(width >= 0) {
			if(rex > rx) {
				width = rex - x;
			}
			if(r.x < x) {
				width += (x - r.x);
				x = r.x;
			}
		} else {
			if(r.x < rx) {
				width = r.x - x;
			}
			if(rex > x) {
				width -= (rex - x);
				x = rex;
			}
		}

		if(height >= 0) {
			if(rey > ry) {
				height = rey - y;
			}
			if(r.y < y) {
				height += (y - r.y);
				y = r.y;
			}
		} else {
			if(r.y < ry) {
				height = r.y - y;
			}
			if(rey > y) {
				height -= (rey - y);
				y = rey;
			}
		}
	}

	public boolean contains(int px, int py) {
		if((width < 0) || (height < 0)) {
			return normalize().contains(px, py);
		}
		return (px >= x) && (px <= (x + width)) && (py >= y) && (py <= (y + height));
	}

	public boolean contains(ExtRect r) {
		r = r.normalize();
		return contains(r.x, r.y) && contains(r.x + r.width, r.y + r.height);
	}

	public boolean intersects(ExtRect r) {
		r = r.normalize();
		boolean containsH = contains(r.x, y) || contains(r.x + r.width, y);
		boolean containsV = contains(x, r.y) || contains(x, r.y + r.height);
		return containsH && containsV;
	}

	public boolean excludes(ExtRect r) {
		return !intersects(r);
	}

	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}

	@SuppressWarnings("LocalVariableHidesMemberVariable")
	public ExtRect normalize() {
		int x = this.x;
		int y = this.y;
		int width = this.width;
		int height = this.height;

		if(width < 0) {
			width = -width;
			x = x - width;
		}
		if(height < 0) {
			height = -height;
			y = y - height;
		}

		return new ExtRect(x, y, width, height);
	}

	@Override
	public String toString() {
		return getClass().getName() + " [" + x + ", " + y + ", " + width + ", " + height + "]";
	}

	public static void main(String[] a) {
		ExtRect r1 = new ExtRect(10, 10, 10, 10);
		ExtRect r2 = new ExtRect(10, 20, 10, 10);
		System.out.println(r1.intersects(r2));
	}

}
