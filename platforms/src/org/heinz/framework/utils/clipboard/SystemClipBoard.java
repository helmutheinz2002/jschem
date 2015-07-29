
package org.heinz.framework.utils.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class SystemClipBoard implements ClipboardOwner {

	private static SystemClipBoard instance;

	private final List listeners = new ArrayList();

	protected final DataFlavor dataFlavor;

	protected final Clipboard clipboard;

	private boolean canPaste;

	private Thread checkThread;

	protected SystemClipBoard(DataFlavor dataFlavor) {
		setInstance();
		this.dataFlavor = dataFlavor;

		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		fireClipboardChanged();
		startCheckThread();
	}

	private synchronized void startCheckThread() {
		// Not the most elegant solution, but the only way to make this work on pre-5.0 Java
		checkThread = new Thread() {

			@Override
			@SuppressWarnings("SleepWhileInLoop")
			public void run() {
				while(true) {
					checkClipboard();
					try {
						Thread.sleep(1000);
					} catch(InterruptedException ex) {
						return;
					}
				}
			}

		};
		checkThread.setPriority(Thread.MIN_PRIORITY);
		checkThread.setDaemon(true);
		checkThread.start();
	}

	private synchronized void setInstance() {
		SystemClipBoard oldBoard = instance;
		instance = this;

		if(oldBoard == null) {
			return;
		}

		for(Iterator it = oldBoard.listeners.iterator(); it.hasNext();) {
			SystemClipboardListener l = (SystemClipboardListener) it.next();
			addClipboardListener(l);
		}

		oldBoard.release();
		fireClipboardChanged();
	}

	public synchronized void release() {
		if(checkThread.isAlive()) {
			checkThread.interrupt();
		}
		instance = null;
	}

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public static SystemClipBoard abstractInstance() {
		if(instance == null) {
			new DefaultClipBoard();
		}
		return instance;
	}

	private synchronized void checkClipboard() {
		boolean oldCanPaste = canPaste;

		try {
			canPaste = isDataAvailable();
		} catch(Throwable t) {
		}

		if(oldCanPaste != canPaste) {
			fireClipboardChanged();
		}
	}

	public abstract void post(Object data);

	public boolean canPaste() {
		return canPaste;
	}

	public void addClipboardListener(SystemClipboardListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeClipboardListener(SystemClipboardListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		checkClipboard();
	}

	private void fireClipboardChanged() {
		boolean cp = canPaste();
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			SystemClipboardListener l = (SystemClipboardListener) it.next();
			l.clipboardChanged(cp);
		}
	}

	private boolean isDataAvailable() {
		Transferable data = clipboard.getContents(this);
		return data.isDataFlavorSupported(dataFlavor);
	}

	@SuppressWarnings("UseSpecificCatch")
	protected Object getContents() {
		try {
			if(isDataAvailable()) {
				Transferable data = clipboard.getContents(this);
				return data.getTransferData(dataFlavor);
			}
		} catch(Throwable e) {
		}

		return null;
	}

}
