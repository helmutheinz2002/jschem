package org.heinz.framework.examples;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class KeyTest {
public static void main(String[] args) {
	JFrame f = new JFrame() {
		protected void processKeyEvent(KeyEvent e) {
			System.out.println(e.getKeyCode());
			super.processKeyEvent(e);
		}
	};
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setVisible(true);
}
}
