
package org.heinz.eda.schem.ui.export;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.framework.crossplatform.utils.Translator;

public class SchemExportFormatBOM_CSV extends SchemExportFormatBOM {

	SchemExportFormatBOM_CSV() {
		super("csv");
	}

	@Override
	protected void printTitle(PrintWriter pw, List sheets) {
		pw.println(quote(Translator.translate("EXPORT_FORMAT_BOM")));
		for(Iterator it = sheets.iterator(); it.hasNext();) {
			Sheet sheet = (Sheet) it.next();
			pw.println(quote(sheet.getTitle()));
		}
		pw.println();
	}

	@Override
	protected void printValue(PrintWriter pw, PartValue pv, int maxValueLen, int maxOrderLen) {
		pw.print(quote(pv.order));
		pw.print(",");

		pw.print(quote("" + pv.parts.size()));
		pw.print(",");

		pw.print(quote(pv.rawValue));
		pw.print(",");

		pw.println(quote(pv.getPartIds()));
	}

	private String quote(String s) {
		StringBuilder sb = new StringBuilder(s);

		for(int i = 0; i < sb.length(); i++) {
			if(sb.charAt(i) == '"') {
				sb.insert(i, '"');
				i++;
			}
		}

		return "\"" + sb.toString() + "\"";
	}

}
