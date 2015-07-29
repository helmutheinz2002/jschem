
package org.heinz.framework.crossplatform.dialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class StandardDialogPanel extends JPanel {

	private final String title;

	public StandardDialogPanel(String title) {
		this.title = title;
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}

	public String getTitle() {
		return title;
	}

	public abstract String check();

	public abstract void ok();

	public void cancel() {
	}

	public void prepareToShow() {
	}

}
