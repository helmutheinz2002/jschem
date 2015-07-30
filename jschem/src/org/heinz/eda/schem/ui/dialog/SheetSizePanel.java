
package org.heinz.eda.schem.ui.dialog;

import java.awt.GridBagLayout;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.SheetSize;
import org.heinz.eda.schem.ui.beans.SheetSizeBean;
import org.heinz.framework.crossplatform.dialog.StandardDialogPanel;
import org.heinz.framework.crossplatform.utils.Translator;

public class SheetSizePanel extends StandardDialogPanel {

	private final SheetSizeBean sheetSizeBean;

	private final Sheet sheet;

	@SuppressWarnings("LeakingThisInConstructor")
	public SheetSizePanel(Sheet sheet) {
		super(Translator.translate("SHEET_SIZE_TITLE"));

		this.sheet = sheet;
		sheetSizeBean = new SheetSizeBean();
		sheetSizeBean.setSheetSize(sheet.getSize());
		setLayout(new GridBagLayout());
		sheetSizeBean.addTo(this, 0);
	}

	@Override
	public String check() {
		return null;
	}

	@Override
	public void ok() {
		SheetSize ss = sheetSizeBean.getSheetSize();
		sheet.setSize(ss);
	}

}
