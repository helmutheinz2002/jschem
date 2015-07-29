
package org.heinz.framework.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OutputStreamTextArea extends JTextArea {

	private final TextAreaOutputStream stdErr;

	private final TextAreaOutputStream stdOut;

	private final List listeners = new ArrayList();

	private boolean empty;

	public OutputStreamTextArea(String title) {
		setEditable(false);
		stdErr = new TextAreaOutputStream(System.err);
		stdOut = new TextAreaOutputStream(System.out);
		new PrintStream(stdOut).println(title);
		empty = true;
	}

	public void setAsStdStreams() {
		System.setOut(new PrintStream(stdOut));
		System.setErr(new PrintStream(stdErr));
	}

	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	public void clear() {
		setText(null);
		empty = true;
	}

	private void fireChange() {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			ChangeListener l = (ChangeListener) it.next();
			l.stateChanged(new ChangeEvent(this));
		}
	}

	class TextAreaOutputStream extends OutputStream {

		private final PrintStream orgStream;

		public TextAreaOutputStream(PrintStream stream) {
			orgStream = stream;
		}

		@Override
		public void write(int b) throws IOException {
			append(new String(new byte[]{(byte) b}));
			orgStream.print((char) b);
			if(empty) {
				fireChange();
			}
			empty = false;
		}

	}

}
