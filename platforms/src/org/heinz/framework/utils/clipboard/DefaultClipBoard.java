package org.heinz.framework.utils.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class DefaultClipBoard extends SystemClipBoard {

	protected DefaultClipBoard() {
		super(DataFlavor.stringFlavor);
	}

	public void post(Object data) {
		Transferable transferData = new StringSelection((String) data);
		clipboard.setContents(transferData, this);
	}
}
