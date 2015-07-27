package org.heinz.eda.schem.model.components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.heinz.eda.schem.util.ExtRect;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;
import org.heinz.framework.utils.xml.XmlPropertyConverterString;

public class Text extends AbstractComponent {
	static {
		PROPERTIES.put(new XmlProperty("text", XmlPropertyConverterString.instance()), Text.class);
		PROPERTIES.put(new XmlProperty("fontName", XmlPropertyConverterString.instance()), Text.class);
		PROPERTIES.put(new XmlProperty("fontSize", XmlPropertyConverterInteger.instance()), Text.class);
		PROPERTIES.put(new XmlProperty("fontStyle", XmlPropertyConverterInteger.instance()), Text.class);
	}

	public static final int[] FONT_SIZES = getFontSizes();
	
	private String text = "";
	private int fontSize = 200;
	private int fontStyle = Font.PLAIN;
	private Font font;
	private String fontName = "SansSerif";
	private FontMetrics fontMetrics;
	private FontRenderContext fontRenderContext;
	private ExtRect bounds;
	
	public Text() {
	}
	
	public Text(int x, int y, String text, String fontName, int fontSize, int fontStyle) {
		super(x, y);
		this.text = text;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontName = fontName;
	}
	
	public Text(Text text) {
		super(text);
		this.text = text.text;
		fontSize = text.fontSize;
		fontStyle = text.fontStyle;
		fontName = text.fontName;
	}
	
	public boolean hasBecomeInvalid() {
		return (fontSize == 0) || (text.length() == 0);
	}
	
	public void setText(String text) {
		fireWillChange();
		this.text = text;
		updateBounds();
		fireChanged();
	}
	
	public String getText() {
		return text;
	}
	
	private void updateBounds() {
		if(fontRenderContext != null) {
			Font f = new Font(fontName, fontStyle, fontSize);
			Rectangle2D b = f.getStringBounds(text, fontRenderContext);
			bounds = new ExtRect(0, 0, (int) b.getWidth(), (int) b.getHeight());
		}
	}
	
	protected void draw(Graphics g, double zoom, boolean selected) {
		Graphics2D g2d = (Graphics2D) g;
		if(fontRenderContext == null)
			fontRenderContext = g2d.getFontRenderContext();

		int fs = (int) (fontSize * zoom);
		updateFont(g, fs);
		
		g.setFont(font);
		g.setColor(getColor(selected));
		g.drawString(text, 0, fs - fontMetrics.getDescent());
	}
	
	public boolean contains(int x, int y, int clickTolerance) {
		if(!isVisible())
			return false;

		return getBounds().contains(x, y);
	}

	protected ExtRect getBounds() {
		if(!isVisible())
			return null;
		
		if(fontMetrics == null)
			return new ExtRect(0, 0, text.length()*fontSize, fontSize);
		
		return bounds;
	}
	
	private void updateFont(Graphics g, int size) {
		if((font == null) || (font.getSize() != size)) {
			font = new Font(fontName, fontStyle, size);
			fontMetrics = g.getFontMetrics(font);
			updateBounds();
		}
	}

	public AbstractComponent duplicate() {
		return new Text(this);
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		fireWillChange();
		this.fontName = fontName;
        font = null;
        fontRenderContext = null;
		fireChanged();
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		fireWillChange();
		this.fontSize = fontSize;
        font = null;
        fontRenderContext = null;
		fireChanged();
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		fireWillChange();
		this.fontStyle = fontStyle;
        font = null;
        fontRenderContext = null;
		fireChanged();
	}
	
	private static int[] getFontSizes() {
		List l = new ArrayList();
		
		for(int i=100; i<200; i+=20)
			l.add(new Integer(i));
		for(int i=200; i<500; i+=50)
			l.add(new Integer(i));
		for(int i=500; i<2000; i+=100)
			l.add(new Integer(i));
		for(int i=2000; i<5000; i+=500)
			l.add(new Integer(i));
		for(int i=5000; i<10000; i+=1000)
			l.add(new Integer(i));
		
		int[] fs = new int[l.size()];
		for(int i=0; i<fs.length; i++)
			fs[i] = ((Integer) l.get(i)).intValue();
		
		return fs;
	}
}
