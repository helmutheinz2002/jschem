package org.heinz.eda.schem.ui.dialog;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.ui.beans.FontBean;
import org.heinz.framework.crossplatform.utils.Translator;

public class TextPropertyPanel extends AbstractComponentPropertyPanel {
	private FontBean fontBean;
	
	public TextPropertyPanel() {
		super(Translator.translate("TEXT_PROPERTIES"), false);
		
		fontBean = new FontBean();
		nextRow = fontBean.addTo(this, nextRow, true);
		
		addFiller();
	}

	public void setComponent(AbstractComponent c) {
		super.setComponent(c);
		
		Text t = (Text) c;
		fontBean.setText(t.getText());
		fontBean.setFontName(t.getFontName());
		fontBean.setFontSize(t.getFontSize());
		fontBean.setFontStyle(t.getFontStyle());
	}

	public void ok() {
		super.ok();
		
		Text t = (Text) getComponent();
		t.setText(fontBean.getText());
		t.setFontName(fontBean.getFontName());
		t.setFontStyle(fontBean.getFontStyle());
		t.setFontSize(fontBean.getFontSize());
	}
	
	public void prepareToShow() {
		fontBean.textField.requestFocusInWindow();
	}
}
