
package org.heinz.framework.crossplatform.utils;

import java.awt.Point;

public class WindowStacker {

	private static final int OFFSET = 10;

	private static final int MAX_OFFSET_COUNT = 10;

	private Point startPosition;

	private Point nextPosition;

	private int offsetCount = 0;

	public WindowStacker() {
		setStartPosition(new Point(OFFSET, OFFSET));
	}

	public final void setStartPosition(Point p) {
		startPosition = new Point(p);
		nextPosition = new Point(startPosition);
	}

	public Point getNextPosition() {
		try {
			return new Point(nextPosition);
		} finally {
			offsetCount++;
			nextPosition.translate(OFFSET, OFFSET);
			if(offsetCount > MAX_OFFSET_COUNT) {
				offsetCount = 0;
				nextPosition = new Point(startPosition);
			}
		}
	}

}
