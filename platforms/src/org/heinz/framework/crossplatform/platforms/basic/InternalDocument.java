
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

	private final Map properties = new HashMap();

	private final DefaultActionStateInfoProvider actionInfoProvider = new DefaultActionStateInfoProvider();

	public InternalDocument() {
		super("", true, true, true, true);
		getContentPane().setLayout(new GridBagLayout());
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				dls.fireDocumentActivated();
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				dls.fireDocumentDeactivated();
			}

			@Override
			@SuppressWarnings("CallToPrintStackTrace")
			public void internalFrameClosing(InternalFrameEvent e) {
				try {
					close();
				} catch(DocumentCloseVetoException e1) {
					e1.printStackTrace();
				}
			}

		});
		setIconImage(IconLoader.instance().loadImage("data/icons/application/document.png"));
		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setVisible(true);
			}

		});
	}

	public void close() throws DocumentCloseVetoException {
		if(dls.getListenerCount() == 0) {
			dispose();
		} else {
			dls.fireDocumentClosing(false);
		}
	}

	@Override
	public void addDocumentListener(DocumentListener listener) {
		dls.addDocumentListener(listener);
	}

	@Override
	public void removeDocumentListener(DocumentListener listener) {
		dls.removeDocumentListener(listener);
	}

	@Override
	public void dispose() {
		dls.fireDocumentClosed();
		ApplicationActions.instance().removeStateInfoProvider(this);
		super.dispose();
	}

	@Override
	public Container getContainer() {
		return getContentPane();
	}

	public final void setIconImage(Image icon) {
		setFrameIcon(new ImageIcon(icon));
	}

	@Override
	@SuppressWarnings("CallToPrintStackTrace")
	public void setIconified(boolean b) {
		try {
			setIcon(b);
		} catch(PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSelected() {
		try {
			setSelected(true);
		} catch(PropertyVetoException e) {
		}
	}

	@Override
	public void setMaximized(boolean b) {
		try {
			setMaximum(b);
		} catch(PropertyVetoException e) {
		}
	}

	@Override
	public boolean isIconified() {
		return isIcon();
	}

	@Override
	public boolean isMaximized() {
		return isMaximum();
	}

	@Override
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

		if(getEditToolBar() != null) {
			getEditToolBar().setDefaultTool();
		}
	}

	@Override
	public Component getDocumentPane() {
		return pane;
	}

	@Override
	public EditToolBar getEditToolBar() {
		return editToolBar;
	}

	public void setEditToolBar(EditToolBar editToolBar) {
		this.editToolBar = editToolBar;
	}

	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	@Override
	public void setProperty(Object key, Object value) {
		properties.put(key, value);
	}

	@Override
	public void addActionStateInfos(ActionStateInfos stateInfos) {
		actionInfoProvider.addActionStateInfos(stateInfos);
	}

	@Override
	public void addStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
		actionInfoProvider.addStateInfoProvider(stateInfoProvider);
	}

	@Override
	public void removeStateInfoProvider(ActionStateInfoProvider stateInfoProvider) {
		actionInfoProvider.removeStateInfoProvider(stateInfoProvider);
	}

}
