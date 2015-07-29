
package org.heinz.framework.crossplatform.platforms.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.CrossPlatform;

public abstract class AbstractActions {

	protected List actionListeners = new ArrayList();

	private final List customActions = new ArrayList();

	@SuppressWarnings("LeakingThisInConstructor")
	public AbstractActions() {
		if(ApplicationActions.instance() != null) {
			((AbstractActions) ApplicationActions.instance()).addCustomActions(this);
		}
	}

	void addCustomActions(AbstractActions customAction) {
		if(!customActions.contains(customAction)) {
			customActions.add(customAction);
		}
	}

	void setCustomActionStates(ActionStateInfos stateInfos) {
		for(Iterator it = customActions.iterator(); it.hasNext();) {
			AbstractActions actions = (AbstractActions) it.next();
			actions.setActionStates(stateInfos);
		}
	}

	protected Application getApplication() {
		return CrossPlatform.getPlatform().getApplication();
	}

	protected void fireAction(Action action) {
		ActionEvent e = new ActionEvent(action, 0, "");
		for(Iterator it = actionListeners.iterator(); it.hasNext();) {
			ActionListener l = (ActionListener) it.next();
			l.actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener listener) {
		if(!actionListeners.contains(listener)) {
			actionListeners.add(listener);
		}
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	protected ApplicationAction createAction(String text, String iconPath, KeyStroke accelerator, Integer mnemonic, ActionListener listener) {
		return new ApplicationAction(text, iconPath, accelerator, mnemonic, listener);
	}

	protected abstract void setActionStates(ActionStateInfos stateInfos);

}
