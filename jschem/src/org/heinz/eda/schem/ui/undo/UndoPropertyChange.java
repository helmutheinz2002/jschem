package org.heinz.eda.schem.ui.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.heinz.eda.schem.model.ComponentAdapter;
import org.heinz.eda.schem.model.ComponentListener;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.framework.crossplatform.utils.Translator;

public class UndoPropertyChange extends AbstractUndoableEdit {
	private ComponentListener listener;
	private AbstractComponent component;
	
	public UndoPropertyChange(AbstractComponent component) {
		this.component = component;
		listener = new ComponentAdapter();
		component.addComponentListener(listener);
	}
	
	public void stopListening() {
		component.removeComponentListener(listener);
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
	}

	public void undo() throws CannotUndoException {
		super.undo();
	}

	public String getPresentationName() {
		return Translator.translate("UNDO_PROPERTY_CHANGE");
	}
	
	public void die() {
		super.die();
	}
}
