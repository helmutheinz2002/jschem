
package org.heinz.eda.schem.ui.dialog;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.framework.crossplatform.utils.Translator;

public class SubTextComponentTableModel extends AbstractTableModel {

	public static final int COL_VISIBLE = 0;

	public static final int COL_FUNCTION = 1;

	public static final int COL_TEXT = 2;

	public static final int COL_COLOR = 3;

	public static final int COL_ORIENTATION = 4;

	public static final int COL_FONT = 5;

	public static final int COL_LAST = 6;

	public static final int[] DEFAULT_COL_WIDTH = {50, 70, 150};

	private List texts = new ArrayList();

	private List deletedTexts = new ArrayList();

	public SubTextComponentTableModel(AbstractComponent c) {
		for(Iterator it = c.attributeTexts(); it.hasNext();) {
			Text t = (Text) it.next();
			texts.add(new TextInfo(t, t.getKey()));
		}
	}

	public void addText(String purpose) {
		int l = texts.size();
		Text newText = AbstractComponent.createAttributeText("");
		texts.add(new TextInfo(newText, true, purpose));
		fireTableRowsInserted(l, l);
	}

	public void removeRow(int row) {
		TextInfo ti = (TextInfo) texts.remove(row);
		deletedTexts.add(ti);
		fireTableRowsDeleted(row, row);
	}

	public List getTexts() {
		return texts;
	}

	public List getDeletedTexts() {
		return deletedTexts;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return (col != COL_FUNCTION);
	}

	@Override
	public int getColumnCount() {
		return COL_LAST;
	}

	@Override
	public int getRowCount() {
		return texts.size();
	}

	@Override
	public String getColumnName(int col) {
		switch(col) {
			case COL_VISIBLE:
				return Translator.translate("TABLE_VISIBLE");
			case COL_FUNCTION:
				return Translator.translate("TABLE_PURPOSE");
			case COL_TEXT:
				return Translator.translate("TABLE_TEXT");
			case COL_ORIENTATION:
				return Translator.translate("TABLE_ORIENTATION");
			case COL_FONT:
				return Translator.translate("TABLE_FONT");
			case COL_COLOR:
				return Translator.translate("TABLE_COLOR");
			default:
				break;
		}
		return "";
	}

	@Override
	public Class getColumnClass(int col) {
		if(col == COL_ORIENTATION) {
			return Orientation.class;
		}
		if(col == COL_VISIBLE) {
			return Boolean.class;
		}
		if(col == COL_FONT) {
			return FontInfo.class;
		}
		if(col == COL_COLOR) {
			return Color.class;
		}
		return String.class;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		TextInfo t = (TextInfo) texts.get(row);
		switch(col) {
			case COL_VISIBLE:
				t.isVisible = ((Boolean) value);
				break;
			case COL_TEXT:
				String oldText = t.newText;
				t.newText = (String) value;
				boolean oldTextEmpty = ((oldText == null) || (oldText.length() == 0));
				boolean newTextEmpty = ((t.newText == null) || (t.newText.length() == 0));
				if(oldTextEmpty && !newTextEmpty) {
					t.isVisible = true;
					fireTableCellUpdated(row, COL_VISIBLE);
				}
				if(newTextEmpty && !oldTextEmpty) {
					t.isVisible = false;
					fireTableCellUpdated(row, COL_VISIBLE);
				}
				break;
			case COL_ORIENTATION:
				t.newOrientation = (Orientation) value;
				break;
			case COL_FONT:
				t.fontInfo = (FontInfo) value;
				break;
			case COL_COLOR:
				t.color = (Color) value;
				break;
			default:
				break;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		TextInfo t = (TextInfo) texts.get(row);
		switch(col) {
			case COL_VISIBLE:
				return t.isVisible;
			case COL_FUNCTION:
				return t.infoText == null ? Translator.translate("INFO") : Translator.translate(t.infoText);
			case COL_TEXT:
				return t.newText;
			case COL_ORIENTATION:
				return t.newOrientation;
			case COL_FONT:
				return t.fontInfo;
			case COL_COLOR:
				return t.color;
			default:
				break;
		}
		return "";
	}

	//------------------------------------------

	public static class FontInfo {

		public String fontName;

		public int fontStyle;

		public int fontSize;

		public FontInfo(String fontName, int fontSize, int fontStyle) {
			this.fontName = fontName;
			this.fontSize = fontSize;
			this.fontStyle = fontStyle;
		}

	}

	public class TextInfo {

		public final Text text;

		public String newText;

		public Orientation newOrientation;

		public boolean isNew;

		public boolean isVisible;

		public final String infoText;

		public FontInfo fontInfo;

		public Color color;

		public TextInfo(Text text, boolean isNew, String infoText) {
			this.text = text;
			newText = text.getText();
			newOrientation = text.getOrientation();
			this.isNew = isNew;
			this.infoText = infoText;
			isVisible = text.isVisible();
			fontInfo = new FontInfo(text.getFontName(), text.getFontSize(), text.getFontStyle());
			color = text.getColor();
		}

		public TextInfo(Text text, String infoText) {
			this(text, false, infoText);
		}

	}

}
