package org.heinz.framework.utils;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.heinz.framework.crossplatform.utils.IconLoader;


public class OutputStreamWindow extends JFrame {
	private static OutputStreamWindow instance;
	private List buttons = new ArrayList();
	private OutputStreamTextArea ost;
	private boolean dirty;
	private boolean firstShow = true;
	
	public OutputStreamWindow(String title, String initialText) {
		super();
		
		if(instance != null)
			throw new IllegalStateException("Instance already set");
		
		setTitle(title);
		setIconImage(getIcon().getImage());
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(4, 4));
		ost = new OutputStreamTextArea(initialText);
		getContentPane().add(BorderLayout.CENTER, new JScrollPane(ost));
		
		JPanel bp = new JPanel(new FlowLayout(FlowLayout.TRAILING, 4, 4));
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		bp.add(clearButton);
		getContentPane().add(BorderLayout.SOUTH, bp);
		
		ost.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dirty = true;
				setButtonsVisible(true);
			}
		});
		ost.setAsStdStreams();
		
		setSize(500, 400);
		
		instance = this;
		
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
	}
	
	public static OutputStreamWindow instance() {
		return instance;
	}
	
	public void clear() {
		dirty = false;
		ost.clear();
		setButtonsVisible(false);
		setVisible(false);
	}

	private void setButtonsVisible(boolean b) {
		for(Iterator it=buttons.iterator(); it.hasNext();)
			((JButton) it.next()).setVisible(b);
	}
	
	public JButton createButton() {
		JButton button = new JButton(getIcon());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(firstShow) {
					ViewUtils.centerOnScreen(OutputStreamWindow.this);
					firstShow = false;
				}
				setVisible(true);
			}
		});
		button.setVisible(dirty);
		buttons.add(button);
		return button;
	}
	
	public void removeButton(JButton button) {
		buttons.remove(button);
	}
	
	private ImageIcon getIcon() {
		return IconLoader.instance().loadIcon("menu/flag.png");
	}
}
