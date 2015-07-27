package org.heinz.eda.schem.model.xml;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.heinz.framework.utils.xml.XmlPropertyConverter;

public class XmlPropertyConverterPoints implements XmlPropertyConverter {
	protected static XmlPropertyConverter instance;
	
	public String formatValue(Object o) {
		List l = (List) o;
		StringBuffer sb = new StringBuffer();
		
		for(Iterator it=l.iterator(); it.hasNext();) {
			Point p = (Point) it.next();
			if(sb.length() > 0)
				sb.append(';');
			sb.append(p.x);
			sb.append(',');
			sb.append(p.y);
		}
		return sb.toString();
	}

	public Object parseValue(String s) {
		List points = new ArrayList();
		StringTokenizer st = new StringTokenizer(s, ";");
		while(st.hasMoreTokens()) {
			String p = st.nextToken();
			StringTokenizer c = new StringTokenizer(p, ",");
			int x = new Integer(c.nextToken()).intValue();
			int y = new Integer(c.nextToken()).intValue();
			points.add(new Point(x, y));
		}
		return points;
	}

	public static XmlPropertyConverter instance() {
		if(instance == null)
			instance = new XmlPropertyConverterPoints();
		return instance;
	}
}
