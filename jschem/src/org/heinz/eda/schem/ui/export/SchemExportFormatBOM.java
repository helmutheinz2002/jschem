package org.heinz.eda.schem.ui.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.util.Stringifier;
import org.heinz.eda.schem.util.UnitConverter;
import org.heinz.framework.crossplatform.utils.Translator;

public abstract class SchemExportFormatBOM extends SchemExportFormat {
	SchemExportFormatBOM(String extension) {
		super(extension, "EXPORT_FORMAT_BOM", false);
	}

	public void exportSchematic(Schematics schematic, File file) throws IOException {
		List sheets = new ArrayList();
		for(Iterator it=schematic.sheets(); it.hasNext();)
			sheets.add(it.next());
		
		exportSheets(sheets, file);
	}
	
	public void exportSheet(Sheet sheet, File file) throws IOException {
		List sheets = new ArrayList();
		sheets.add(sheet);
		exportSheets(sheets, file);
	}
	
	public void exportSheets(List sheets, File file) throws IOException {
		// Group components by value
		Map partsByKey = new HashMap();
		
		for(Iterator it=Schematics.getComponentList(sheets, Component.class).iterator(); it.hasNext();) {
			Component c = (Component) it.next();
			
			String value = c.getAttributeText(Component.KEY_PART_NAME).getText();
			if(value.length() == 0)
				continue;
			
			String id = c.getAttributeText(Component.KEY_PART_ID).getText();
			String order = c.getAttributeText(Component.KEY_ORDER_NO).getText();
			
			PartInfo pi = new PartInfo(id, value, order);
			
			PartValue partValue = (PartValue) partsByKey.get(pi.key);
			if(partValue == null) {
				partValue = new PartValue(value, order);
				partsByKey.put(pi.key, partValue);
			}
			partValue.add(pi);
		}
		
		// Group values by component types
		Map partValuesByType = new HashMap();
		// Collect diverse values here
		PartValueList diverseValues = new PartValueList(Translator.translate("MISCELLANEOUS"));
		
		for(Iterator it=partsByKey.values().iterator(); it.hasNext();) {
			PartValue pv = (PartValue) it.next();
			
			if(pv.diverse) {
				diverseValues.add(pv);
				continue;
			}
			
			PartValueList typeList = (PartValueList) partValuesByType.get(pv.category);
			if(typeList == null) {
				typeList = new PartValueList(pv.category);
				partValuesByType.put(pv.category, typeList);
			}
			typeList.add(pv);
		}
		
		// Determine max value length
		int maxValueLen = 0; 
		for(Iterator it=partValuesByType.values().iterator(); it.hasNext();) {
			PartValueList pvl = (PartValueList) it.next();
			if(pvl.maxLenValue > maxValueLen)
				maxValueLen = pvl.maxLenValue;
		}
		
		// Determine max order length
		int maxOrderLen = 0; 
		for(Iterator it=partValuesByType.values().iterator(); it.hasNext();) {
			PartValueList pvl = (PartValueList) it.next();
			if(pvl.maxLenOrder > maxOrderLen)
				maxOrderLen = pvl.maxLenOrder;
		}
		
		List pvls = new ArrayList(partValuesByType.keySet());
		Collections.sort(pvls);

		// Output to file
		FileOutputStream fo = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fo);
		printTitle(pw, sheets);
		
		for(Iterator it=pvls.iterator(); it.hasNext();) {
			String type = (String) it.next();
			PartValueList partValueList = (PartValueList) partValuesByType.get(type);
			
			Collections.sort(partValueList.partValues, new PartValueComparator(partValueList.maxLenValue));
			printType(pw, partValueList, maxValueLen, maxOrderLen);
		}
		
		if(diverseValues.partValues.size() > 0)
			printType(pw, diverseValues, maxValueLen, maxOrderLen);
		
