
package org.heinz.eda.schem.ui.tools;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToolBar;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.ui.beans.FontBean;

public class TextTool extends OrientedNewTool implements PropertyChangeListener {

	private final FontBean fontBean;

	@SuppressWarnings("LeakingThisInConstructor")
	public TextTool() {
		super("texttool.png", false);

		fontBean = new FontBean();
		fontBean.setFontName(SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME));
		fontBean.setFontSize(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE));
		fontBean.setFontStyle(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_STYLE));

		addToolbarObject(new JToolBar.Separator());
		addToolbarObject(fontBean.boldButton);
		addToolbarObject(fontBean.italicButton);
		addToolbarObject(new JToolBar.Separator());
		addToolbarObject(fontBean.fontTypePanel);
		addToolbarObject(new JToolBar.Separator());
		addToolbarObject(fontBean.fontSizePanel);
		addToolbarObject(new JToolBar.Separator());
		addToolbarObject(fontBean.textField);

		SchemOptions.instance().addPropertyChangeListener(this);
	}

	@Override
	protected AbstractComponent createComponent(int x, int y) {
		final Text t = new Text(x, y, fontBean.getText(), fontBean.getFontName(), fontBean.getFontSize(), fontBean.getFontStyle());
		t.setOrientation(getOrientation());
		return t;
	}

	protected boolean acceptObject(AbstractComponent newComponent, Point p) {
		Text t = (Text) newComponent;
		return !((t.getText() == null) || (t.getText().length() == 0));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SchemOptions.PROPERTY_TEXT_FONT_SIZE)) {
			fontBean.setFontSize(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_SIZE));
		} else if(evt.getPropertyName().equals(SchemOptions.PROPERTY_TEXT_FONT_STYLE)) {
			fontBean.setFontStyle(SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_TEXT_FONT_STYLE));
		} else if(evt.getPropertyName().equals(SchemOptions.PROPERTY_TEXT_FONT_NAME)) {
			fontBean.setFontName(SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME));
		}
	}

}
