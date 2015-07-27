package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentListener;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;
import org.heinz.framework.crossplatform.utils.DocumentListenerSupport;
import org.heinz.framework.crossplatform.utils.IconLoader;

public class InternalDocument extends JInternalFrame implements Document {
	protected DocumentListenerSupport dls = new DocumentListenerSupport(this);
	private Component pane;
	private EditToolBar editToolBar;
	private Map properties = new HashMap();
	private DefaultActionStateInfoProvider actionInfoProvider = new DefaultActionStateInfoProvider();

	public InternalDocument() {
		super("", true, true, true, true);
		getContentPane().setLayout(new GridBagLayout());
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameActivated(InternalFrameEvent e) {
				dls.fireDocumentActivated();
			}

			public void internalFrameDeactivated(InternalFrameEvent e) {
				dls.fireDocumentDeactivated();
			}

			public void internalFrameClosing(InternalFrameEvent e) {
				try {
					close();
				} catch (DocumentCloseVetoException e1) {
					e1.printStackTrace();
				}
			}
		});
		setIconImage(IconLoader.instance().loadImage("data/icons/application/document.png"));
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}
	
	public void close() throws DocumentCloseVetoException {
		if(dls.getListenerCount() == 0)
			dispose();
		else
			dls.fireDocumentClosing(false);
	}
	
	public void addDocumentListener(DocumentListener listener) {
		dls.addDocumentListener(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		dls.removeDocumentListener(listener);
	}

	public void dispose() {
		dls.fireDocumentClosed();
		ApplicationActions.instance().removeStateInfoProvider(this);
		super.dispose();
	}

	public Container getContainer() {
		return getContentPane();
	}

	public void setIconImage(Image icon) {
		setFrameIcon(new ImageIcon(icon));
	}

	public void setIconified(boolean b) {
		try {
			setIcon(b);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public void setSelected() {
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
		}
	}

	public void setMaximized(boolean b) {
		try {
			setMaximum(b);
		} catch (PropertyVetoException e) {
		}
	}

	public boolean isIconified() {
		return isIcon();
	}

	public boolean isMaximized() {
		return isMaximum();
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
		pane.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		
		getContentPane().add(pane, c);
		
		if(getEditToolBar() != null)
			getEditToolBar().setDefaultTool();
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
