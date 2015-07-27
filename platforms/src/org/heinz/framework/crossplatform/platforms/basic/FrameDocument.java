package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentListener;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;
import org.heinz.framework.crossplatform.utils.DocumentListenerSupport;
import org.heinz.framework.crossplatform.utils.IconLoader;

public class FrameDocument extends JFrame implements Document {
	protected DocumentListenerSupport dls = new DocumentListenerSupport(this);
	private Component pane;
	private EditToolBar editToolBar;
	private Map properties = new HashMap();
	private DefaultActionStateInfoProvider actionInfoProvider = new DefaultActionStateInfoProvider();

	public FrameDocument() {
		getContentPane().setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				dls.fireDocumentActivated();
			}

			public void windowDeactivated(WindowEvent e) {
				dls.fireDocumentDeactivated();
			}

			public void windowClosing(WindowEvent e) {
				if(dls.getListenerCount() == 0)
					dispose();
				else {
					try {
						dls.fireDocumentClosing(false);
					} catch (DocumentCloseVetoException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		setIconImage(IconLoader.instance().loadImage("data/icons/application/application.png"));
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}
	
	public void addDocumentListener(DocumentListener listener) {
		dls.addDocumentListener(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		dls.removeDocumentListener(listener);
	}

	public Container getContainer() {
		return getContentPane();
	}

	public void close() throws DocumentCloseVetoException {
		if(dls.getListenerCount() == 0)
			dispose();
		else
			dls.fireDocumentClosing(false);
	}
	
	public void dispose() {
		dls.fireDocumentClosed();
		ApplicationActions.instance().removeStateInfoProvider(this);
		super.dispose();
	}

	public void setIconified(boolean b) {
		setWindowState(JFrame.ICONIFIED, b);
	}

	public void setSelected() {
		requestFocus();
		toFront();
	}
	
	public boolean isSelected() {
		return isActive();
	}

	public void setMaximized(boolean b) {
		setWindowState(JFrame.MAXIMIZED_BOTH, b);
	}
	
	private void setWindowState(int state, boolean b) {
		int s = getExtendedState(); 
		if(((s & state) != state) && b)
			setExtendedState(s | state);
		if(((s & state) == state) && !b)
			setExtendedState(s & ~state);
	}

	public boolean isIconified() {
		return (getExtendedState() & JFrame.ICONIFIED) == JFrame.ICONIFIED;
	}

	public boolean isMaximized() {
		return (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
	}

	public void setDocumentPane(Component pane) {
		this.pane = pane;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		getContentPane().add(pane, c);
		
		pane.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		
		if(getEditToolBar() != null)
			getEditToolBar().setDefaultTool();
		
		validate();
	}

	public Component getDocumentPane() {
		return pane;
	}

	public EditToolBar getEditToolBar() {
		return editToolBar;
	}

	public void setEditToolBar(EditToolBar editToolBar) {
		this.editToolBar = editToolBar;
	}

	public Object getProperty(Object key) {
		return properties.get(key);
	}

	public void setProperty(Object key, Object value) {
		properties.put(key, value);
	}

	public void addActionStateInfos(ActionStateInfos stateInfos) {
		actionInfoProvider.addActionStateInfos(stateInfos);
	}

	public void addStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
		actionInfoProvider.addStateInfoProvider(stateInfoProvider);
	}

	public void removeStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
		actionInfoProvider.removeStateInfoProvider(stateInfoProvider);
	}
}
