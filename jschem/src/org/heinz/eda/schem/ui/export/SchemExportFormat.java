
package org.heinz.eda.schem.ui.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.export.ExportFormat;
import org.heinz.framework.crossplatform.utils.export.ExportHelper;

public abstract class SchemExportFormat extends ExportFormat {

	public static final SchemExportFormat BOM_TXT = new SchemExportFormatBOM_TXT();

	public static final SchemExportFormat BOM_CSV = new SchemExportFormatBOM_CSV();

	public static final SchemExportFormat CIR = new SchemExportFormatSpice();

	private static final SchemExportFormat[] SCHEM_EXPORT_FORMATS = {BOM_TXT, BOM_CSV /*, CIR */};

	public static ExportFormat[] getDefaultSheetExportFormats() {
		return getFormatList(ExportHelper.getDefaultImageExportFormats(), SCHEM_EXPORT_FORMATS);
	}

	public static ExportFormat[] getDefaultSchematicsExportFormats() {
		return getFormatList(ExportHelper.getDefaultMultiImageExportFormats(), SCHEM_EXPORT_FORMATS);
	}

	protected SchemExportFormat(String extension, String description, boolean image) {
		super(extension, description, image);
	}

	public void exportSchematic(Schematics schematic, File file) throws IOException {
		List sheets = new ArrayList();
		for(Iterator it = schematic.sheets(); it.hasNext();) {
			sheets.add(it.next());
		}

		exportSheets(sheets, file);
	}

	public void exportSheet(Sheet sheet, File file) throws IOException {
		List sheets = new ArrayList();
		sheets.add(sheet);
		exportSheets(sheets, file);
	}

	public abstract void exportSheets(List sheets, File file) throws IOException;

	private static ExportFormat[] getFormatList(ExportFormat[] globalFormats, ExportFormat[] schemFormats) {
		ExportFormat[] exp = new ExportFormat[schemFormats.length + globalFormats.length];

		List sl = new ArrayList(Arrays.asList(schemFormats));
		List gl = new ArrayList(Arrays.asList(globalFormats));
		gl.addAll(sl);

		int i = 0;
		for(Iterator it = gl.iterator(); it.hasNext(); i++) {
			exp[i] = (ExportFormat) it.next();
		}

		return exp;
	}

}