		pw.close();
		fo.close();
	}
	
	protected String flatten(List list, Stringifier stringifier, String separator) {
		StringBuffer sb = new StringBuffer();
		for(Iterator it=list.iterator(); it.hasNext();) {
			sb.append(stringifier.toString(it.next()));
			if(it.hasNext())
				sb.append(separator);
		}
		
		return sb.toString();
	}
	
	protected abstract void printTitle(PrintWriter pw, List sheets);
	protected abstract void printValue(PrintWriter pw, PartValue pv, int maxValueLen, int maxOrderLen);
	
	protected void printType(PrintWriter pw, PartValueList partValueList, int maxValueLen, int maxOrderLen) {
		for(Iterator pvit=partValueList.partValues.iterator(); pvit.hasNext();) {
			PartValue pv = (PartValue) pvit.next();
			printValue(pw, pv, maxValueLen, maxOrderLen);
		}
		
		pw.println();
	}

	//---------------------------------------------------------------------

	class PartValueComparator implements Comparator {
		private String filler;
		
		public PartValueComparator(int maxLen) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<maxLen; i++)
				sb.append(" ");
			filler = sb.toString();
		}
		
		public int compare(Object arg0, Object arg1) {
			PartValue pv1 = (PartValue) arg0;
			PartValue pv2 = (PartValue) arg1;
			
			if((pv1.decimalValue != null) && (pv2.decimalValue != null))
				return pv1.decimalValue.compareTo(pv2.decimalValue);

			if(pv1.decimalValue != null)
				return -1;
			
			if(pv2.decimalValue != null)
				return 1;
			
			String value1 = filler.substring(pv1.rawValue.length()) + pv1.rawValue;
			String value2 = filler.substring(pv2.rawValue.length()) + pv2.rawValue;

			return value1.compareTo(value2);
		}
	}
	
	//---------------------------------------------------------------------

	class PartValueList implements Comparable {
		public List partValues = new ArrayList();
		public String title;
		public int maxLenValue = 0;
		public int maxLenOrder = 0;
		
		public PartValueList(String title) {
			this.title = title;
		}
		
		public void add(PartValue pv) {
			partValues.add(pv);
			if(pv.getValueLength() > maxLenValue)
				maxLenValue = pv.getValueLength();
			if(pv.getOrderLength() > maxLenOrder)
				maxLenOrder = pv.getOrderLength();
		}

		public int compareTo(Object arg0) {
			PartValueList pvl = (PartValueList) arg0;
			return title.compareTo(pvl.title);
		}
	}
	
	//---------------------------------------------------------------------

	class PartValue implements Stringifier {
		public String rawValue;
		public Double decimalValue;
		public List parts = new ArrayList();
		public boolean diverse = false;
		public String category;
		public String order;
		
		public PartValue(String value, String order) {
			this.rawValue = value;
			this.order = order;
			try {
				decimalValue = new Double(UnitConverter.convertToDecimal(rawValue));
			} catch(Exception e) {
			}
		}
		
		public void add(PartInfo pi) {
			parts.add(pi);
			if(category == null)
				category = pi.category;
			else {
				if(!category.equals(pi.category))
					diverse = true;
			}
		}
		
		public int getOrderLength() {
			return order.length();
		}
		
		public int getValueLength() {
			if(decimalValue == null)
				return rawValue.length();
			return 0;
		}
		
		public String getPartIds() {
			Collections.sort(parts);
			return flatten(parts, this, ",");
		}

		public String toString(Object o) {
			return ((PartInfo) o).id;
		}
	}
	
	//---------------------------------------------------------------------
	
	class PartInfo implements Comparable {
		public final String value;
		public final String id;
		public final String order;
		public int number;
		public String category;
		public final String key;
		
		public PartInfo(String id, String value, String order) {
			this.id = id;
			this.value = value;
			this.order = order;
			key = value + order;
			
			StringBuffer numberSb = new StringBuffer();
			number = 0;
			category = id;
			while(true) {
				int l = category.length();
				if(l == 0)
					break;
				
				char ch = category.charAt(l - 1);
				if(Character.isDigit(ch)) {
					category = category.substring(0, l - 1);
					numberSb.insert(0, ch);
				} else
					break;
			}
			
			try {
				number = new Integer(numberSb.toString()).intValue();
			} catch(Exception ex) {
				number = 0;
			}
		}

		public int compareTo(Object arg) {
			PartInfo pi = (PartInfo) arg;
			return number - pi.number;
		}
		
		public String toString() {
			return "" + id;
		}
	}
}
