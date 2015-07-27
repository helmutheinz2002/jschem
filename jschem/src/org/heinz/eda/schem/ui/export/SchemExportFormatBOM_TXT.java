package org.heinz.eda.schem.ui.export;

import java.io.PrintWriter;
import java.util.List;

import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.util.Stringifier;
import org.heinz.framework.crossplatform.utils.Translator;

public class SchemExportFormatBOM_TXT extends SchemExportFormatBOM {
	private static final Stringifier sheetStringifier = new Stringifier() {
		public String toString(Object o) {
			return ((Sheet) o).getTitle();
		}
	};

	SchemExportFormatBOM_TXT() {
		super("txt");
	}

	protected  void printTitle(PrintWriter pw, List sheets) {
		String sheetNames = flatten(sheets, sheetStringifier, ",");
		pw.println(Translator.translate("EXPORT_FORMAT_BOM") + ": " + sheetNames);
		pw.println();
	}
	
	protected void printType(PrintWriter pw, PartValueList partValueList, int maxValueLen, int maxOrderLen) {
		pw.println(partValueList.title+":");
		super.printType(pw, partValueList, maxValueLen, maxOrderLen);
	}

	protected void printValue(PrintWriter pw, PartValue pv, int maxValueLen, int maxOrderLen) {
		if(maxOrderLen > 0) {
			pw.print(fillString(pv.order, maxOrderLen, ' ', false));
			pw.print("\t");
		}
		
		pw.print(fillString(""+pv.parts.size(), 4, ' ', true));
		pw.print("\t");
		
		pw.print(fillString(pv.rawValue, maxValueLen, ' ', false));
		pw.print("\t");
		
		pw.println(pv.getPartIds());
	}
	
	private String fillString(String s, int len, char filler, boolean prepend) {
		StringBuffer sb = new StringBuffer(s);
		
		while(sb.length() < len)
			if(prepend)
				sb.insert(0, filler);
			else
				sb.append(filler);
		
		return sb.toString();
	}
}
