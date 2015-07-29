
package org.heinz.framework.crossplatform.dialog.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public abstract class SimpleColorChooserSwatchPanel extends JPanel implements MouseListener {

	private final Color[] colors;

	private final int rows;

	private final int cols;

	private final int fieldSize;

	@SuppressWarnings("LeakingThisInConstructor")
	public SimpleColorChooserSwatchPanel(Color[] colors, int rows, int cols, int fieldSize) {
		this.colors = colors;
		this.rows = rows;
		this.cols = cols;
		this.fieldSize = fieldSize;
		Dimension d = new Dimension(rows * fieldSize, cols * fieldSize);
		setPreferredSize(d);
		addMouseListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		for(int r = 0; r < rows; r++) {
			int y = r * fieldSize;

			for(int c = 0; c < cols; c++) {
				int x = c * fieldSize;
				Color col = colors[r * cols + c];
				g.setColor(col);
				g.fillRect(x, y, fieldSize - 1, fieldSize - 1);
			}
		}
	}

	protected abstract void colorChanged(Color color);

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		int selRow = p.y / fieldSize;
		int selCol = p.x / fieldSize;
		int idx = selRow * cols + selCol;

		colorChanged(colors[idx]);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
