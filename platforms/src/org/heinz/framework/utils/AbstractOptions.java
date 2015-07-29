
package org.heinz.framework.utils;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.heinz.framework.utils.xml.XmlObjectManager;
import org.heinz.framework.utils.xml.XmlPropertyConverter;
import org.heinz.framework.utils.xml.XmlPropertyConverterBoolean;
import org.heinz.framework.utils.xml.XmlPropertyConverterColor;
import org.heinz.framework.utils.xml.XmlPropertyConverterDouble;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;
import org.heinz.framework.utils.xml.XmlPropertyConverterString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public abstract class AbstractOptions {

	private static final int MAX_RECENT_FILES = 10;

	private static final List allOptions = new ArrayList();

	public static final String PROPERTY_LAST_EXPORT_DIR = defineOption("Settings.lastExportDir", "");

	public static final String PROPERTY_AUTHOR = defineOption("Settings.author", System.getProperty("user.name"));

	public static final String PROPERTY_COMPANY = defineOption("Settings.company", "");

	public static final String PROPERTY_BASE_RECENT_FILE = "Settings.recentFile";

	public static final String FILE_NAME = "settings.xml";

	private static final String DEFAULT_SUFFIX = ".default";

	private static final String TAG_OPTIONS = "options";

	private static final String TAG_OPTION = "option";

	private static final String TAG_VALUE = "value";

	private static final String TAG_KEY = "key";

	private static final String TAG_CLASS = "class";

	private static final Map converters = getAccessors();

	private Map options = new HashMap();

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private File settingsFile;

	public AbstractOptions(String dirName, AbstractOptions instance) {
		File wd = new File(dirName);
		wd.mkdirs();
		settingsFile = new File(dirName + File.separator + FILE_NAME);

		try {
			load();
		} catch(Exception ex) {
		}
		init();
		silentSave();

		if(instance != null) {
			throw new IllegalArgumentException("Options instance exists");
		}
	}

	public static String getDefaultOptionName(String option) {
		return option + DEFAULT_SUFFIX;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public Object getOption(String key) {
		return options.get(key);
	}

	public List getListOption(String baseKey) {
		String listKey = baseKey + ".";
		Map listEntries = new HashMap();

		for(Iterator it = options.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if(key.startsWith(listKey) && !key.endsWith(DEFAULT_SUFFIX)) {
				listEntries.put(key, options.get(key));
			}
		}

		List l = new ArrayList(listEntries.keySet());
		Collections.sort(l, new ListSuffixComparator());

		List res = new ArrayList();
		for(Iterator it = l.iterator(); it.hasNext();) {
			String key = (String) it.next();
			res.add(listEntries.get(key));
		}

		return res;
	}

	public int getIntOption(String key) {
		return ((Integer) getOption(key));
	}

	public double getDoubleOption(String key) {
		return ((Double) getOption(key));
	}

	public boolean getBoolOption(String key) {
		return ((Boolean) getOption(key));
	}

	public String getStringOption(String key) {
		return (String) getOption(key);
	}

	public Color getColorOption(String key) {
		return (Color) getOption(key);
	}

	public void setOption(String key, Object value) {
		if(value instanceof List) {
			List oldList = getListOption(key);
			String listKey = key + ".";

			for(Iterator it = options.keySet().iterator(); it.hasNext();) {
				String ok = (String) it.next();
				if(ok.startsWith(listKey)) {
					it.remove();
				}
			}

			List l = (List) value;
			int numDigits = (int) (Math.log(l.size()) / Math.log(10)) + 1;
			String formatString = "";
			for(int i = 0; i < numDigits; i++) {
				formatString += "0";
			}
			DecimalFormat format = new DecimalFormat(formatString);

			int idx = 0;
			for(Iterator it = l.iterator(); it.hasNext(); idx++) {
				String s = (String) it.next();
				options.put(listKey + format.format(idx), s);
			}

			pcs.firePropertyChange(key, oldList, l);
		} else {
			Object oldVal = options.get(key);
			options.put(key, value);
			pcs.firePropertyChange(key, oldVal, value);
		}
		silentSave();
	}

	private void setInitial(String key, Object value) {
		if(!options.containsKey(key)) {
			options.put(key, value);
		}
		String defKey = getDefaultOptionName(key);
		options.put(defKey, value);
	}

	private void init() {
		for(Iterator it = allOptions.iterator(); it.hasNext();) {
			Option o = (Option) it.next();
			setInitial(o.optionName, o.defaultValue);
		}
	}

	private void load() throws Exception {
		SAXParser p = SAXParserFactory.newInstance().newSAXParser();
		FileInputStream fis = new FileInputStream(settingsFile);

		DefaultHandler handler = new DefaultHandler() {

			@Override
			@SuppressWarnings("CallToPrintStackTrace")
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if(qName.toLowerCase().equals(TAG_OPTION)) {
					String key = attributes.getValue(TAG_KEY);
					String stringValue = attributes.getValue(TAG_VALUE);
					String className = attributes.getValue(TAG_CLASS);

					try {
						Class clazz = Class.forName(className);

						XmlPropertyConverter converter = (XmlPropertyConverter) converters.get(clazz);
						Object value = converter.parseValue(stringValue);
						options.put(key, value);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		};

		p.parse(fis, handler);
		init();
	}

	private void silentSave() {
		try {
			save();
		} catch(Exception e) {
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public void save() throws IOException {
		try (FileOutputStream fos = new FileOutputStream(settingsFile)) {
			PrintWriter pw = new PrintWriter(fos);
			
			pw.println(XmlObjectManager.XML_HEADER);
			pw.println("<" + TAG_OPTIONS + ">");
			
			List keys = new ArrayList(options.keySet());
			Collections.sort(keys);
			for(Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				if(key.endsWith(DEFAULT_SUFFIX)) {
					continue;
				}
				
				Object val = options.get(key);
				
				pw.print(XmlObjectManager.DEFAULT_INDENT + "<" + TAG_OPTION);
				pw.print(" " + TAG_KEY + "=\"" + key + "\"");
				pw.print(" " + TAG_CLASS + "=\"" + val.getClass().getName() + "\"");
				XmlPropertyConverter converter = (XmlPropertyConverter) converters.get(val.getClass());
				try {
					pw.print(" " + TAG_VALUE + "=\"" + converter.formatValue(val) + "\"");
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				pw.println("/>");
			}
			
			pw.println("</" + TAG_OPTIONS + ">");
			
			pw.close();
		}
	}

	public static String defineOption(String optionName, Object defaultValue) {
		if(allOptions.contains(optionName)) {
			throw new IllegalArgumentException("Option defined twice");
		}
		Option option = new Option(optionName, defaultValue);
		allOptions.add(option);
		return optionName;
	}

	private static Map getAccessors() {
		Map map = new HashMap();
		map.put(String.class, XmlPropertyConverterString.instance());
		map.put(Integer.class, XmlPropertyConverterInteger.instance());
		map.put(Boolean.class, XmlPropertyConverterBoolean.instance());
		map.put(Double.class, XmlPropertyConverterDouble.instance());
		map.put(Color.class, XmlPropertyConverterColor.instance());
		return map;
	}

	protected void addAccessor(Class optionClass, XmlPropertyConverter converter) {
		converters.put(optionClass, converter);
	}

	protected static boolean checkSystemProperty(String property, String value) {
		try {
			String s = System.getProperty(property);
			return s.equals(value);
		} catch(Exception ex) {
		}
		return false;
	}

	public void addToRecentFiles(File file) {
		List fileList = getListOption(PROPERTY_BASE_RECENT_FILE);

		String path = file.getAbsolutePath();
		int idx = fileList.indexOf(path);
		if(idx >= 0) {
			fileList.remove(idx);
		}

		fileList.add(0, path);
		if(fileList.size() > MAX_RECENT_FILES) {
			fileList = fileList.subList(0, MAX_RECENT_FILES);
		}

		setOption(PROPERTY_BASE_RECENT_FILE, fileList);
	}

	//--------------------------------------------------------

	static class Option {

		public final String optionName;

		public final Object defaultValue;

		public Option(String optionName, Object defaultValue) {
			this.optionName = optionName;
			this.defaultValue = defaultValue;
		}

	}

	//--------------------------------------------------------

	class ListSuffixComparator implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			String key1 = (String) arg0;
			String key2 = (String) arg1;

			int i = key1.lastIndexOf('.');
			int idx1 = Integer.parseInt(key1.substring(i + 1));
			i = key2.lastIndexOf('.');
			int idx2 = Integer.parseInt(key2.substring(i + 1));

			return idx1 - idx2;
		}

	}

}
