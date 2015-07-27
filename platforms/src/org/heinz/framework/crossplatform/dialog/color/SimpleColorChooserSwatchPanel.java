package org.heinz.framework.crossplatform.dialog.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public abstract class SimpleColorChooserSwatchPanel extends JPanel implements MouseListener {
	private Color[] colors;
	private int rows;
	private int cols;
	private int fieldSize;
	
	public SimpleColorChooserSwatchPanel(Color[] colors, int rows, int cols, int fieldSize) {
		this.colors = colors;
		this.rows = rows;
		this.cols = cols;
		this.fieldSize = fieldSize;
		Dimension d = new Dimension(rows * fieldSize, cols * fieldSize);
		setPreferredSize(d);
		addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		for(int r=0; r<rows; r++) {
			int y = r * fieldSize;
			
			for(int c=0; c<cols; c++) {
				int x = c * fieldSize;
				Color col = colors[r * cols + c];
				g.setColor(col);
				g.fillRect(x, y, fieldSize-1, fieldSize-1);
			}
		}
	}

	protected abstract void colorChanged(Color color);
	
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		int selRow = p.y / fieldSize;
		int selCol = p.x / fieldSize;
		int idx = selRow * cols + selCol;
		
		colorChanged(colors[idx]);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
