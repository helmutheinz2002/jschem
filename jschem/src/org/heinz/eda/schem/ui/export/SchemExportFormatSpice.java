package org.heinz.eda.schem.ui.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.heinz.eda.schem.SchemConstants;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.netlist.NetListAnalyzer;
import org.heinz.eda.schem.util.UnitConverter;

public class SchemExportFormatSpice extends SchemExportFormat {
	private static final String LINE_SEPARATOR = "*******************************************************************";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); 
	
	protected SchemExportFormatSpice() {
		super("cir", "EXPORT_FORMAT_CIR", false);
	}

	public void exportSheets(List sheets, File file) throws IOException {
		NetListAnalyzer analyzer = new NetListAnalyzer(sheets);
		
		FileOutputStream fo = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fo);
		pw.println(LINE_SEPARATOR);
		pw.println("* Netlist created by " + SchemConstants.PROGRAM_DESCRIPTION);
		pw.println("* " + DATE_FORMAT.format(new Date()));
		pw.println(LINE_SEPARATOR);
		pw.println("*");
		
		List lines = new ArrayList();
		for(Iterator it=analyzer.components(); it.hasNext();) {
			Component c = (Component) it.next();
			List pinInfos = (List) analyzer.getPinsForComponent(c);
			
			if(pinInfos.size() > 0)
				printComponent(lines, c, pinInfos);
		}
		
		Collections.sort(lines);
		for(Iterator it=lines.iterator(); it.hasNext();) {
			String line = (String) it.next();
			pw.println(line);
		}
		
		pw.close();
		fo.close();
	}

	private void printComponent(List lines, Component c, List pinInfos) {
		String id = c.getAttributeText(Component.KEY_PART_ID).getText();
		
		String category = id;
		while(true) {
			int l = category.length();
			if(l == 0)
				break;
			
			char ch = category.charAt(l - 1);
			if(Character.isDigit(ch))
				category = category.substring(0, l - 1);
			else
				break;
		}

		category = category.toLowerCase();
		
		if(category.length() > 0) {
			if(category.equals("r") || category.equals("c") || category.equals("l"))
				printDipole(lines, c, id, pinInfos);
			else
				printSubCircuitCall(lines, c, id, pinInfos);
		}
	}
	
	private void printSubCircuitCall(List lines, Component c, String id, List pinInfos) {
		String model = c.getAttributeText(Component.KEY_MODEL_NAME).getText();
		if(model.length() == 0)
			return;
		
		StringBuffer sb = new StringBuffer("X");
		sb.append(id);
		
		printNodes(sb, pinInfos);
		
		sb.append(" ");
		sb.append(model);

		lines.add(sb.toString());
	}
	
	private void printDipole(List lines, Component c, String id, List pinInfos) {
		StringBuffer sb = new StringBuffer(id);
		
		printNodes(sb, pinInfos);
		
		String model = c.getAttributeText(Component.KEY_MODEL_NAME).getText();
		if(model.length() > 0) {
			sb.append(" ");
			sb.append(model);
		}

		String value = c.getAttributeText(Component.KEY_PART_NAME).getText();
		value = UnitConverter.convertToStandardNotation(value);
		
		sb.append(" ");
		sb.append(value);
		
		lines.add(sb.toString());
	}
	
	private void printNodes(StringBuffer sb, List pinInfos) {
		for(Iterator it=pinInfos.iterator(); it.hasNext();) {
			NetListAnalyzer.PinNetInfo pi = (NetListAnalyzer.PinNetInfo) it.next();
			sb.append(" ");
			sb.append(pi.net.netNo);
		}
	}
}
