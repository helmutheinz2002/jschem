
package org.heinz.eda.schem.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public abstract class SimpleDocumentFilter extends DocumentFilter {

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
		sb.insert(offs, str);
		if(accept(sb.toString())) {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
		StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
		sb.delete(offset, offset + length);
		if(accept(sb.toString())) {
			super.remove(fb, offset, length);
		}
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
		sb.replace(offset, offset + length, text);
		if(accept(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
		}
	}

	protected abstract boolean accept(String s);

}
