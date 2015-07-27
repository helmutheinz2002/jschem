package org.heinz.framework.crossplatform.dialog;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.PlatformDefaults;
import org.heinz.framework.crossplatform.utils.Translator;

public class StandardDialog extends JDialog implements ActionListener {
	public static final int OK_CANCEL_BUTTONS = 0;
	public static final int CLOSE_BUTTON = 1;
	
	public static final int CLOSE_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	public static final int OK_PRESSED = 2;
	
	private JButton okButton;
	private JButton cancelButton;
	private StandardDialogPanel editPanel;
	private int result = CANCEL_PRESSED;
	private int buttons;
	private boolean reverseButtons;

	public static int showDialog(Window owner, StandardDialogPanel editPanel, Dimension minSize) {
		return showDialog(owner, editPanel, OK_CANCEL_BUTTONS, minSize);
	}
	
	public static int showDialog(Window owner, StandardDialogPanel editPanel) {
		return showDialog(owner, editPanel, OK_CANCEL_BUTTONS, null);
	}
	
	public static int showDialog(Window owner, StandardDialogPanel editPanel, int buttons) {
		return showDialog(owner, editPanel, buttons, null);
	}
	
	public static int showDialog(Window owner, StandardDialogPanel editPanel, int buttons, Dimension minSize) {
		StandardDialog d = null;
		if(owner instanceof Dialog)
			d = new StandardDialog((Dialog) owner, editPanel, buttons);
		else
			d = new StandardDialog((Frame) owner, editPanel, buttons);
		d.pack();
		editPanel.prepareToShow();

		Dimension dd = d.getSize();
		if(minSize != null) {
			if (dd.width < minSize.width)
				dd.width = minSize.width;
			if (dd.height < minSize.height)
				dd.height = minSize.height;
			d.setSize(dd);
		}
		
		Point p = new Point(0, 0);
		Dimension od = Toolkit.getDefaultToolkit().getScreenSize();
		if(owner != null) {
			Point l = owner.getLocation();
			if((l.x >= 0) && (l.y >= 0)) {
				p = l;
				od = owner.getSize();
			}
		}
		d.setLocation(p.x + (od.width - dd.width) / 2, p.y + (od.height - dd.height) / 2);
		d.setVisible(true);
		
		return d.result;
	}

	StandardDialog(Frame owner, final StandardDialogPanel editPanel, int buttons) {
		super(owner, true);
		init(editPanel, buttons);
	}

	StandardDialog(Dialog owner, final StandardDialogPanel editPanel, int buttons) {
		super(owner, true);
		init(editPanel, buttons);
	}

	private void init(final StandardDialogPanel editPanel, int buttons) {
		this.editPanel = editPanel;
		this.buttons = buttons;
		
		reverseButtons = CrossPlatform.getPlatform().getPlatformDefaults().getBool(PlatformDefaults.REVERSE_BUTTON_ORDER);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				editPanel.cancel();
			}
		});
		
		setTitle(editPanel.getTitle());
		
		getContentPane().setLayout(new BorderLayout());

		JPanel lp = new JPanel(new BorderLayout());
		getContentPane().add(BorderLayout.SOUTH, lp);
		getContentPane().add(BorderLayout.CENTER, editPanel);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		int bottom = CrossPlatform.getPlatform().getPlatformDefaults().getInt(PlatformDefaults.BOTTOM_INSET);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, bottom, 5));
		lp.add(BorderLayout.EAST, buttonPanel);

		List buttonList = new ArrayList();
		
		if(buttons == OK_CANCEL_BUTTONS) {
			okButton = new JButton(Translator.translate("OK"));
			cancelButton = new JButton(Translator.translate("CANCEL"));
			addButton(buttonList, okButton);
			addButton(buttonList, cancelButton);
			okButton.addActionListener(this);
			cancelButton.addActionListener(this);
			getRootPane().setDefaultButton(okButton);
		} else if(buttons == CLOSE_BUTTON) {
			cancelButton = new JButton(Translator.translate("CLOSE"));
			addButton(buttonList, cancelButton);
			cancelButton.addActionListener(this);
			getRootPane().setDefaultButton(cancelButton);
		}

		for(Iterator it=buttonList.iterator(); it.hasNext();) {
			JButton b = (JButton) it.next();
			buttonPanel.add(b);
		}
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				editPanel.cancel();
				setVisible(false);
			}
		};

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
	}

	private void addButton(List buttons, JButton button) {
		if(reverseButtons)
			buttons.add(0, button);
		else
			buttons.add(button);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			editPanel.ok();
			result = OK_PRESSED;
		} else {
			if(buttons == CLOSE_BUTTON)
				result = CLOSE_PRESSED;
			else if(buttons == OK_CANCEL_BUTTONS)
				result = CANCEL_PRESSED;
			editPanel.cancel();
		}

		setVisible(false);
	}
}
