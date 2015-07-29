
package org.heinz.framework.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

public class ViewUtils {

	public static void centerOnScreen(Component c) {
		centerOn(c, new Point(), Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static void centerOn(Component c, Component owner) {
		centerOn(c, owner.getLocation(), owner.getSize());
	}

	public static void centerOn(Component c, Point p, Dimension d) {
		Dimension cd = c.getSize();
		c.setLocation(p.x + (d.width - cd.width) / 2, p.y + (d.height - cd.height) / 2);
	}

	public static Dimension getDefaultWindowSize() {
		return getDefaultWindowSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static Point getDefaultWindowPosition() {
		return getDefaultWindowPosition(Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static Dimension getDefaultWindowSize(Dimension d) {
		int ww = d.width * 4 / 5;
		int wh = d.height * 4 / 5;
		return new Dimension(ww, wh);
	}

	public static Point getDefaultWindowPosition(Dimension d) {
		int ww = d.width * 4 / 5;
		int wh = d.height * 4 / 5;
		return new Point((d.width - ww) / 2, (d.height - wh) / 2);
	}

}
