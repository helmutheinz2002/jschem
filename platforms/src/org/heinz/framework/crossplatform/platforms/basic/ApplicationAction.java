
package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;

public class ApplicationAction extends AbstractAction {

	private ActionListener listener;

	public ApplicationAction(String text, String iconPath, KeyStroke accelerator, Integer mnemonic, ActionListener listener) {
		super(Translator.translate(text), IconLoader.instance().loadIcon(iconPath));
		init(text, accelerator, mnemonic, listener);
	}

	public ApplicationAction(String text, KeyStroke accelerator, Integer mnemonic, ActionListener listener) {
		super(Translator.translate(text));
		init(text, accelerator, mnemonic, listener);
	}

	public void setAccelerator(KeyStroke accelerator) {
		putValue(Action.ACCELERATOR_KEY, accelerator);
	}

	private void init(String text, KeyStroke accelerator, Integer mnemonic, ActionListener listener) {
		this.listener = listener;

		KeyStroke softAccelerator = Translator.instance().getAccelerator(text, CrossPlatform.getPlatform().getName());
		if(softAccelerator != null) {
			accelerator = softAccelerator;
		}

		if(accelerator != null) {
			putValue(ACCELERATOR_KEY, accelerator);
		}
		if(mnemonic != null) {
			putValue(MNEMONIC_KEY, mnemonic);
		}

		try {
			putValue(SHORT_DESCRIPTION, Translator.translate(text + "_TOOLTIP"));
		} catch(Exception ex) {
			// Does not have a tooltip, set text instead
			putValue(SHORT_DESCRIPTION, Translator.translate(text));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(listener != null) {
			e.setSource(this);
			listener.actionPerformed(e);
		}
	}

	public void fireActionPerformed() {
		actionPerformed(new ActionEvent(this, 0, (String) getValue(ACTION_COMMAND_KEY)));
	}

}
