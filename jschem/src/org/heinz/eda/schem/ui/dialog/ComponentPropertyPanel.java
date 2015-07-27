package org.heinz.eda.schem.ui.dialog;

import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class ComponentPropertyPanel extends AbstractComponentPropertyPanel {
	private SubTextComponentPanel textPanel;
	
	public ComponentPropertyPanel(String title, boolean fillColor) {
		super(Translator.translate(title), fillColor);
		
		textPanel = new SubTextComponentPanel();
		addExtension(textPanel);
	}

	public ComponentPropertyPanel(boolean isSymbol) {
		this(isSymbol ? "SYMBOL_PROPERTIES" : "COMPONENT_PROPERTIES", false);
	}

	public void setComponent(AbstractComponent c) {
		super.setComponent(c);
		textPanel.setComponent(c);
	}
	
	public void ok() {
		super.ok();
		textPanel.ok();
	}
}
