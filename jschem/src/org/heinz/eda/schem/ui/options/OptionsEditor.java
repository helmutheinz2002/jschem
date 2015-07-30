
package org.heinz.eda.schem.ui.options;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;

import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;

public class OptionsEditor extends StandardDialogPanel {

	private final List editPanels = new ArrayList();

	private JTabbedPane tabbook;

	private static int lastIndex = -1;

	public OptionsEditor() {
		super(Translator.translate("OPTIONS"));
		setBorder(null);
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		tabbook = new JTabbedPane();
		add(BorderLayout.CENTER, tabbook);

		addPage(new ViewEditor());
		addPage(new ColorEditor());
		addPage(new FontEditor());
		addPage(new GeneralEditor());

		if(lastIndex >= 0) {
			tabbook.setSelectedIndex(lastIndex);
		}
	}

	private void addPage(StandardDialogPanel panel) {
		tabbook.add(panel.getTitle(), panel);
		editPanels.add(panel);
	}

	@Override
	public void ok() {
		for(Iterator it = editPanels.iterator(); it.hasNext();) {
			StandardDialogPanel p = (StandardDialogPanel) it.next();
			p.ok();
		}
		lastIndex = tabbook.getSelectedIndex();
	}

	@Override
	public void cancel() {
		for(Iterator it = editPanels.iterator(); it.hasNext();) {
			StandardDialogPanel p = (StandardDialogPanel) it.next();
			p.cancel();
		}
		lastIndex = tabbook.getSelectedIndex();
	}

	@Override
	public String check() {
		for(Iterator it = editPanels.iterator(); it.hasNext();) {
			StandardDialogPanel p = (StandardDialogPanel) it.next();
			String s = p.check();
			if(s != null) {
				return s;
			}
		}
		return null;
	}

}
